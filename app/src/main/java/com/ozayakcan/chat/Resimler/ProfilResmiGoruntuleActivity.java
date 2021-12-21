package com.ozayakcan.chat.Resimler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ozayakcan.chat.Ozellik.KullaniciActivity;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

public class ProfilResmiGoruntuleActivity extends KullaniciActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_resmi_goruntule);
        ImageView profilResmi = findViewById(R.id.profilResmi);
        TextView baslik = findViewById(R.id.baslik);
        Intent intent = getIntent();
        String profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);
        String isimString = intent.getStringExtra(Veritabani.IsimKey);
        if(isimString.equals("")){
            baslik.setVisibility(View.GONE);
        }
        RelativeLayout profilResmiLayout = findViewById(R.id.profilResmiLayout);
        int ekranGenisligi = getResources().getDisplayMetrics().widthPixels;
        double boyut = ekranGenisligi / 1.5;
        profilResmiLayout.getLayoutParams().width = (int) Math.round(boyut);
        profilResmiLayout.getLayoutParams().height = (int) Math.round(boyut);
        profilResmiLayout.requestLayout();
        baslik.setText(isimString);
        if (!profilResmiString.equals(Veritabani.VarsayilanDeger)){
            ResimlerClass.getInstance(ProfilResmiGoruntuleActivity.this).ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);
        }
    }
}