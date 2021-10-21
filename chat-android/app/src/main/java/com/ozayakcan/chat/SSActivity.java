package com.ozayakcan.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozayakcan.chat.Login.LoginActivity;
import com.ozayakcan.chat.Login.RegisterActivity;
import com.ozayakcan.chat.Utils.SharedPreference;

public class SSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ss);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            //Giriş Yapıldı
            if (SharedPreference.getInstance(this).GetirBoolean(SharedPreference.kullaniciKaydedildi, false)){
                //Kaydedildi
            }else{
                //Kaydedilmedi
                startActivity(new Intent(SSActivity.this, RegisterActivity.class));
                overridePendingTransition(0,0);
                finish();
            }
        }else{
            //Giriş yapılmadı
            startActivity(new Intent(SSActivity.this, LoginActivity.class));
            overridePendingTransition(0,0);
            finish();
        }
    }
}