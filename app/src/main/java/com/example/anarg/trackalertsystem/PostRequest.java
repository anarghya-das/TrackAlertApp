package com.example.anarg.trackalertsystem;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cunoraz.gifview.library.GifView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This Async Task class is related to the Main Activity class which handles the network request
 * and updates the UI based on the response.
 * @author Anarghya Das
 */
public class PostRequest extends AsyncTask<String, Void, String> {
    private BackEnd backEnd;
    private ArrayList<String> value;
    private MediaPlayer mediaPlayer;
    private GifView gifView;
    private TextView textView;
    private boolean pause;
    private boolean danger;
    AsyncResponse response;
    private ThreadControl threadControl;
    private ArrayList<String> dangerTracks;
    /**
     * Constructor which initialises most of the instance variables
     * @param s Stores the track name
     * @param mp Stores the siren sound mp3
     * @param gifView Stores the siren gif image
     * @param pause stores pause value of the sound
     * @param textView stores the text on the main screen
     * @param threadControl stores the reference to thread control class
     * @param response stores the reference for async response interface
     */
    PostRequest(ArrayList<String> s, MediaPlayer mp, GifView gifView, boolean pause, TextView textView, ThreadControl threadControl,AsyncResponse response) {
        backEnd = new BackEnd();
        value = s;
        mediaPlayer = mp;
        this.gifView = gifView;
        this.pause = pause;
        danger = false;
        this.textView = textView;
        this.threadControl = threadControl;
        this.response=response;
        dangerTracks=new ArrayList<>();
    }
    /**
     * The network connections are done here in background
     * @param strings urls of the severs to be connected
     * @return response from the server
     */
    @Override
    protected String doInBackground(String... strings) {
        try {
            boolean t = true;
            while (t) {
                threadControl.waitIfPaused();
                //Stop work if control is cancelled.
                if (threadControl.isCancelled()) {
                    break;
                }
                String res = post(strings[0], "asd");
                ArrayList<Train> trains = backEnd.jsonParse(res);
                if (trains==null){
                    throw new Exception();
                }else if (trains.size() != 0) {
                    ArrayList<String> trackNames = backEnd.trackNames(trains);
                    Log.d("TrackValue", trackNames.toString());
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
            return null;
        }
        return "normal";
    }
    /**
     * Updates the UI based on the server response
     * @param result server response
     */
    @Override
    protected void onPostExecute(String result) {
        if (result==null){
            response.processFinish("null");
        }else {
            Log.d("loadingTime", "loadingDONe ");
            response.processFinish("good");
            if (danger) {
                gifView.setVisibility(View.VISIBLE);
                gifView.play();
                if (dangerTracks.size()==1){
                    textView.setText("TRAIN INCOMING ON TRACK: "+dangerTracks.get(0));
                }else {
                    StringBuilder danger= new StringBuilder();
                    for (int i=0;i<dangerTracks.size();i++){
                        if (i==dangerTracks.size()-1){
                            danger.append(dangerTracks.get(i)).append(".");
                        }else {
                            danger.append(dangerTracks.get(i)).append(",");
                        }
                    }
                    textView.setText("TRAIN INCOMING ON TRACKS: " + danger);
                }
            } else {
                gifView.setVisibility(View.INVISIBLE);
                gifView.pause();
                textView.setText("No Train enroute on this Track");
                dangerTracks.clear();
            }
        }
    }
    /**
     * Helper method which checks if the particular track name is in the array list of track Names
     * @param s track name
     * @param tracks array list of track Names
     * @return true if track name is in the array list else false
     */
    private boolean checkTrack(ArrayList<String> s, ArrayList<String> tracks) {
        boolean finalVal=false;
        for (int i=0;i<s.size();i++) {
            String track=s.get(i).trim();
            for (String t : tracks) {
                if (t.equals(track)) {
                    dangerTracks.add(track);
                    finalVal = true;
                }
            }
        }
        return finalVal;
    }
    /**
     * Method to set Up HTTP POST Request
     * @param u URl
     * @param json JSON Data to be posted
     * @return response
     * @throws IOException throws an exception if not executed properly
     */
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
    /**
     * Converts the input stream object into String
     * @param is input stream object
     * @return String
     */
    private String convertInputStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}