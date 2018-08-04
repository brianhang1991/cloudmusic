package com.example.administrator.mmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private ListView list_view_music;
    private List<MusicPOJO> list_music_pojo;
    private List<String> list_name = new ArrayList<String>();
    private MusicService.MyBinder binder = null;
    Intent intent_service = null;
    String path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    void init(){
        list_view_music = (ListView) findViewById(R.id.list_view_music);
        getAllMusic();
        intent_service = new Intent(MainActivity.this,MusicService.class);
        bindService(intent_service, MainActivity.this, Context.BIND_AUTO_CREATE);

        list_view_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                path = getPathByName(list_name.get(i));
                binder.setData(path);
                binder.setAllMusic(list_music_pojo);
                startService(intent_service);
                Intent intent = new Intent(MainActivity.this,MusicPlay.class);
                Bundle bundle = new Bundle();
                bundle.putBinder("play",binder);
                intent.putExtra("play",bundle);
                startActivity(intent);
            }
        });
    }

    void getAllMusic(){
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

    String getPathByName(String name){
        for(MusicPOJO pojo : list_music_pojo){
            if(name.equals(pojo.getName())){
                return pojo.getPath();
            }
        }
        return null;
    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        binder = (MusicService.MyBinder) iBinder;
    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
