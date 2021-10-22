package com.ozayakcan.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Login.LoginActivity;
import com.ozayakcan.chat.Login.RegisterActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Utils.SharedPreference;
import com.ozayakcan.chat.Utils.Veritabani;

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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(user.getPhoneNumber());
                databaseReference.keepSynced(true);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Kullanici kullanici = snapshot.getValue(Kullanici.class);
                        if (kullanici == null){
                            startActivity(new Intent(SSActivity.this, RegisterActivity.class));
                            overridePendingTransition(0,0);
                            finish();
                        }else{
                            startActivity(new Intent(SSActivity.this, MainActivity.class));
                            overridePendingTransition(0,0);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Veritabanı", error.getMessage());
                        Toast.makeText(SSActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
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