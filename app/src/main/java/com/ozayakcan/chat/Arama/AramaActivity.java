package com.ozayakcan.chat.Arama;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Bildirimler.BildirimClass;
import com.ozayakcan.chat.ChatApp;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Resimler.ResimlerClass;

public class AramaActivity extends KullaniciAppCompatActivity {

    private ImageView profilResmi, hoparlaruAcKapat, kamerayiAcKapat,
            sesiAcKapat, aramayiSonlandir, yanitlaBtn, reddetBtn;
    private LinearLayout gelenArama, altKisim;
    private TextView kisiAdi;


    String telefonString, tokenString;

    private boolean hoparlorAcik = false;
    private boolean KameraGoster = false;
    private boolean kameraAcik = false;
    private boolean sesAcik = true;

    private boolean Arayan = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arama);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        kisiAdi = findViewById(R.id.kisiAdi);

        String idString = getIntent().getStringExtra(Veritabani.IDKey);
        tokenString = getIntent().getStringExtra(Veritabani.IDKey);
        telefonString = getIntent().getStringExtra(Veritabani.TelefonKey);
        ChatApp.AramaKisisiniAyarla(telefonString);
        String isimString = getIntent().getStringExtra(Veritabani.IsimKey);

        String profilResmiString = getIntent().getStringExtra(Veritabani.ProfilResmiKey);
        KameraGoster = getIntent().getBooleanExtra(Veritabani.KameraKey, false);
        Arayan = getIntent().getBooleanExtra(Veritabani.AramaKey, true);
        profilResmi = findViewById(R.id.profilResmi);
        ResimlerClass.getInstance(AramaActivity.this).ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);

        String goruntulenecekIsim = isimString.equals("") ? telefonString : isimString;
        IsimAyarla(goruntulenecekIsim);
        KisiAdiniGuncelle();

        gelenArama = findViewById(R.id.gelenArama);
        altKisim = findViewById(R.id.altKisim);

        gelenArama.setVisibility(Arayan ? View.GONE : View.VISIBLE);
        altKisim.setVisibility(Arayan ? View.VISIBLE: View.GONE);

        yanitlaBtn = findViewById(R.id.yanitlaBtn);
        reddetBtn = findViewById(R.id.reddetBtn);
        yanitlaBtn.setOnClickListener(v -> Yanitla());
        reddetBtn.setOnClickListener(v -> AramayiSonlandir(true));

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
            AramayiSonlandir(true);
        });
        if (Arayan){
            BildirimClass.AramaBildirimYolla(tokenString, firebaseUser.getPhoneNumber(), BildirimClass.AramaKey, KameraGoster ? "1":"0", new BildirimClass.BildirimListener() {
                @Override
                public void Gonderildi() {
                    //Arama Sesi
                }

                @Override
                public void Gonderilmedi() {
                    Toast.makeText(AramaActivity.this, getString(R.string.call_failed), Toast.LENGTH_SHORT).show();
                    AramayiSonlandir(false);
                }
            });

        }else{
            //Çalma sesi
        }
    }
    private void IsimAyarla(String isim){
        kisiAdi.setText(Arayan ?
                getString(R.string.s_dialing).replace("%s", isim)
                : getString(R.string.s_calling).replace("%s", isim));
    }
    private void KisiAdiniGuncelle() {
        DatabaseReference kisiAdiGuncelle = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.KisiTablosu).child(telefonString);
        kisiAdiGuncelle.keepSynced(true);
        kisiAdiGuncelle.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                if (kullanici != null){
                    IsimAyarla(kullanici.getIsim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        ChatApp.registerBroadcastReceiver(aramaBroadcasReceiver, BildirimClass.YanitlaKey);
        ChatApp.registerBroadcastReceiver(aramaBroadcasReceiver, BildirimClass.ReddetKey);
        ChatApp.registerBroadcastReceiver(aramaBroadcasReceiver, BildirimClass.MesgulKey);
        ChatApp.registerBroadcastReceiver(aramaBroadcasReceiver, BildirimClass.AramaKey);
        ChatApp.AramaKisisiniAyarla(telefonString);
        super.onStart();
    }

    @Override
    protected void onStop() {
        ChatApp.unregisterBroadcastReceiver(aramaBroadcasReceiver);
        ChatApp.AramaKisisiniAyarla("");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ChatApp.AramaKisisiniAyarla("");
        super.onDestroy();
    }

    private final BroadcastReceiver aramaBroadcasReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BildirimClass.YanitlaKey)){
                if (intent.getStringExtra(BildirimClass.KisiKey).equals(telefonString)){
                    AramaYanitlandi();
                }
            } else if(intent.getAction().equals(BildirimClass.ReddetKey)){
                if (intent.getStringExtra(BildirimClass.KisiKey).equals(telefonString)){
                    AramayiSonlandir(false);
                }
            } else if(intent.getAction().equals(BildirimClass.MesgulKey)){
                if (intent.getStringExtra(BildirimClass.KisiKey).equals(telefonString)){
                    Toast.makeText(AramaActivity.this, getString(R.string.busy), Toast.LENGTH_SHORT).show();
                    AramayiSonlandir(false);
                }
            }
        }
    };

    private void Yanitla() {
        AramaYanitlandi();
        BildirimClass.AramaBildirimYolla(tokenString, firebaseUser.getPhoneNumber(), BildirimClass.YanitlaKey, KameraGoster ? "1" : "0", new BildirimClass.BildirimListener() {
            @Override
            public void Gonderildi() {

            }

            @Override
            public void Gonderilmedi() {

            }
        });
    }

    private void AramaYanitlandi() {
        gelenArama.setVisibility(View.GONE);
        altKisim.setVisibility(View.VISIBLE);
        altKisim.setAlpha(0.0f);
        gelenArama.animate()
                .alpha(1.0f)
                .setListener(null);
    }

    private void AramayiSonlandir(boolean bildirimGonder) {
        if (bildirimGonder){
            BildirimClass.AramaBildirimYolla(tokenString, firebaseUser.getPhoneNumber(), BildirimClass.ReddetKey, KameraGoster ? "1" : "0", new BildirimClass.BildirimListener() {
                @Override
                public void Gonderildi() {
                    finish();
                    Geri();
                }

                @Override
                public void Gonderilmedi() {
                    finish();
                    Geri();
                }
            });
        }else{
            finish();
            Geri();
        }
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