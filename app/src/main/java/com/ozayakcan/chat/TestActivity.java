package com.ozayakcan.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;

public class TestActivity extends KullaniciAppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        View testActivity = findViewById(R.id.testActivity);
        testActivity.post(() -> klavyePopup.Baslat());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        klavyePopup.Durdur();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void KlavyeYuksekligiDegisti(int yukseklik) {
        TextView textView = findViewById(R.id.textView);
        textView.setText("Yükseklik: "+ yukseklik);
        View view = findViewById(R.id.klavye);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
        params.height = yukseklik;
        view.setLayoutParams(params);
        super.KlavyeYuksekligiDegisti(yukseklik);
    }
}