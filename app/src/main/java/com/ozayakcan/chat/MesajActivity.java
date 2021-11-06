package com.ozayakcan.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Adapter.MesajAdapter;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.Ozellik.Veritabani;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView gonderText, altUyari;
    private FrameLayout gonderBtn;
    private TextView durum;

    private String telefonString;
    private String tabloString = Veritabani.MesajTablosu;

    private FirebaseUser firebaseUser;
    private Veritabani veritabani;
    private MesajAdapter mesajAdapter;
    private List<Mesaj> mesajList;
    private Query mesajlariGosterQuery;
    private DatabaseReference kisiBilgileriRef;
    private DatabaseReference onlineDurumuRef;
    private String idString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);
        Resimler resimler = new Resimler(MesajActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> Geri());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        veritabani = new Veritabani(MesajActivity.this);
        mesajList = new ArrayList<>();

        Intent intent = getIntent();
        idString = intent.getStringExtra(Veritabani.IDKey);
        String isimString = intent.getStringExtra(Veritabani.IsimKey);
        telefonString = intent.getStringExtra(Veritabani.TelefonKey);
        String profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);
        String intentTabloString = intent.getStringExtra(Veritabani.MesajTablosu);
        if (intentTabloString != null){
            if (!intentTabloString.equals("")){
                tabloString = intentTabloString;
            }
        }
        gonderBtn = findViewById(R.id.gonderBtn);
        gonderText = findViewById(R.id.gonderText);
        altUyari = findViewById(R.id.altUyari);
        Uyari(tabloString.equals(Veritabani.ArsivTablosu), getString(R.string.you_cannot_send_messages_in_the_archive));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mesajAdapter = new MesajAdapter(MesajActivity.this, mesajList);
        mesajAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                recyclerView.setAdapter(mesajAdapter);
            }
        });
        recyclerView.setAdapter(mesajAdapter);
        CircleImageView profilResmi = findViewById(R.id.profilResmi);
        TextView kisiBasHarfi = findViewById(R.id.kisiBasHarfi);
        TextView isim = findViewById(R.id.isim);
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
        mesajlariGosterQuery = FirebaseDatabase.getInstance().getReference(tabloString).child(firebaseUser.getPhoneNumber()).child(telefonString).orderByKey();
        mesajlariGosterQuery.keepSynced(true);
        kisiBilgileriRef = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(telefonString);
        kisiBilgileriRef.keepSynced(true);
        onlineDurumuRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        KisiBilgileriniGoster(true);
        KisininOnlineDurumunuGuncelle(true);
        MesajlariGoster(true);
        gonderBtn.setOnClickListener(v -> MesajGonder());
    }

    private void Uyari(boolean goster, String... uyari) {
        String uyariYazisi = uyari.length > 0 ? uyari[0] : "";
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        if (goster){
            gonderText.setVisibility(View.GONE);
            gonderBtn.setVisibility(View.GONE);
            altUyari.setText(uyariYazisi);
            altUyari.setVisibility(View.VISIBLE);
            constraintSet.connect(R.id.recyclerView,ConstraintSet.BOTTOM,R.id.altUyari,ConstraintSet.TOP,0);
        }else{
            altUyari.setText(uyariYazisi);
            altUyari.setVisibility(View.GONE);
            gonderText.setVisibility(View.VISIBLE);
            gonderBtn.setVisibility(View.VISIBLE);
            constraintSet.connect(R.id.recyclerView,ConstraintSet.BOTTOM,R.id.gonderTextLayout,ConstraintSet.TOP,0);
        }
        constraintSet.applyTo(constraintLayout);
    }

    boolean mesajlarGuncelleniyor = false;
    private void MesajlariGoster(boolean goster) {
        if (mesajlarGuncelleniyor != goster){
            if (goster){
                mesajlariGosterQuery.addValueEventListener(mesajlariGosterEventListener);
            }else{
                mesajlariGosterQuery.removeEventListener(mesajlariGosterEventListener);
            }
            mesajlarGuncelleniyor = goster;
        }
    }

    private void MesajGonder() {
        String mesaj = gonderText.getText().toString();
        String mesajKontrol = mesaj.replace("\n", "");
        if(!mesajKontrol.equals("")){
            veritabani.MesajGonder(mesaj, idString, telefonString, firebaseUser, MesajActivity.this);
            gonderText.setText("");
        }else{
            Toast.makeText(MesajActivity.this, getString(R.string.you_cannot_send_empty_messages), Toast.LENGTH_SHORT).show();
        }
    }

    private final ValueEventListener mesajlariGosterEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            mesajList.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                Mesaj mesaj = dataSnapshot.getValue(Mesaj.class);
                if (mesajList.size() > 0){
                    if (!ChatApp.MesajTarihiBul(mesaj.getTarih(), false).equals(ChatApp.MesajTarihiBul(mesajList.get(mesajList.size()-1).getTarih(), false))){
                        Mesaj tarihMsj = new Mesaj();
                        tarihMsj.setTarih(mesaj.getTarih());
                        tarihMsj.setTarihGoster(true);
                        tarihMsj.setMesaj(ChatApp.MesajTarihiBul(mesaj.getTarih(), false));
                        mesajList.add(tarihMsj);
                    }
                }else{
                    Mesaj tarihMsj = new Mesaj();
                    tarihMsj.setTarih(mesaj.getTarih());
                    tarihMsj.setTarihGoster(true);
                    tarihMsj.setMesaj(ChatApp.MesajTarihiBul(mesaj.getTarih(), false));
                    mesajList.add(tarihMsj);
                }
                if (!mesaj.isGonderen()){
                    if (tabloString.equals(Veritabani.MesajTablosu)){
                        HashMap<String, Object> mapBir = new HashMap<>();
                        mapBir.put(Veritabani.GorulduKey, true);
                        DatabaseReference gorulduOlarakIsaretleBir = FirebaseDatabase.getInstance()
                                .getReference(Veritabani.MesajTablosu+"/"+firebaseUser.getPhoneNumber()+"/"+telefonString+"/"+dataSnapshot.getKey());
                        gorulduOlarakIsaretleBir.updateChildren(mapBir);
                        DatabaseReference gorulduOlarakIsaretleIki = FirebaseDatabase.getInstance()
                                .getReference(Veritabani.MesajTablosu+"/"+telefonString+"/"+firebaseUser.getPhoneNumber()+"/"+dataSnapshot.getKey());
                        gorulduOlarakIsaretleIki.updateChildren(mapBir);
                    }
                    mesajList.add(mesaj);
                }else{
                    mesajList.add(mesaj);
                }
            }
            mesajAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private boolean kisiBilgileriGuncelleniyor = false;
    private void KisiBilgileriniGoster(boolean goster) {
        if (kisiBilgileriGuncelleniyor != goster){
            if (goster){
                kisiBilgileriRef.addValueEventListener(kisiBilgileriValueEventListener);
            }else{
                kisiBilgileriRef.removeEventListener(kisiBilgileriValueEventListener);
            }
            kisiBilgileriGuncelleniyor = goster;
        }
    }
    private final ValueEventListener kisiBilgileriValueEventListener = new ValueEventListener() {
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
    };
    private boolean onlineDurumuGuncelleniyor = false;
    private void KisininOnlineDurumunuGuncelle(boolean guncelle){
        if (onlineDurumuGuncelleniyor != guncelle){
            if (guncelle){
                onlineDurumuRef.addValueEventListener(onlineDurumuValueEventListener);
            }else{
                onlineDurumuRef.removeEventListener(onlineDurumuValueEventListener);
            }
            onlineDurumuGuncelleniyor = guncelle;
        }
    }
    private final ValueEventListener onlineDurumuValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            boolean baglandi = snapshot.getValue(Boolean.class);
            if (baglandi){
                KisiBilgileriniGoster(true);
            }else{
                durum.setText(getString(R.string.offline));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    private void Geri(){
        MesajlariGoster(false);
        KisiBilgileriniGoster(false);
        KisininOnlineDurumunuGuncelle(false);
        Intent intent = new Intent(MesajActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        overridePendingTransition(R.anim.soldan_saga_giris, R.anim.soldan_saga_cikis);
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
        MesajlariGoster(true);
        KisiBilgileriniGoster(true);
        KisininOnlineDurumunuGuncelle(true);
        Veritabani.DurumGuncelle(firebaseUser, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MesajlariGoster(false);
        KisiBilgileriniGoster(false);
        KisininOnlineDurumunuGuncelle(false);
        Veritabani.DurumGuncelle(firebaseUser, false);
    }
}