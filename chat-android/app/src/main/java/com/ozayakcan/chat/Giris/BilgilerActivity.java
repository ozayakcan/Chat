package com.ozayakcan.chat.Giris;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.yalantis.ucrop.UCrop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class BilgilerActivity extends AppCompatActivity {

    private CircleImageView profilResmi, kamera;
    private EditText isimET, hakkimdaET;
    private TextView isimHata;
    private Button bitirBtn;
    private Resimler resimler;
    private Veritabani veritabani;
    private SharedPreference sharedPreference;
    private Izinler izinler;
    FirebaseUser firebaseUser;
    private String resimBaglantisi = Veritabani.VarsayilanDeger;

    String profilResmiString, isimString, hakkimdaString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bilgiler);
        resimler = new Resimler(BilgilerActivity.this);
        izinler = new Izinler(BilgilerActivity.this);
        veritabani = new Veritabani(BilgilerActivity.this);
        sharedPreference = new SharedPreference(BilgilerActivity.this);

        profilResmi = findViewById(R.id.profilResmi);
        kamera = findViewById(R.id.kamera);
        isimET = findViewById(R.id.isimET);
        Intent intent = getIntent();
        profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);
        isimString = intent.getStringExtra(Veritabani.IsimKey);
        hakkimdaString = intent.getStringExtra(Veritabani.HakkimdaKey);
        isimHata = findViewById(R.id.isimHata);
        hakkimdaET = findViewById(R.id.hakkimdaET);
        if (isimString.equals("")){
            BilgileriGetir();
        }else{
            resimler.ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);
            resimBaglantisi = profilResmiString;
            isimET.setText(isimString);
            isimET.setSelection(isimET.getText().length());
            hakkimdaET.setText(hakkimdaString);
        }
        bitirBtn = findViewById(R.id.bitirBtn);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profilResmi.setOnClickListener(v -> ProfilResmiDegistir());
        kamera.setOnClickListener(v -> ProfilResmiDegistir());
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
    }

    private void BilgileriGetir() {
        DatabaseReference bilgilerReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        bilgilerReference.keepSynced(true);
        bilgilerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                if (kullanici != null){
                    resimler.ResimGoster(kullanici.getProfilResmi(), profilResmi, R.drawable.ic_profil_resmi);
                    resimBaglantisi = kullanici.getProfilResmi();
                    isimET.setText(kullanici.getIsim());
                    isimET.setSelection(isimET.getText().length());
                    hakkimdaET.setText(kullanici.getHakkimda());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ProfilResmiDegistir() {
        resimler.ProfilResmiDegistir(firebaseUser, resimBaglantisi, profilResmi, resimYukleActivityResult, kameraIzniResultLauncher, dosyaIzniResultLauncher);
    }

    ActivityResultLauncher<String> kameraIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    KameraIzniVerildi();
                }else{
                    KameraIzniVerilmedi();
                }
            });

    private void KameraIzniVerildi(){
        resimler.KameradanYukle(resimYukleActivityResult);
    }
    private void KameraIzniVerilmedi(){
        izinler.ZorunluIzinUyariKutusu(Manifest.permission.CAMERA, kameraIzniResultLauncher);
    }
    ActivityResultLauncher<String> dosyaIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    DosyaIzniVerildi();
                }else{
                    DosyaIzniVerilmedi();
                }
            });
    private void DosyaIzniVerildi(){
        resimler.GaleridenYukle(resimYukleActivityResult);
    }
    private void DosyaIzniVerilmedi(){
        izinler.ZorunluIzinUyariKutusu(Manifest.permission.READ_EXTERNAL_STORAGE, dosyaIzniResultLauncher);
    }
    ActivityResultLauncher<Intent> resimYukleActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null){
                        Uri sonuc = intent.getData();
                        resimler.ResimKirp(sonuc);
                    }else{
                        Toast.makeText(BilgilerActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null){
                Uri resimUri = UCrop.getOutput(data);
                resimler.ResimYukle(firebaseUser, resimUri, profilResmi, firebaseUser.getUid()+"/"+Veritabani.ProfilResmiDosyaAdi+Resimler.VarsayilanResimUzantisi);
            }
        }
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
                            Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString(), date.getTime(), true);
                            HashMap<String, Object> map = veritabani.KayitHashMap(kullaniciEkle, true);
                            databaseReference.updateChildren(map);
                        } catch (ParseException e) {
                            Log.d("Tarih Alınamadı", e.getMessage());
                            Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString(), true);
                            HashMap<String, Object> map = veritabani.KayitHashMap(kullaniciEkle, true);
                            databaseReference.updateChildren(map);
                        }
                    }else{
                        Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString(), true);
                        HashMap<String, Object> map = veritabani.KayitHashMap(kullaniciEkle, false);
                        databaseReference.updateChildren(map);
                    }
                    if(izinler.KontrolEt(Manifest.permission.READ_CONTACTS)){
                        veritabani.KisileriEkle(firebaseUser);
                    }
                    sharedPreference.KaydetBoolean(SharedPreference.kullaniciKaydedildi, true);
                    overridePendingTransition(0,0);
                    startActivity(new Intent(BilgilerActivity.this, MainActivity.class));
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