package com.ozayakcan.chat.Ayarlar;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Ozellik.KullaniciActivity;
import com.ozayakcan.chat.R;

public class AyarlarActivity extends KullaniciActivity {

    private Toolbar toolbar;

    private TextView baslik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());
        baslik = findViewById(R.id.baslik);
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