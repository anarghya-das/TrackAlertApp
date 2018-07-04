package com.example.anarg.trackalertsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cunoraz.gifview.library.GifView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncResponse{
    private static final String govURl = "http://tms.affineit.com:4445/SignalAhead/Json/SignalAhead";
    private MediaPlayer mediaPlayer;
    private PostRequest govPost;
    private String value;
    private boolean pause,error;
    private int errorFrequency;
    private GifView gifView;
    private TextView textView;
    private FloatingActionButton audioButton;
    private AlertDialog dialog;
    private ArrayList<PostRequest> allRequests;
    private ThreadControl threadControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
        pause=false;
        error=false;
        errorFrequency=0;
        allRequests=new ArrayList<>();
        audioButton= findViewById(R.id.soundButton);
        textView=findViewById(R.id.textView);
        gifView= findViewById(R.id.gif1);
        gifView.setGifResource(R.drawable.siren);
        Intent i= getIntent();
        value=i.getStringExtra("value");
        mediaPlayer=MediaPlayer.create(this,R.raw.music);
        mediaPlayer.setLooping(true);
        threadControl=new ThreadControl();
        govPost=new PostRequest(value,mediaPlayer,gifView,pause,textView,threadControl,this);
        govPost.execute(govURl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(timerTask);
    }

    @Override
    public void processFinish(String output) {
        if (output.equals("null")) {
            if (dialog == null) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                error=true;
                gifView.setVisibility(View.INVISIBLE);
                exceptionRaised("Connection Error", "Please wait while we try to reconnect." +
                        "\nIn the mean while check if your internet connection is working.", false);
            } else if (!dialog.isShowing()) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                error=true;
                gifView.setVisibility(View.INVISIBLE);
                exceptionRaised("Connection Error", "Please wait while we try to reconnect." +
                        "\nIn the mean while check if your internet connection is working.", false);
            }else if (errorFrequency==60000){
                dialog.dismiss();
                exceptionRaised("Connection Error", "Could not reconnect." +
                        "\nThere might be some problem, please try again later!", true);
            }
        }else if (dialog!=null&&dialog.isShowing()&&output.equals("good")){
            error=false;
            errorFrequency=0;
            dialog.dismiss();
            if (audioButton.getTag().equals("noaudio")) {
                pause = true;
            }else if (audioButton.getTag().equals("audio")){
                pause =false;
            }
        }
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
                govPost= new PostRequest(value,mediaPlayer,gifView,pause,textView,threadControl,MainActivity.this);
                govPost.execute(govURl);
                allRequests.add(govPost);
            }
            if (error){
                errorFrequency++;
            }
            mHandler.postDelayed(timerTask, 1);
        }};

    public void soundChange(View view) {
        if (audioButton.getTag().equals("audio")){
            pause=true;
            audioButton.setTag("noaudio");
            audioButton.setImageResource(R.drawable.noaudio);
        }else if (audioButton.getTag().equals("noaudio")){
            pause=false;
            audioButton.setTag("audio");
            audioButton.setImageResource(R.drawable.audio);
        }
    }

    public void stop(View view) {
        finish();
    }

    public void exceptionRaised(String title,String body,boolean buttons) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(body)
                .setTitle(title);
        if (buttons) {
            builder.setNegativeButton("Restart", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent i = getIntent();
                    startActivity(i);
                }
            });
            builder.setPositiveButton("Main Menu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


}
