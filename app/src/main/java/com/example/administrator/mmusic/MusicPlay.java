package com.example.administrator.mmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MusicPlay extends AppCompatActivity implements ServiceConnection {
    private ImageView cover_image_view;
    private Button play_button;
    private SeekBar seekBar;
    private boolean isChanging;
    private Intent intent_service = null;
    private MusicService.MyBinder binder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        init();
    }

    void init(){
        cover_image_view = (ImageView)findViewById(R.id.cover_image);
        play_button = (Button)findViewById(R.id.play_button);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new MySeekbar());

        intent_service = new Intent(MusicPlay.this,MusicService.class);
        bindService(intent_service, MusicPlay.this, Context.BIND_AUTO_CREATE);
//        startService(intent_service);
        if(binder.isPlay()){
            play_button.setText("stop");
        }else {
            play_button.setText("start");
        }
    }

    class MySeekbar implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            isChanging=true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
//            player.seekTo(seekbar.getProgress());
            isChanging=false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        binder = (MusicService.MyBinder) iBinder;
    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
