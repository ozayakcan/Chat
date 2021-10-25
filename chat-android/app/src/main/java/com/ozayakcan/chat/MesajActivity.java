package com.ozayakcan.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.Ozellik.Veritabani;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajActivity extends AppCompatActivity {

    private CircleImageView profilResmi;
    private RecyclerView recyclerView;
    private TextView kisiBasHarfi, gonderText, isim, durum;

    private String idString;
    private String telefonString;
    private String isimString;
    private String profilResmiString;

    private Resimler resimler;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);
        resimler = new Resimler(MesajActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> Geri());

        Intent intent = getIntent();
        idString = intent.getStringExtra(Veritabani.IDKey);
        isimString = intent.getStringExtra(Veritabani.IsimKey);
        telefonString = intent.getStringExtra(Veritabani.TelefonKey);
        profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);

        profilResmi = findViewById(R.id.profilResmi);
        kisiBasHarfi = findViewById(R.id.kisiBasHarfi);
        isim = findViewById(R.id.isim);
        durum = findViewById(R.id.durum);
        resimler.ResimGoster(profilResmiString, profilResmi, R.drawable.varsayilan_arkaplan);
        if (profilResmiString.equals(Veritabani.VarsayilanDeger)){
            if (isimString.equals("")){
                kisiBasHarfi.setText("#");
            }else{
                kisiBasHarfi.setText(String.valueOf(isimString.charAt(0)));
            }
        }
        if (isimString.equals("")){
            isim.setText(telefonString);
        }else{
            isim.setText(isimString);
        }
        KisiBilgileriniGoster();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        gonderText = findViewById(R.id.gonderText);
    }

    private void KisiBilgileriniGoster() {
        DatabaseReference kisiBilgileri = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(telefonString);
        kisiBilgileri.keepSynced(true);
        kisiBilgileri.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                if (kullanici != null){
                    if (kullanici.getOnlineDurumu()){
                        durum.setText(getString(R.string.online));
                    }else{
                        durum.setText(getString(R.string.offline));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Geri(){
        Intent intent = new Intent(MesajActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        overridePendingTransition(R.anim.sagdan_sola_giris, R.anim.sagdan_sola_cikis);
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
}