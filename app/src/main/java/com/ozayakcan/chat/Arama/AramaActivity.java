package com.ozayakcan.chat.Arama;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Resimler.ResimlerClass;

public class AramaActivity extends AppCompatActivity {

    private ImageView profilResmi, hoparlaruAcKapat, kamerayiAcKapat,
            sesiAcKapat, aramayiSonlandir, yanitlaBtn, reddetBtn;
    private LinearLayout gelenArama, altKisim;

    private boolean hoparlorAcik = false;
    private boolean kameraAcik = false;
    private boolean sesAcik = true;

    private boolean Arayan = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arama);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String idString = getIntent().getStringExtra(Veritabani.IDKey);
        String telefonString = getIntent().getStringExtra(Veritabani.TelefonKey);
        String isimString = getIntent().getStringExtra(Veritabani.IsimKey);
        String profilResmiString = getIntent().getStringExtra(Veritabani.ProfilResmiKey);
        boolean KameraGoster = getIntent().getBooleanExtra(Veritabani.KameraKey, false);
        Arayan = getIntent().getBooleanExtra(Veritabani.AramaKey, true);
        profilResmi = findViewById(R.id.profilResmi);
        ResimlerClass.getInstance(AramaActivity.this).ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);

        gelenArama = findViewById(R.id.gelenArama);
        altKisim = findViewById(R.id.altKisim);

        gelenArama.setVisibility(Arayan ? View.GONE : View.VISIBLE);
        altKisim.setVisibility(Arayan ? View.VISIBLE: View.GONE);

        yanitlaBtn = findViewById(R.id.yanitlaBtn);
        reddetBtn = findViewById(R.id.reddetBtn);
        yanitlaBtn.setOnClickListener(v -> {
            Yanitla();
        });
        reddetBtn.setOnClickListener(v -> {
            AramayiSonlandir();
        });

        hoparlaruAcKapat = findViewById(R.id.hoparlaruAcKapat);
        kamerayiAcKapat = findViewById(R.id.kamerayiAcKapat);
        sesiAcKapat = findViewById(R.id.sesiAcKapat);
        aramayiSonlandir = findViewById(R.id.aramayiSonlandir);
        kameraAcik = KameraGoster;
        if (KameraGoster){
            kamerayiAcKapat.setImageResource(R.drawable.ic_baseline_videocam_24);
        }else{
            kamerayiAcKapat.setImageResource(R.drawable.ic_baseline_videocam_off_disabled_24);
        }
        hoparlaruAcKapat.setOnClickListener(v -> {
            hoparlorAcik = !hoparlorAcik;
            if (hoparlorAcik){
                hoparlaruAcKapat.setBackground(ContextCompat.getDrawable(AramaActivity.this, R.drawable.oval_arkaplan_transparan));
            }else{
                hoparlaruAcKapat.setBackground(null);
            }
        });
        kamerayiAcKapat.setOnClickListener(v -> {
            if (KameraGoster){
                kameraAcik = !kameraAcik;
                if (kameraAcik){
                    kamerayiAcKapat.setImageResource(R.drawable.ic_baseline_videocam_24);
                }else {
                    kamerayiAcKapat.setImageResource(R.drawable.ic_baseline_videocam_off_24);
                }
            }
        });
        sesiAcKapat.setOnClickListener(v -> {
            sesAcik = !sesAcik;
            if (sesAcik){
                sesiAcKapat.setImageResource(R.drawable.ic_baseline_mic_24);
            }else {
                sesiAcKapat.setImageResource(R.drawable.ic_baseline_mic_off_24);
            }
        });
        aramayiSonlandir.setOnClickListener(v ->{
            AramayiSonlandir();
        });
    }

    private void Yanitla() {
        gelenArama.setVisibility(View.GONE);
        altKisim.setVisibility(View.VISIBLE);
        altKisim.setAlpha(0.0f);
        gelenArama.animate()
                .alpha(1.0f)
                .setListener(null);
    }

    private void AramayiSonlandir() {
        finish();
        Geri();
    }

    private void Geri(){
        if (Arayan){
            overridePendingTransition(R.anim.yukaridan_asagi_giris, R.anim.yukaridan_asagi_cikis);
        }else{
            overridePendingTransition(0,0);
        }
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