package com.example.administrator.mmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MusicPlay extends AppCompatActivity {
    private ImageView cover_image_view;
    private Button play_button;
    private SeekBar seekBar;
    private boolean isChanging;
    private MusicService.MyBinder binder = null;
    Intent intent_from_main = null;
    private int position;
    public Handler seekbarhandler=null;
    public SeekbarThread seekbarthread=null;
    public String music_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        init();
    }

    void init(){
        intent_from_main = getIntent();
        Bundle bundle = intent_from_main.getBundleExtra("play");
        binder = (MusicService.MyBinder) bundle.getBinder("play");
        cover_image_view = (ImageView)findViewById(R.id.cover_image);
        play_button = (Button)findViewById(R.id.play_button);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
//        seekBar.setOnSeekBarChangeListener(new MySeekbar());
        if(binder.isPlay()){
            play_button.setText("stop");
        }else {
            play_button.setText("start");
        }

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(play_button.getText().toString().equals("play")){
                    binder.play();
                    play_button.setText("stop");
                }else {
                    binder.stop();
                    play_button.setText("play");
                }
            }
        });
        seekbarhandler = new MyHandler();
        seekbarthread = new SeekbarThread();
        seekbarthread.start();
    }
    //自定义进度条，设置进度条监听
    class MySeekbar implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            seekBar.setProgress(progress);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isChanging=true;
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
//            player.seekTo(seekbar.getProgress());
            isChanging=false;
        }
    }
    //更新进度条线程
    public class SeekbarThread extends Thread {
        @Override
        public void run(){
            //写成死循环持续更新
            while(true){
                Message message=new Message();
                message.what=1;
                //判断当前音乐是否切换，如果切换，发送更新seekbar的duration的Message
                if(music_name == null){
                    music_name = binder.getCurrentMusicName();
                }else {
                    if(!music_name.equals(binder.getCurrentMusicName())){
                        music_name = binder.getCurrentMusicName();
                        Message message01 = new Message();
                        message01.what = 2;
                        seekbarhandler.handleMessage(message01);
                    }
                }

                try{
                    Thread.sleep(500);
//                    Log.d("test thread:" ,"in seekbar thread");
                    seekbarhandler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    //更新进度条的Handler
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //更新当前进度
            if(msg.what==1){
                int progress = binder.getCurrentProgess();
                seekBar.setProgress(progress);
            }
            //根据当前音乐看是否更新seekbar的duration
            if(msg.what==2){
                int duration = binder.getDuration();
                seekBar.setMax(duration);
            }
        }
    }

}
