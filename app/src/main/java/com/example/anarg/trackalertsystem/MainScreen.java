package com.example.anarg.trackalertsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 This class controls the welcome screen of the app.
 @author Anarghya Das
 */
public class MainScreen extends AppCompatActivity {
    //Stores the track name as user input
    private TextInputEditText textInputEditText;
    /**
     * The first function which runs after the activity has started
     * Initializes the the instance variables declared above
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        if(connectivityCheck()) {
            textInputEditText = findViewById(R.id.text);
        }else{
            exceptionRaised("Connectivity Error","Enable mobile data or WiFi to use this app.");
        }
    }
    /**
     * Checks if the user is connected to Wifi or mobile data or not
     * @return true is connected and false otherwise
     */
    private boolean connectivityCheck(){
        boolean result=false;
        ConnectivityManager connectivitymanager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivitymanager.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                if (netInfo.isConnected()) {
                    result=true;
                }
            }
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (netInfo.isConnected()) {
                    result=true;
                }
            }
        }
        return result;
    }
    /**
     * OnClick listener for start button which starts the Main Activity of the app
     */
    public void start(View view) {
        String input=textInputEditText.getText().toString();
        if (input.charAt(input.length()-1)=='.'){
            input=input.substring(0,input.length()-1);
        }
        Log.d("InputTest", input);
        boolean output=inputCheck(input);
        if (!input.isEmpty()&&connectivityCheck()&&output){
            Intent i= new Intent(this,MainActivity.class);
            i.putExtra("value",input);
            startActivity(i);
        }
        else if (textInputEditText.getText().toString().isEmpty()){
            textInputEditText.setError("Enter a valid Track Name!");
        }else if (!output){
            textInputEditText.setError("Enter , to separate multiple Track Names!");
        }
        else if (!connectivityCheck()){
            exceptionRaised("Connectivity Error","Enable mobile data or WiFi to use this app.");
        }
    }
    private boolean inputCheck(String input){
        if (input.split(",").length>1){
            return true;
        }else if (input.split(".").length>1||input.split(" ").length>1
                ||input.split(";").length>1){
            return false;
        }else{
            return true;
        }
    }
    /**
     * Method which creates a custom dialog box to show if the program encountered an error
     */
    public void exceptionRaised(String title,String body) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(body)
                .setTitle(title);
        builder.setNegativeButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

}
