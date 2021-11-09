package com.ozayakcan.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozayakcan.chat.Fragment.KisilerFragment;
import com.ozayakcan.chat.Fragment.MesajlarFragment;
import com.ozayakcan.chat.Fragment.VPAdapter;
import com.ozayakcan.chat.Ozellik.Veritabani;

import java.util.Objects;

public class ArsivActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    MesajlarFragment mesajlarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arsiv);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> Geri());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.archive_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        viewPager = findViewById(R.id.viewPager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        mesajlarFragment = new MesajlarFragment(ArsivActivity.this);
        vpAdapter.fragmentEkle(mesajlarFragment, getString(R.string.messages));
        viewPager.setAdapter(vpAdapter);
    }

    public void MesajBasiliTut(String id, String isim, String telefon, String profilResmi, int index){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ArsivActivity.this, R.style.AltMenuTema);
        View altMenuView = LayoutInflater.from(ArsivActivity.this).inflate(R.layout.layout_mesaj_islevleri, findViewById(R.id.altMenuLayout));
        TextView mesajiArsivle =  altMenuView.findViewById(R.id.mesajiArsivle);
        mesajiArsivle.setText(R.string.unarchive);
        altMenuView.findViewById(R.id.mesajiGoruntule).setOnClickListener(v -> {
            MesajGoster(id, isim, telefon, profilResmi);
            bottomSheetDialog.dismiss();
        });
        altMenuView.findViewById(R.id.mesajiArsivle).setOnClickListener(v -> {
            mesajlarFragment.MesajlariArsivle(telefon, index, false);
            bottomSheetDialog.dismiss();
        });
        altMenuView.findViewById(R.id.mesajiSil).setOnClickListener(v -> {
            mesajlarFragment.MesajlariSil(telefon, index, true);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(altMenuView);
        bottomSheetDialog.show();
    }

    public void MesajGoster(String id, String isim, String telefon, String profilResmi){
        Intent intent = new Intent(ArsivActivity.this, MesajActivity.class);
        intent.putExtra(Veritabani.IDKey, id);
        intent.putExtra(Veritabani.IsimKey, isim);
        intent.putExtra(Veritabani.TelefonKey, telefon);
        intent.putExtra(Veritabani.ProfilResmiKey, profilResmi);
        intent.putExtra(Veritabani.MesajTablosu, Veritabani.ArsivTablosu);
        startActivity(intent);
        overridePendingTransition(R.anim.sagdan_sola_giris, R.anim.sagdan_sola_cikis);
        finish();
    }

    private void Geri() {
        startActivity(new Intent(ArsivActivity.this, MainActivity.class));
        overridePendingTransition(0,0);
        finish();
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
}