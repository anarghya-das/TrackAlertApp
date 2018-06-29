package com.example.anarg.trackalertsystem;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainScreen extends AppCompatActivity {
    private TextInputEditText textInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        textInputEditText=findViewById(R.id.text);
    }

    public void start(View view) {
        if (!textInputEditText.getText().toString().isEmpty()){
            Intent i= new Intent(this,MainActivity.class);
            i.putExtra("value",textInputEditText.getText().toString());
            startActivity(i);
        }
    }
}
