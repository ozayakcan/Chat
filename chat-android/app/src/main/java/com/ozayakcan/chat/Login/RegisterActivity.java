package com.ozayakcan.chat.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Utils.ChatApp;
import com.ozayakcan.chat.Utils.SharedPreference;
import com.ozayakcan.chat.Utils.Veritabani;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView profilResmi, kamera;
    private EditText isimET, hakkimdaET;
    private TextView isimHata;
    private Button bitirBtn;
    private ChatApp chatApp;
    private SharedPreference sharedPreference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //** Çoğu Activity'de çağırılacak
        //chatApp = new ChatApp(this);
        //chatApp.Init();
        //**
        sharedPreference = new SharedPreference(RegisterActivity.this);
        profilResmi = findViewById(R.id.profilResmi);
        kamera = findViewById(R.id.kamera);
        isimET = findViewById(R.id.isimET);
        isimHata = findViewById(R.id.isimHata);
        hakkimdaET = findViewById(R.id.hakkimdaET);
        bitirBtn = findViewById(R.id.bitirBtn);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        isimET.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_NEXT){
                hakkimdaET.requestFocus();
            }
            return false;
        });
        isimET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isimHata.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        hakkimdaET.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Kaydet();
            }
            return false;
        });
        bitirBtn.setOnClickListener(v -> Kaydet());

        profilResmi.setOnClickListener(v -> ProfilResmiDegistir());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Database Kullanıcı", snapshot.toString());
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                if(kullanici == null){
                    return;
                }
                isimET.setText(kullanici.getIsim());
                hakkimdaET.setText(kullanici.getHakkimda());
                if (!kullanici.getIsim().isEmpty()){
                    isimET.setSelection(isimET.getText().length());
                }
                if (!kullanici.getProfilResmi().equals(Veritabani.VarsayilanDeger)){
                    Picasso.get().load(kullanici.getProfilResmi()).into(profilResmi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void Kaydet(){
        String isim = isimET.getText().toString();
        if (isim.isEmpty()){
            isimHata.setVisibility(View.VISIBLE);
        }else{
            bitirBtn.setEnabled(false);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
            databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("Database Kullanıcı Kaydet", snapshot.toString());
                    Kullanici kullanici = snapshot.getValue(Kullanici.class);
                    if(kullanici == null){
                        Date date = new Date();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Veritabani.TarihSaatFormati);
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Veritabani.TarihSaatFormati);
                        try {
                            date = simpleDateFormat.parse(sdf.format(new Date()));
                            Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString(), date.getTime());
                            databaseReference.setValue(kullaniciEkle);
                        } catch (ParseException e) {
                            Log.d("Tarih Alınamadı", e.getMessage());
                            Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString());
                            databaseReference.setValue(kullaniciEkle);
                        }
                    }else{
                        Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString());
                        databaseReference.setValue(kullaniciEkle);
                    }
                    sharedPreference.KaydetBoolean(SharedPreference.kullaniciKaydedildi, true);
                    overridePendingTransition(0,0);
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Veritabanı Hatası", error.getMessage());
                    Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    bitirBtn.setEnabled(true);
                }
            });
        }
    }

    private void ProfilResmiDegistir() {
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return false;
        }
        else{
            return super.onKeyDown(keyCode, event);
        }
    }
}