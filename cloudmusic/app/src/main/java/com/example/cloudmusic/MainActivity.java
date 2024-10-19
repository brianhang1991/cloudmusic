package com.example.cloudmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cloudmusic.activity.MusicPlayActivity;
import com.example.cloudmusic.contant.ServerConstant;
import com.example.cloudmusic.http.HttpExecutor;
import com.example.cloudmusic.pojo.Music;
import com.example.cloudmusic.service.MusicService;
import com.example.cloudmusic.tools.JsonHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private ListView listViewMusic;
    private List<Music> musics;
    private List<String> musicNames = new ArrayList();
    private MusicService.PlayBinder binder;
    private MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            try {
                init();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // init activity
    void init() throws IllegalAccessException, InterruptedException {

        listViewMusic = findViewById(R.id.list_view_music);
//        getAllMusicFromLocal();
//        getAllMusicFromRaw();
        getAllMusicsFromOnline();

        Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
//        bindService(intent, MainActivity.this, Context.BIND_AUTO_CREATE);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        bindService(intent, MainActivity.this, Context.BIND_ADJUST_WITH_ACTIVITY);

        listViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String path = getPathByName(musicNames.get(i));

//                binder.setData(path);
                binder.setOnlinePath(path);
//                binder.setAllMusic(musics);

                startService(serviceIntent);

                Intent activityIntent = new Intent(MainActivity.this, MusicPlayActivity.class);

                Bundle bundle = new Bundle();
                bundle.putBinder("play",binder);
                activityIntent.putExtra("play",bundle);

                startActivity(activityIntent);
            }
        });
    }

    // get all musics from backend
    void getAllMusicsFromOnline() throws InterruptedException {

        // get musics info from backend
        GetMusicsFromOnlineThread httpGetThread = new GetMusicsFromOnlineThread();

        httpGetThread.start();
        httpGetThread.join();

        for (Music music : musics) {
            musicNames.add(music.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,musicNames);
        listViewMusic.setAdapter(adapter);
    }

    class GetMusicsFromOnlineThread extends Thread{

        @Override
        public void run() {
            musics = JsonHelper.getArrayFromString(HttpExecutor.httpGetString(ServerConstant.URL_LIST_MUSICS.getDesc()), Music.class);
        }
    }

    void getAllMusicFromLocal(){
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        musics = new ArrayList<Music>();
        if(cursor.moveToFirst()){
            do{
                Music pojo = new Music();
                pojo.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                pojo.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                musicNames.add(pojo.getName());
                musics.add(pojo);
            }while (cursor.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,musicNames);
        listViewMusic.setAdapter(adapter);
    }

    void getAllMusicFromRaw() throws IllegalAccessException {

        Resources resources = getResources();
        Field[] fields = R.raw.class.getFields();
        musics = new ArrayList<Music>();

        int resourceId = getResources().getIdentifier("raw/test01", "raw", getPackageName());


        for (Field field : fields) {

            Music pojo = new Music();
            String name = getResources().getResourceEntryName(field.getInt(field));
            int resId = getResources().getIdentifier("raw/" + name, "raw", getPackageName());

            pojo.setName(name);
            pojo.setResId(resId);
            musicNames.add(name);
            musics.add(pojo);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,musicNames);
        listViewMusic.setAdapter(adapter);
    }

    String getPathByName(String name){
        for(Music music : musics){
            if(name.equals(music.getName())){
                return music.getPath();
            }
        }
        return null;
    }

    int getResIdByName(String name){
        for(Music pojo : musics){
            if(name.equals(pojo.getName())){
                return pojo.getResId();
            }
        }
        return 0;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            binder = (MusicService.PlayBinder) iBinder;
            musicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

}
