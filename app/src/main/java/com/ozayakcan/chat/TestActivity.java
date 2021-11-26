package com.ozayakcan.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.ozayakcan.chat.Ozellik.KullaniciActivity;

public class TestActivity extends KullaniciActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TextView testTW = findViewById(R.id.testTW);
    }
}