package com.ozayakcan.chat.Resimler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

public class ProfilResmiGoruntuleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_resmi_goruntule);
        Intent intent = getIntent();
        String profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);
        String isimString = intent.getStringExtra(Veritabani.IsimKey);
        RelativeLayout profilResmiLayout = findViewById(R.id.profilResmiLayout);
        int ekranGenisligi = getResources().getDisplayMetrics().widthPixels;
        double boyut = ekranGenisligi / 1.5;
        profilResmiLayout.getLayoutParams().width = (int) Math.round(boyut);
        profilResmiLayout.getLayoutParams().height = (int) Math.round(boyut);
        profilResmiLayout.requestLayout();
        ImageView profilResmi = findViewById(R.id.profilResmi);
        TextView baslik = findViewById(R.id.baslik);
        baslik.setText(isimString);
        ResimlerClass resimlerClass = new ResimlerClass(this);
        if (!profilResmiString.equals(Veritabani.VarsayilanDeger)){
            resimlerClass.ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);
        }
    }
}