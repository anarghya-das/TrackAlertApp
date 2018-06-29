package com.example.anarg.trackalertsystem;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PostRequest extends AsyncTask<String, Void, Void> {
    private BackEnd backEnd;
    private String value;
    private MediaPlayer mediaPlayer;
    private GifImageView gif;
    private TextView textView;
    private boolean pause;
    private boolean danger;
    private ThreadControl threadControl;

    PostRequest(String s, MediaPlayer mp, GifImageView gif, boolean pause, TextView textView, ThreadControl threadControl) {
        backEnd = new BackEnd();
        value = s;
        mediaPlayer = mp;
        this.gif = gif;
        this.pause = pause;
        danger = false;
        this.textView = textView;
        this.threadControl = threadControl;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            boolean t = true;
            while (t) {
                threadControl.waitIfPaused();
                //Stop work if control is cancelled.
                if (threadControl.isCancelled()) {
                    break;
                }
                String res = post(strings[0], "asd");
                ArrayList<Train> trains = backEnd.jsonGov(res);
                if (trains.size() != 0) {
                    ArrayList<String> trackNames = backEnd.trackNames(trains);
                    Log.d("sound", trackNames.toString());
                    boolean result = checkTrack(value, trackNames);
                    if (result) {
                        danger = true;
                        if (!pause) {
                            if (!mediaPlayer.isPlaying()) {
                                mediaPlayer.start();
                            }
                        }
                        if (mediaPlayer.isPlaying() && pause) {
                            mediaPlayer.pause();
                        }
                    } else {
                        if (mediaPlayer.isPlaying() && !pause) {
                            danger = false;
                            mediaPlayer.stop();
                        }
                    }
                }
                t = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (danger) {
            gif.setVisibility(View.VISIBLE);
            textView.setText("TRAIN INCOMING!");
        } else {
            gif.setVisibility(View.INVISIBLE);
            textView.setText("No Train enroute on this Track");
        }
    }

    private boolean checkTrack(String s, ArrayList<String> trains) {
        for (String t : trains) {
            if (t.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private String post(String u, String json) throws IOException {
        String response;
        // This is getting the url from the string we passed in
        URL url = new URL(u);

        // Create the urlConnection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        urlConnection.setRequestProperty("Content-Type", "application/json");

        urlConnection.setRequestMethod("POST");


        // OPTIONAL - Sets an authorization header
        urlConnection.setRequestProperty("Authorization", "someAuthString");

        // Send the post body
        if (json != null && !json.isEmpty()) {
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(json);
            writer.flush();
        }

        int statusCode = urlConnection.getResponseCode();


        if (statusCode == 200) {

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            response = convertInputStreamToString(inputStream);
            if (response == null || response.isEmpty()) {
                throw new IOException();
            }

        }
        // From here you can convert the string to JSON with whatever JSON parser you like to use
        // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
        else {
            // Status code is not 200
            // Do something to handle the error
            throw new IOException();
        }

        return response;
    }

    private String convertInputStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}