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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private ListView list_view_music;
    private List<MusicPOJO> list_music_pojo;
    private List<String> list_name = new ArrayList<String>();
    private MusicService.MyBinder binder = null;
    Intent intent_service = null;
    String path = "";
    int resId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            init();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    void init() throws IllegalAccessException {
        list_view_music = (ListView) findViewById(R.id.list_view_music);
//        getAllMusicFromLocal();
        getAllMusicFromRaw();
        intent_service = new Intent(MainActivity.this, MusicService.class);
        bindService(intent_service, MainActivity.this, Context.BIND_AUTO_CREATE);

        list_view_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                path = getPathByName(list_name.get(i));
                resId = getResIdByName(list_name.get(i));

                binder.setData(resId);
                binder.setAllMusic(list_music_pojo);
                startService(intent_service);
                Intent intent = new Intent(MainActivity.this, MusicPlay.class);
                Bundle bundle = new Bundle();
                bundle.putBinder("play",binder);
                intent.putExtra("play",bundle);
                startActivity(intent);
            }
        });
    }

    void getAllMusicFromLocal(){
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        list_music_pojo = new ArrayList<MusicPOJO>();
        if(cursor.moveToFirst()){
            do{
                MusicPOJO pojo = new MusicPOJO();
                pojo.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                pojo.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                list_name.add(pojo.getName());
                list_music_pojo.add(pojo);
            }while (cursor.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list_name);
        list_view_music.setAdapter(adapter);
    }

    void getAllMusicFromRaw() throws IllegalAccessException {

        Resources resources = getResources();
        Field[] fields = R.raw.class.getFields();
        list_music_pojo = new ArrayList<MusicPOJO>();

        int resourceId = getResources().getIdentifier("raw/test01", "raw", getPackageName());


        for (Field field : fields) {

            MusicPOJO pojo = new MusicPOJO();
            String name = getResources().getResourceEntryName(field.getInt(field));
            int resId = getResources().getIdentifier("raw/" + name, "raw", getPackageName());

            pojo.setName(name);
            pojo.setResId(resId);
            list_name.add(name);
            list_music_pojo.add(pojo);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list_name);
        list_view_music.setAdapter(adapter);
    }

    String getPathByName(String name){
        for(MusicPOJO pojo : list_music_pojo){
            if(name.equals(pojo.getName())){
                return pojo.getPath();
            }
        }
        return null;
    }

    int getResIdByName(String name){
        for(MusicPOJO pojo : list_music_pojo){
            if(name.equals(pojo.getName())){
                return pojo.getResId();
            }
        }
        return 0;
    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        binder = (MusicService.MyBinder) iBinder;
    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
