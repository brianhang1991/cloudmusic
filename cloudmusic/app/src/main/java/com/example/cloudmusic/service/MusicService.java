package com.example.cloudmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.cloudmusic.contant.ServerConstant;
import com.example.cloudmusic.pojo.Music;

import java.util.List;

public class MusicService extends Service {

    private final int LAST_FLAG = 0;
    private final int NEXT_FLAG = 1;

    public MediaPlayer mediaPlayer = new MediaPlayer();
    public PlayBinder binder = new PlayBinder();

    List<?> musicList;
    String path;
    int resId;
    String currentMusicName;

    public MusicService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // start play
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

            mediaPlayer.setDataSource(ServerConstant.URL_LOAD_ONLINE_MUSIC.getDesc() + "/?path=" + path);

            currentMusicName = path;

            mediaPlayer.prepareAsync();
            mediaPlayer.start();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer arg0) {
//                    playRandom();
//                }
//            });
        }catch (Exception e){
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // binder for play music
    public class PlayBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
        public void setData(String data){
            MusicService.this.path = data;
        }
        public void setOnlinePath(String path) {
            MusicService.this.path = path;
        }
        public void setData(int data){
            MusicService.this.resId = data;
        }
        public void setAllMusic(List<?> musicList){
            MusicService.this.musicList = musicList;
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
        public void setProgess(int progess) {
            mediaPlayer.seekTo(progess);
        }
        public int getDuration(){
            return mediaPlayer.getDuration();
        }
        public String getCurrentMusicName(){
            return currentMusicName;
        }
        public void playNext(){
            switchMusic(NEXT_FLAG);
        }
        public void playLast(){
            switchMusic(LAST_FLAG);
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

    // get music path under random
    String randomPlay(){
        int musicPosition = (int)(Math.random() * musicList.size());
        return ((Music)(musicList.get(musicPosition))).getPath();
    }

    public void playRandom(){
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

            currentMusicName = randomPlay();
//            mediaPlayer = MediaPlayer.create(this, resId);
//            mediaPlayer.setDataSource(currentMusicName);
            mediaPlayer.setDataSource(ServerConstant.URL_LOAD_ONLINE_MUSIC.getDesc());

            mediaPlayer.prepare();

            mediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void switchMusic(int direction){
        if(direction == LAST_FLAG){
            playRandom();
        }else {
            playRandom();
        }
    }
}
