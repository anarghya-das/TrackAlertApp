package com.example.anarg.trackalertsystem;

import org.json.JSONException;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String govURl = "http://tms.affineit.com:4445/SignalAhead/Json/SignalAhead";

    @Test
    public void BackEndTest() throws JSONException {
        BackEnd b=new BackEnd();
        ArrayList<Train> t=b.jsonGov(postagain(govURl,"sdsd"));
        System.out.println(t.size());
        int c=0;
        for (Train to: t){
            System.out.println(to.getTrackName());
            c++;
        }
        System.out.println(c);
    }

    private String postagain(String s,String p){
        String response;
        try {
            // This is getting the url from the string we passed in
            URL url = new URL(s);

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.setRequestMethod("POST");


            // Send the post body
            if (p != null&&!p.isEmpty()) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(p);
                writer.flush();
            }

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                response = convertInputStreamToString(inputStream);

            }
            //Close our InputStream and Buffered reader
            // From here you can convert the string to JSON with whatever JSON parser you like to use
            // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
            else {
                // Status code is not 200
                // Do something to handle the error
                throw new Exception();
            }

        } catch (Exception e) {
            return null;
        }
        return response;
    }
    static String convertInputStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}