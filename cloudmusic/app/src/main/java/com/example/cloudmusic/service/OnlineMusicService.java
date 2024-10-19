package com.example.cloudmusic.service;

import com.example.cloudmusic.contant.ServerConstant;

public class OnlineMusicService extends MusicService{
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mediaPlayer.setDataSource(ServerConstant.URL_LOAD_ONLINE_MUSIC.getDesc());
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
