package com.ozayakcan.chat;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TextView testTW = findViewById(R.id.testTW);
        testTW.setText("AuthToken: "+ ChatApp.getE3KitKullanici().authToken+"\nVirgil Token: "+ChatApp.getE3KitKullanici().virgilToken+"\nE3: "+ChatApp.getE3KitKullanici().eThree.toString());
    }
}