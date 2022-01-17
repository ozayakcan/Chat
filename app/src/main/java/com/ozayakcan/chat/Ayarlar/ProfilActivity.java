package com.ozayakcan.chat.Ayarlar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ozayakcan.chat.MesajActivity;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.Metinler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Resim.ResimlerClass;
import com.yalantis.ucrop.UCrop;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends KullaniciAppCompatActivity {

    private RelativeLayout profilResmiLayout, isimLayout, hakkimdaLayout;
    private CircleImageView profilResmi, kamera;
    private TextView isimText, hakkimdaText, telefonText;
    private LinearLayout progressBarLayout;

    private String profilResmiString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());

        profilResmiLayout = findViewById(R.id.profilResmiLayout);
        profilResmi = findViewById(R.id.profilResmi);
        kamera = findViewById(R.id.kamera);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        isimText = findViewById(R.id.isimText);
        hakkimdaText = findViewById(R.id.hakkimdaText);
        telefonText = findViewById(R.id.telefonText);
        Intent intent = getIntent();
        profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);

        profilResmi.setOnClickListener(v -> ResimlerClass.getInstance(ProfilActivity.this).ProfilResmiGoruntule("", profilResmiString));
        kamera.setOnClickListener(v -> ResimlerClass.getInstance(ProfilActivity.this).ProfilResmiDegistir(firebaseUser, profilResmiString, profilResmi, resimYukleActivityResult, kameraIzniResultLauncher, dosyaIzniResultLauncher));

        String isimString = intent.getStringExtra(Veritabani.IsimKey);
        isimText.setText(isimString);
        String hakkimdaString = intent.getStringExtra(Veritabani.HakkimdaKey);
        hakkimdaText.setText(hakkimdaString);
        String telefonString = intent.getStringExtra(Veritabani.TelefonKey);
        telefonText.setText(telefonString);
        ResimlerClass.getInstance(ProfilActivity.this).ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);

        BilgiDegistirPenceresiTanimlari();

        isimLayout = findViewById(R.id.isimLayout);
        isimLayout.setOnClickListener(v -> BilgiDegistir(DEGISTIRILECEK_ISIM));
        hakkimdaLayout = findViewById(R.id.hakkimdaLayout);
        hakkimdaLayout.setOnClickListener(v -> BilgiDegistir(DEGISTIRILECEK_HAKKIMDA));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        klavyePopup.Durdur();
    }

    private BottomSheetDialog acilirMenuDialog;
    private View acilirmenu;
    private EditText acilirMenuEditText;
    private View klavyeView;
    private ProgressBar acilirMenuProgressBar;
    private TextInputLayout acilirMenuTextInputLayout;
    private void BilgiDegistirPenceresiTanimlari() {
        acilirMenuDialog = new BottomSheetDialog(ProfilActivity.this, R.style.AltMenuTema);
        acilirmenu = LayoutInflater.from(ProfilActivity.this).inflate(R.layout.layout_metin_duzenle, (LinearLayout) findViewById(R.id.altMenuLayout));
        View view = acilirmenu.findViewById(R.id.altMenuLayout);
        view.post(() -> klavyePopup.Baslat());
        acilirMenuTextInputLayout = acilirmenu.findViewById(R.id.acilirMenuTextInputLayout);
        acilirMenuEditText = acilirmenu.findViewById(R.id.editText);
        klavyeView = acilirmenu.findViewById(R.id.klavye);
        acilirMenuProgressBar = acilirmenu.findViewById(R.id.progressBar);
        acilirMenuDialog.setContentView(acilirmenu);
    }

    @Override
    public void KlavyeYuksekligiDegisti(int yukseklik) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)klavyeView.getLayoutParams();
        params.height = yukseklik;
        klavyeView.setLayoutParams(params);
        super.KlavyeYuksekligiDegisti(yukseklik);
    }

    private final int DEGISTIRILECEK_ISIM = 0;
    private final int DEGISTIRILECEK_HAKKIMDA = 1;

    private void BilgiDegistir(int degistirilecekBilgi) {
        if (degistirilecekBilgi == DEGISTIRILECEK_ISIM){
            acilirMenuEditText.setText(isimText.getText().toString());
            acilirMenuEditText.setHint(getString(R.string.name));
            acilirMenuEditText.setMaxLines(1);
            acilirMenuEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.maxKarakterIsim))});
            acilirMenuTextInputLayout.setCounterMaxLength(getResources().getInteger(R.integer.maxKarakterIsim));
        }else if(degistirilecekBilgi == DEGISTIRILECEK_HAKKIMDA){
            acilirMenuEditText.setText(hakkimdaText.getText().toString());
            acilirMenuEditText.setHint(getString(R.string.about_me));
            acilirMenuEditText.setMaxLines(5);
            acilirMenuEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.maxKarakterHakkimda))});
            acilirMenuTextInputLayout.setCounterMaxLength(getResources().getInteger(R.integer.maxKarakterHakkimda));
        }
        acilirMenuEditText.setSelection(acilirMenuEditText.getText().length());
        acilirMenuEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Kaydet(degistirilecekBilgi);
            }
            return false;
        });
        TextView iptalBtn = acilirmenu.findViewById(R.id.iptalBtn);
        TextView kaydetBtn = acilirmenu.findViewById(R.id.kaydetBtn);
        iptalBtn.setOnClickListener(v -> {
            Metinler.getInstance(getApplicationContext()).KlavyeKapat(acilirMenuEditText);
            acilirMenuDialog.dismiss();
        });
        kaydetBtn.setOnClickListener(v -> {
            Kaydet(degistirilecekBilgi);
        });
        acilirMenuDialog.show();
        Metinler.getInstance(getApplicationContext()).KlavyeAc(acilirMenuEditText);
        acilirMenuEditText.selectAll();
    }

    private void Kaydet(int degistirilecekBilgi) {
        if (acilirMenuEditText.getText().toString().equals("")){
            Toast.makeText(ProfilActivity.this, getString(R.string.cannot_be_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        acilirMenuProgressBar.setVisibility(View.VISIBLE);
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
        hashMap.put(veri, acilirMenuEditText.getText().toString());
        String finalVeri = veri;
        databaseReference.updateChildren(hashMap, (error, ref) -> {
            Intent guncelle = new Intent(finalVeri);
            guncelle.putExtra(finalVeri, acilirMenuEditText.getText().toString());
            LocalBroadcastManager.getInstance(ProfilActivity.this).sendBroadcast(guncelle);
            acilirMenuProgressBar.setVisibility(View.GONE);
            Metinler.getInstance(getApplicationContext()).KlavyeKapat(acilirMenuEditText);
            acilirMenuDialog.dismiss();
            if (degistirilecekBilgi == DEGISTIRILECEK_ISIM){
                isimText.setText(acilirMenuEditText.getText().toString());
            }else{
                hakkimdaText.setText(acilirMenuEditText.getText().toString());
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
        ResimlerClass.getInstance(ProfilActivity.this).KameradanYukle(resimYukleActivityResult);
    }
    private void KameraIzniVerilmedi(){
        Izinler.getInstance(ProfilActivity.this).ZorunluIzinUyariKutusu(Manifest.permission.CAMERA, kameraIzniResultLauncher);
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
        ResimlerClass.getInstance(ProfilActivity.this).GaleridenYukle(resimYukleActivityResult);
    }
    private void DosyaIzniVerilmedi(){
        Izinler.getInstance(ProfilActivity.this).ZorunluIzinUyariKutusu(Manifest.permission.READ_EXTERNAL_STORAGE, dosyaIzniResultLauncher);
    }
    ActivityResultLauncher<Intent> resimYukleActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null){
                        Uri sonuc = intent.getData();
                        ResimlerClass.getInstance(ProfilActivity.this).ResimKirp(sonuc);
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
                ResimlerClass.getInstance(ProfilActivity.this).ResimYukle(resimUri, firebaseUser.getUid() + "/" + Veritabani.ProfilResmiDosyaAdi + ResimlerClass.VarsayilanResimUzantisi, progressBarLayout, new ResimlerClass.ResimYukleSonuc() {
                    @Override
                    public void Basarili(String resimUrl) {
                        profilResmiString = resimUrl;
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.ProfilResmiKey);
                        reference.keepSynced(true);
                        reference.setValue(profilResmiString);
                        if (profilResmi != null){
                            ResimlerClass.getInstance(ProfilActivity.this).ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);
                        }
                        Intent guncelle = new Intent(Veritabani.ProfilResmiKey);
                        guncelle.putExtra(Veritabani.ProfilResmiKey, resimUrl);
                        LocalBroadcastManager.getInstance(ProfilActivity.this).sendBroadcast(guncelle);

                    }

                    @Override
                    public void Basarisiz(String hata) {
                        Log.e("Resim", hata);
                        Toast.makeText(ProfilActivity.this, getString(R.string.could_not_update_profile_photo), Toast.LENGTH_SHORT).show();
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