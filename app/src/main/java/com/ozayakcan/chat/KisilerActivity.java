package com.ozayakcan.chat;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.widget.Toolbar;

import com.ozayakcan.chat.Fragment.KisilerFragment;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;

public class KisilerActivity extends KullaniciAppCompatActivity {

    KisilerFragment kisilerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kisiler);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());

        kisilerFragment = new KisilerFragment(KisilerActivity.this);
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.kisilerFragmentContainer, kisilerFragment, null).commit();
    }
    public void MesajGoster(String id, String isim, String telefon, String profilResmi){
        startActivity(ChatApp.MesajIntent(KisilerActivity.this, id, isim, telefon, profilResmi));
        overridePendingTransition(R.anim.sagdan_sola_giris, R.anim.sagdan_sola_cikis);
        finish();
    }

    private void Geri() {
        finish();
        overridePendingTransition(R.anim.yukaridan_asagi_giris, R.anim.yukaridan_asagi_cikis);
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