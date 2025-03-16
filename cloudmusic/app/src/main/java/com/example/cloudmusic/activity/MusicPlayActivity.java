package com.example.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cloudmusic.R;
import com.example.cloudmusic.service.MusicService;

public class MusicPlayActivity extends AppCompatActivity {

    private ImageView coverImageView;
    private ImageButton playButton;
    private ImageButton lastButton;
    private ImageButton nextButton;
    private SeekBar seekBar;
    private boolean isChanging;
    private MusicService.PlayBinder binder = null;
    Intent intentFromMain = null;
    private int position;
    public Handler seekbarhandler=null;
    public SeekbarThread seekbarthread=null;
    public String musicName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        init();
    }

    // init activity
    void init(){

        intentFromMain = getIntent();

        Bundle bundle = intentFromMain.getBundleExtra("play");
        binder = (MusicService.PlayBinder) bundle.getBinder("play");

        coverImageView = findViewById(R.id.cover_image);
        playButton = findViewById(R.id.play_button);
        lastButton = findViewById(R.id.last_button);
        nextButton = findViewById(R.id.next_button);
        seekBar = findViewById(R.id.seekbar);

        playButton.setBackgroundResource(R.mipmap.stop);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binder.isPlay()){
                    binder.play();
                    playButton.setBackgroundResource(R.mipmap.stop);
                }else {
                    binder.stop();
                    playButton.setBackgroundResource(R.mipmap.play);
                }
            }
        });

        lastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binder.playLast();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binder.playLast();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                isChanging=false;
                Message message = new Message();
                message.what = 3;
                seekbarhandler.sendMessage(message);
            }
        });

        seekbarhandler = new MyHandler();
        seekbarthread = new SeekbarThread();
        seekbarthread.start();
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
                if(musicName == null){
                    musicName = binder.getCurrentMusicName();
                }else {
                    if(!musicName.equals(binder.getCurrentMusicName())){
                        musicName = binder.getCurrentMusicName();
                        Message message01 = new Message();
                        message01.what = 2;
                        seekbarhandler.sendMessage(message01);
                    }
                }

                try{
                    Thread.sleep(500);
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

            switch (msg.what){
                case 1:
                    int progress = binder.getCurrentProgess();
                    seekBar.setProgress(progress);
                    break;
                case 2:
                    int duration = binder.getDuration();
                    seekBar.setMax(duration);
                    break;
                case 3:
                    binder.setProgess(seekBar.getProgress());
                    break;
                default:
            }
        }
    }
}
