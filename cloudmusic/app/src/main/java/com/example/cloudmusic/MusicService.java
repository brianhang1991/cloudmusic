package com.example.cloudmusic;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.cloudmusic.contant.ServerConstant;

import java.util.List;

public class MusicService extends Service {
    private final int LAST_FLAG = 0;
    private final int NEXT_FLAG = 1;
    public MediaPlayer mediaPlayer = new MediaPlayer();;
    public MyBinder binder = new MyBinder();
    List<?> music_list;
    String path;
    int resId;
    String record_path;
    String current_music_name;
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
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
//            mediaPlayer = MediaPlayer.create(this, resId);
//            mediaPlayer.setDataSource(path);
            mediaPlayer.setDataSource(ServerConstant.URL_LOAD_ONLINE_MUSIC.getDesc());

            mediaPlayer.prepare();

            mediaPlayer.setLooping(true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //startService启动方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
//            mediaPlayer = MediaPlayer.create(this, resId);
//            mediaPlayer.setDataSource(path);


            mediaPlayer.setDataSource(ServerConstant.URL_LOAD_ONLINE_MUSIC.getDesc());

            current_music_name = path;
            mediaPlayer.prepare();

            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    play_with_random();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    //Binder类的自定义
    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
        public void setData(String data){
            MusicService.this.path = data;
        }
        public void setData(int data){
            MusicService.this.resId = data;
        }
        public void setAllMusic(List<?> music_list){
            MusicService.this.music_list = music_list;
        }
        public boolean isPlay(){
            return mediaPlayer.isPlaying();
        }
        public void stop(){
            mediaPlayer.pause();
        }
        public void play(){
            mediaPlayer.start();
        }
        public int getCurrentProgess(){
            return mediaPlayer.getCurrentPosition();
        }
        public int getDuration(){
            return mediaPlayer.getDuration();
        }
        public String getCurrentMusicName(){
            return current_music_name;
        }
        public void playNext(){
            switch_music(NEXT_FLAG);
        }
        public void playLast(){
            switch_music(LAST_FLAG);
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

    //获取随机播放列表中的音乐路径
    String randomPlay(){
        int music_position = (int)(Math.random() * music_list.size());
        return ((MusicPOJO)(music_list.get(music_position))).getPath();
    }

    public void play_with_random(){
        mediaPlayer.reset();
        try {

            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            current_music_name = randomPlay();
//            mediaPlayer = MediaPlayer.create(this, resId);
//            mediaPlayer.setDataSource(current_music_name);
            mediaPlayer.setDataSource(ServerConstant.URL_LOAD_ONLINE_MUSIC.getDesc());

            mediaPlayer.prepare();

            mediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void switch_music(int direction){
        if(direction == LAST_FLAG){
            play_with_random();
        }else {
            play_with_random();
        }
    }
}
