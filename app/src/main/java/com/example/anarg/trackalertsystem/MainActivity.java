package com.example.anarg.trackalertsystem;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String govURl = "http://tms.affineit.com:4445/SignalAhead/Json/SignalAhead";
    private MediaPlayer mediaPlayer;
    private PostRequest govPost;
    private String value;
    private boolean pause;
    private GifImageView gif;
    private TextView textView;
    private ArrayList<PostRequest> allRequests;
    private ThreadControl threadControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pause=false;
        allRequests=new ArrayList<>();
        textView=findViewById(R.id.textView);
        gif=findViewById(R.id.gifView);
        gif.setGifImageResource(R.drawable.siren);
        Intent i= getIntent();
        value=i.getStringExtra("value");
        mediaPlayer=MediaPlayer.create(this,R.raw.music);
        mediaPlayer.setLooping(true);
        threadControl=new ThreadControl();
        govPost=new PostRequest(value,mediaPlayer,gif,pause,textView,threadControl);
        govPost.execute(govURl);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(timerTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("sound", "onDestroy: ");
        mHandler.removeCallbacks(timerTask);
        mediaPlayer.stop();
        for (PostRequest p: allRequests){
            p.cancel(true);
            threadControl.cancel();
        }
    }

    private Handler mHandler = new Handler();
    private Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (govPost.getStatus()== AsyncTask.Status.FINISHED) {
                govPost= new PostRequest(value,mediaPlayer,gif,pause,textView,threadControl);
                govPost.execute(govURl);
                allRequests.add(govPost);
            }
            mHandler.postDelayed(timerTask, 1);
        }};

    public void soundChange(View view) {
        FloatingActionButton button= findViewById(R.id.soundButton);
        if (button.getTag().equals("audio")){
            pause=true;
            button.setTag("noaudio");
            button.setImageResource(R.drawable.noaudio);
        }else if (button.getTag().equals("noaudio")){
            pause=false;
            button.setTag("audio");
            button.setImageResource(R.drawable.audio);
        }
    }

    public void stop(View view) {
        finish();
    }
}
