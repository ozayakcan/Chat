package com.ozayakcan.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TextView testTW = findViewById(R.id.testTW);
        Intent intent = getIntent();
        String veri = intent.getStringExtra("veri");
        testTW.setText(veri);
    }
}