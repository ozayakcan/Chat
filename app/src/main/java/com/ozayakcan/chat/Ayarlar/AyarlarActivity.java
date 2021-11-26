package com.ozayakcan.chat.Ayarlar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Giris.BilgilerActivity;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Resimler.ResimlerClass;
import com.yalantis.ucrop.UCrop;

import de.hdodenhof.circleimageview.CircleImageView;

public class AyarlarActivity extends KullaniciAppCompatActivity {

    private Toolbar toolbar;
    private RelativeLayout profilResmiLayout;
    private CircleImageView profilResmi, kamera;
    private Izinler izinler;
    private ResimlerClass resimlerClass;
    private LinearLayout progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());

        resimlerClass = new ResimlerClass(this);
        izinler = new Izinler(this);

        profilResmiLayout = findViewById(R.id.profilResmiLayout);
        profilResmi = findViewById(R.id.profilResmi);
        kamera = findViewById(R.id.kamera);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        KullaniciBilgileriniGetir();
    }

    private void KullaniciBilgileriniGetir() {
        DatabaseReference kisiBilgileri = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        kisiBilgileri.keepSynced(true);
        kisiBilgileri.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                if (kullanici == null){
                    return;
                }
                resimlerClass.ResimGoster(kullanici.getProfilResmi(), profilResmi, R.drawable.ic_profil_resmi);
                profilResmi.setOnClickListener(v -> resimlerClass.ProfilResmiGoruntule("", kullanici.getProfilResmi()));
                kamera.setOnClickListener(v -> resimlerClass.ProfilResmiDegistir(firebaseUser, kullanici.getProfilResmi(), profilResmi, resimYukleActivityResult, kameraIzniResultLauncher, dosyaIzniResultLauncher));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        resimlerClass.KameradanYukle(resimYukleActivityResult);
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
        resimlerClass.GaleridenYukle(resimYukleActivityResult);
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
                        resimlerClass.ResimKirp(sonuc);
                    }else{
                        Toast.makeText(AyarlarActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null){
                Uri resimUri = UCrop.getOutput(data);
                resimlerClass.ResimYukle(firebaseUser, resimUri, profilResmi, firebaseUser.getUid()+"/"+Veritabani.ProfilResmiDosyaAdi+ ResimlerClass.VarsayilanResimUzantisi, progressBarLayout);
            }
        }
    }

    private void Geri() {
        startActivity(new Intent(AyarlarActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.soldan_saga_giris, R.anim.soldan_saga_cikis);
    }

    @Override
    public void onBackPressed() {
        Geri();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Geri();
            return false;
        }
        else{
            return super.onKeyDown(keyCode, event);
        }
    }
}