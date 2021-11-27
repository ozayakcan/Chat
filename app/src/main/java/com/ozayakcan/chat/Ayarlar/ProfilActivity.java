package com.ozayakcan.chat.Ayarlar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Resimler.ResimlerClass;
import com.yalantis.ucrop.UCrop;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends KullaniciAppCompatActivity {

    private RelativeLayout profilResmiLayout, isimLayout, hakkimdaLayout;
    private CircleImageView profilResmi, kamera;
    private TextView isimText, hakkimdaText, telefonText;
    private Izinler izinler;
    private ResimlerClass resimlerClass;
    private LinearLayout progressBarLayout;

    private String profilResmiString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());
        resimlerClass = new ResimlerClass(this);
        izinler = new Izinler(this);

        profilResmiLayout = findViewById(R.id.profilResmiLayout);
        profilResmi = findViewById(R.id.profilResmi);
        kamera = findViewById(R.id.kamera);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        isimText = findViewById(R.id.isimText);
        hakkimdaText = findViewById(R.id.hakkimdaText);
        telefonText = findViewById(R.id.telefonText);
        Intent intent = getIntent();
        profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);

        profilResmi.setOnClickListener(v -> resimlerClass.ProfilResmiGoruntule("", profilResmiString));
        kamera.setOnClickListener(v -> resimlerClass.ProfilResmiDegistir(firebaseUser, profilResmiString, profilResmi, resimYukleActivityResult, kameraIzniResultLauncher, dosyaIzniResultLauncher));

        String isimString = intent.getStringExtra(Veritabani.IsimKey);
        isimText.setText(isimString);
        String hakkimdaString = intent.getStringExtra(Veritabani.HakkimdaKey);
        hakkimdaText.setText(hakkimdaString);
        String telefonString = intent.getStringExtra(Veritabani.TelefonKey);
        telefonText.setText(telefonString);
        resimlerClass.ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);

        isimLayout = findViewById(R.id.isimLayout);
        isimLayout.setOnClickListener(v -> BilgiDegistir(DEGISTIRILECEK_ISIM));
        hakkimdaLayout = findViewById(R.id.hakkimdaLayout);
        hakkimdaLayout.setOnClickListener(v -> BilgiDegistir(DEGISTIRILECEK_HAKKIMDA));
    }

    private final int DEGISTIRILECEK_ISIM = 0;
    private final int DEGISTIRILECEK_HAKKIMDA = 1;

    private void BilgiDegistir(int degistirilecekBilgi) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfilActivity.this, R.style.AltMenuTema);
        View altMenuView = LayoutInflater.from(ProfilActivity.this).inflate(R.layout.layout_metin_duzenle, (LinearLayout) findViewById(R.id.altMenuLayout));
        EditText editText = altMenuView.findViewById(R.id.editText);
        if (degistirilecekBilgi == DEGISTIRILECEK_ISIM){
            editText.setText(isimText.getText().toString());
            editText.setHint(getString(R.string.name));
            editText.setMaxLines(1);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else if(degistirilecekBilgi == DEGISTIRILECEK_HAKKIMDA){
            editText.setText(hakkimdaText.getText().toString());
            editText.setHint(getString(R.string.about_me));
            editText.setMaxLines(5);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        }
        ProgressBar progressBar = altMenuView.findViewById(R.id.progressBar);
        editText.setSelection(editText.getText().length());
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Kaydet(bottomSheetDialog, progressBar, editText, degistirilecekBilgi);
            }
            return false;
        });
        Button iptalBtn = altMenuView.findViewById(R.id.iptalBtn);
        Button kaydetBtn = altMenuView.findViewById(R.id.kaydetBtn);
        iptalBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());
        kaydetBtn.setOnClickListener(v -> {
            Kaydet(bottomSheetDialog, progressBar, editText, degistirilecekBilgi);
        });
        bottomSheetDialog.setContentView(altMenuView);
        bottomSheetDialog.show();
    }

    private void Kaydet(BottomSheetDialog bottomSheetDialog, ProgressBar progressBar, EditText editText, int degistirilecekBilgi) {
        if (editText.getText().toString().equals("")){
            Toast.makeText(ProfilActivity.this, getString(R.string.cannot_be_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        String veri = "";
        if (degistirilecekBilgi == DEGISTIRILECEK_ISIM){
            veri = Veritabani.IsimKey;
        }else if(degistirilecekBilgi == DEGISTIRILECEK_HAKKIMDA){
            veri = Veritabani.HakkimdaKey;
        }else{
            return;
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(veri, editText.getText().toString());
        String finalVeri = veri;
        databaseReference.updateChildren(hashMap, (error, ref) -> {
            Intent guncelle = new Intent(finalVeri);
            guncelle.putExtra(finalVeri, editText.getText().toString());
            LocalBroadcastManager.getInstance(ProfilActivity.this).sendBroadcast(guncelle);
            progressBar.setVisibility(View.GONE);
            bottomSheetDialog.dismiss();
            if (degistirilecekBilgi == DEGISTIRILECEK_ISIM){
                isimText.setText(editText.getText().toString());
            }else{
                hakkimdaText.setText(editText.getText().toString());
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
                        Toast.makeText(ProfilActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null){
                Uri resimUri = UCrop.getOutput(data);
                resimlerClass.ResimYukle(firebaseUser, resimUri, profilResmi, firebaseUser.getUid() + "/" + Veritabani.ProfilResmiDosyaAdi + ResimlerClass.VarsayilanResimUzantisi, progressBarLayout, new ResimlerClass.ResimYukleSonuc() {
                    @Override
                    public void Basarili(String resimUrl) {
                        profilResmiString = resimUrl;
                        Intent guncelle = new Intent(Veritabani.ProfilResmiKey);
                        guncelle.putExtra(Veritabani.ProfilResmiKey, resimUrl);
                        LocalBroadcastManager.getInstance(ProfilActivity.this).sendBroadcast(guncelle);
                    }

                    @Override
                    public void Basarisiz() {

                    }
                });
            }
        }
    }

    private void Geri() {
        supportFinishAfterTransition();
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