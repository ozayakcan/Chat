package com.ozayakcan.chat.Ayarlar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.ChatApp;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Resim.ResimlerClass;

import de.hdodenhof.circleimageview.CircleImageView;

public class AyarlarActivity extends KullaniciAppCompatActivity {

    private RelativeLayout profilLayout;
    private CircleImageView profilResmi;
    private TextView isim, hakkimda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());

        profilLayout = findViewById(R.id.profilLayout);
        profilResmi = findViewById(R.id.profilResmi);
        isim = findViewById(R.id.isim);
        hakkimda = findViewById(R.id.hakkimda);
        BilgileriGoruntule();
        ChatApp.registerBroadcastReceiver(verileriGuncelle, Veritabani.IsimKey);
        ChatApp.registerBroadcastReceiver(verileriGuncelle, Veritabani.HakkimdaKey);
        ChatApp.registerBroadcastReceiver(verileriGuncelle, Veritabani.ProfilResmiKey);
        LinearLayout bildirimLayout = findViewById(R.id.bildirimLayout);
        bildirimLayout.setOnClickListener(v -> AyariAc(new Intent(AyarlarActivity.this, BildirimActivity.class)));

    }

    private void AyariAc(Intent intent){
        startActivity(intent);
        overridePendingTransition(R.anim.sagdan_sola_giris,R.anim.sagdan_sola_cikis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatApp.unregisterBroadcastReceiver(verileriGuncelle);
    }

    private void BilgileriGoruntule() {
        DatabaseReference kisiBilgileri = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        kisiBilgileri.keepSynced(true);
        kisiBilgileri.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                if (kullanici == null){
                    return;
                }
                isim.setText(kullanici.getIsim());
                hakkimda.setText(kullanici.getHakkimda());
                ResimlerClass.getInstance(AyarlarActivity.this).ResimGoster(kullanici.getProfilResmi(), profilResmi, R.drawable.ic_profil_resmi);
                profilLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(AyarlarActivity.this, ProfilActivity.class);
                    intent.putExtra(Veritabani.IsimKey, kullanici.getIsim());
                    intent.putExtra(Veritabani.HakkimdaKey, kullanici.getHakkimda());
                    intent.putExtra(Veritabani.TelefonKey, kullanici.getTelefon());
                    intent.putExtra(Veritabani.ProfilResmiKey, kullanici.getProfilResmi());
                    startActivity(intent);
                    overridePendingTransition(0,0);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    BroadcastReceiver verileriGuncelle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Veritabani.IsimKey)){
                if (intent.getStringExtra(Veritabani.IsimKey) != null && !intent.getStringExtra(Veritabani.IsimKey).equals("")){
                    isim.setText(intent.getStringExtra(Veritabani.IsimKey));
                }
            }else if (intent.getAction().equals(Veritabani.HakkimdaKey)){
                if (intent.getStringExtra(Veritabani.HakkimdaKey) != null && !intent.getStringExtra(Veritabani.HakkimdaKey).equals("")){
                    isim.setText(intent.getStringExtra(Veritabani.HakkimdaKey));
                }
            }else if (intent.getAction().equals(Veritabani.ProfilResmiKey)){
                if (intent.getStringExtra(Veritabani.ProfilResmiKey) != null && !intent.getStringExtra(Veritabani.ProfilResmiKey).equals("")){
                    ResimlerClass.getInstance(AyarlarActivity.this).ResimGoster(intent.getStringExtra(Veritabani.ProfilResmiKey), profilResmi, R.drawable.ic_profil_resmi);
                }
            }
        }
    };

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