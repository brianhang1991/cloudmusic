package com.example.administrator.mmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class MusicService extends Service {
    public MusicService(){}
    public MediaPlayer mediaPlayer;
    public MyBinder binder = new MyBinder();
    public MusicService(String music_path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(music_path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }
}
