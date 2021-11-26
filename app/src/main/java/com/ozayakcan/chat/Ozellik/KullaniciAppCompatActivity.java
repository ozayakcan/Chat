package com.ozayakcan.chat.Ozellik;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class KullaniciAppCompatActivity extends AppCompatActivity {

    public FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Veritabani.DurumGuncelle(firebaseUser, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Veritabani.DurumGuncelle(firebaseUser, false);
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
}
