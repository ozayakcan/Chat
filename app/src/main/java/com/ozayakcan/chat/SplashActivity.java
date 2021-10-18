package com.ozayakcan.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozayakcan.chat.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            //Giriş yapıldı
        }else{
            //Giriş yapılmadı
            overridePendingTransition(0,0);
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}