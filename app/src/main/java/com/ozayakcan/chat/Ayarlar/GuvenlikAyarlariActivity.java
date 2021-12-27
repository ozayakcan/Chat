package com.ozayakcan.chat.Ayarlar;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.widget.Toolbar;

import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.R;

public class GuvenlikAyarlariActivity extends KullaniciAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guvenlik_ayarlari);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());
    }

    private void Geri(){
        finish();
        overridePendingTransition(R.anim.soldan_saga_giris, R.anim.soldan_saga_cikis);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}