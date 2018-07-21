package com.example.administrator.mmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {
    public MediaPlayer mediaPlayer = new MediaPlayer();;
    public MyBinder binder = new MyBinder();
    List<?> music_list;
    String path;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  new MyBinder();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);

        }catch (Exception e){
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
//            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(randomPlay());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
        public void setData(String data){
            MusicService.this.path = data;
        }
        public void setAllMusic(List<?> music_list){
            MusicService.this.music_list = music_list;
        }
        public boolean isPlay(){
            return mediaPlayer.isPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    String randomPlay(){
        int music_position = (int)(Math.random() * music_list.size());
        return ((MusicPOJO)(music_list.get(music_position))).getPath();
    }
}
