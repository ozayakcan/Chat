package com.ozayakcan.chat.Ozellik;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class KullaniciAppCompatActivity extends AppCompatActivity implements KlavyePopup.KlavyeListener {

    public FirebaseUser firebaseUser;
    public KlavyePopup klavyePopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        klavyePopup = new KlavyePopup(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Veritabani.DurumGuncelle(firebaseUser, true);
        klavyePopup.setKlavyeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Veritabani.DurumGuncelle(firebaseUser, false);
        klavyePopup.setKlavyeListener(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Veritabani.DurumGuncelle(firebaseUser, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Veritabani.DurumGuncelle(firebaseUser, false);
    }

    @Override
    public void KlavyeYuksekligiDegisti(int yukseklik) {

    }
}
