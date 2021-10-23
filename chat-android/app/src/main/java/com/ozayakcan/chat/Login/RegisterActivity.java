package com.ozayakcan.chat.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Utils.ChatApp;
import com.ozayakcan.chat.Utils.Izinler;
import com.ozayakcan.chat.Utils.Resimler;
import com.ozayakcan.chat.Utils.SharedPreference;
import com.ozayakcan.chat.Utils.Veritabani;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView profilResmi, kamera;
    private EditText isimET, hakkimdaET;
    private TextView isimHata;
    private Button bitirBtn;
    private ChatApp chatApp;
    private Resimler resimler;
    private Veritabani veritabani;
    private SharedPreference sharedPreference;
    private Izinler izinler;
    FirebaseUser firebaseUser;
    private String resimBaglantisi = Veritabani.VarsayilanDeger;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //** Çoğu Activity'de çağırılacak
        //chatApp = new ChatApp(this);
        //chatApp.Init();
        //**
        izinler = new Izinler(RegisterActivity.this);
        resimler = new Resimler(RegisterActivity.this);
        veritabani = new Veritabani(RegisterActivity.this);
        sharedPreference = new SharedPreference(RegisterActivity.this);
        profilResmi = findViewById(R.id.profilResmi);
        kamera = findViewById(R.id.kamera);
        isimET = findViewById(R.id.isimET);
        isimHata = findViewById(R.id.isimHata);
        hakkimdaET = findViewById(R.id.hakkimdaET);
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
                    resimBaglantisi = kullanici.getProfilResmi();
                    resimler.ResimGoster(resimBaglantisi, profilResmi, R.drawable.ic_profil_resmi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, getString(R.string.could_not_connect_to_database), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ProfilResmiDegistir() {
        resimler.ProfilResmiDegistir(firebaseUser, resimBaglantisi, profilResmi, resimYukleActivityResult);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Izinler.PROFIL_RESMI_KAMERA_IZIN_KODU){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                resimler.KameradanYukle(resimYukleActivityResult);
            }else{
                izinler.ZorunluIzinUyariKutusu(Manifest.permission.CAMERA, Izinler.PROFIL_RESMI_KAMERA_IZIN_KODU);
            }
        }else if(requestCode == Izinler.PROFIL_RESMI_DOSYA_IZIN_KODU){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                resimler.GaleridenYukle(resimYukleActivityResult);
            }else{
                izinler.ZorunluIzinUyariKutusu(Manifest.permission.READ_EXTERNAL_STORAGE, Izinler.PROFIL_RESMI_DOSYA_IZIN_KODU);
            }
        }
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
                        Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null){
                Uri resimUri = UCrop.getOutput(data);
                resimler.ResimYukle(firebaseUser, resimUri, profilResmi, firebaseUser.getUid()+"/"+Veritabani.ProfilResmiDosyaAdi+resimler.DosyaUzantisi(resimUri));
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
                            Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString(), date.getTime());
                            HashMap<String, Object> map = veritabani.KayitHashMap(kullaniciEkle, true);
                            databaseReference.updateChildren(map);
                        } catch (ParseException e) {
                            Log.d("Tarih Alınamadı", e.getMessage());
                            Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString());
                            HashMap<String, Object> map = veritabani.KayitHashMap(kullaniciEkle, true);
                            databaseReference.updateChildren(map);
                        }
                    }else{
                        Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), hakkimdaET.getText().toString());
                        HashMap<String, Object> map = veritabani.KayitHashMap(kullaniciEkle, false);
                        databaseReference.updateChildren(map);
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