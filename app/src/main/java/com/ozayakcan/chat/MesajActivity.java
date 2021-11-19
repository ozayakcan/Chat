package com.ozayakcan.chat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Adapter.MesajAdapter;
import com.ozayakcan.chat.Bildirimler.BildirimClass;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Ozellik.MesajFonksiyonlari;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.Ozellik.Veritabani;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView gonderText, altUyari;
    private LinearLayout gonderBtn, gonderTextLayout;
    private TextView durum;

    private String telefonString;
    private String tabloString = Veritabani.MesajTablosu;

    private FirebaseUser firebaseUser;
    private Veritabani veritabani;
    private MesajAdapter mesajAdapter;
    private List<Mesaj> mesajList;
    private DatabaseReference kisiBilgileriRef;
    private DatabaseReference onlineDurumuRef;
    private String idString;

    private String getirilecekMesaj = MesajFonksiyonlari.KaydedilecekTur;

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

        Intent intent = getIntent();
        idString = intent.getStringExtra(Veritabani.IDKey);
        String isimString = intent.getStringExtra(Veritabani.IsimKey);
        telefonString = intent.getStringExtra(Veritabani.TelefonKey);
        String profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);
        String intentTabloString = intent.getStringExtra(Veritabani.MesajTablosu);
        if (intentTabloString != null && !intentTabloString.equals("")){
            tabloString = intentTabloString;
            if (tabloString.equals(Veritabani.ArsivTablosu)){
                getirilecekMesaj = MesajFonksiyonlari.KaydedilecekTurArsiv;
            }else{
                getirilecekMesaj = MesajFonksiyonlari.KaydedilecekTur;
            }
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        gonderBtn = findViewById(R.id.gonderBtn);
        gonderText = findViewById(R.id.gonderText);
        gonderTextLayout = findViewById(R.id.gonderTextLayout);
        altUyari = findViewById(R.id.altUyari);
        Uyari(tabloString.equals(Veritabani.ArsivTablosu), getString(R.string.you_cannot_send_messages_in_the_archive));

        mesajList = new ArrayList<>();
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
        kisiBilgileriRef = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(telefonString);
        onlineDurumuRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        KisiBilgileriniGoster(true);
        KisininOnlineDurumunuGuncelle(true);
        gonderBtn.setOnClickListener(v -> MesajGonder());
        MesajlariGoster();
    }

    private void Uyari(boolean goster, String uyariYazisi) {
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        if (goster){
            gonderTextLayout.setVisibility(View.GONE);
            gonderBtn.setVisibility(View.GONE);
            altUyari.setText(uyariYazisi);
            altUyari.setVisibility(View.VISIBLE);
            constraintSet.connect(R.id.recyclerView,ConstraintSet.BOTTOM,R.id.altUyari,ConstraintSet.TOP,0);
        }else{
            altUyari.setText(uyariYazisi);
            altUyari.setVisibility(View.GONE);
            gonderTextLayout.setVisibility(View.VISIBLE);
            gonderBtn.setVisibility(View.VISIBLE);
            constraintSet.connect(R.id.recyclerView,ConstraintSet.BOTTOM,R.id.gonderTextLayout,ConstraintSet.TOP,0);
        }
        constraintSet.applyTo(constraintLayout);
    }
    private void GorulduOlarakIsaretle() {
        DatabaseReference gorulduKisiRef = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(telefonString);
        gorulduKisiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                if (kullanici != null){
                    BildirimClass.getInstance(MesajActivity.this).GorulduBildirimiYolla(kullanici.getFcmToken(), telefonString, firebaseUser.getPhoneNumber());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void MesajlariGoster(){
        GorulduOlarakIsaretle();
        List<Mesaj> mesajlar = MesajFonksiyonlari.getInstance(MesajActivity.this).MesajlariGetir(telefonString, getirilecekMesaj);
        for (Mesaj mesaj : mesajlar){
            if (mesajList.size() > 0){
                if (!ChatApp.MesajTarihiBul(mesaj.getTarih(), false).equals(ChatApp.MesajTarihiBul(mesajList.get(mesajList.size()-1).getTarih(), false))){
                    mesaj.setTarihGoster(true);
                }
            }else{
                mesaj.setTarihGoster(true);
            }
            mesajList.add(mesaj);
        }
        mesajAdapter.notifyDataSetChanged();
        MesajlariGuncelle(true);
    }
    boolean mesajlarGuncelleniyor = false;
    private void MesajlariGuncelle(boolean goster) {
        if (mesajlarGuncelleniyor != goster){
            if (goster){
                ChatApp.registerBroadcastReceiver(mMesaj2BroadcastReceiver, BildirimClass.MesajKey);
                ChatApp.registerBroadcastReceiver(mMesaj2BroadcastReceiver, BildirimClass.GorulduKey);
            }else{
                ChatApp.unregisterBroadcastReceiver(mMesaj2BroadcastReceiver);
            }
            mesajlarGuncelleniyor = goster;
        }
    }
    private final BroadcastReceiver mMesaj2BroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(BildirimClass.MesajKey)){
            mesajList.clear();
            MesajlariGoster();
        }else if (intent.getAction().equals(BildirimClass.GorulduKey)){
            if (intent.getStringExtra(BildirimClass.KisiKey).equals(telefonString)){
                mesajList.clear();
                MesajlariGoster();
            }
        }
        }
    };



    @SuppressLint("NotifyDataSetChanged")
    private void MesajGonder() {
        String mesaj = gonderText.getText().toString();
        String mesajKontrol = mesaj.replace("\n", "");
        if(!mesajKontrol.equals("")){
            Mesaj mesajClass = MesajFonksiyonlari.getInstance(MesajActivity.this).MesajiKaydet("", telefonString, mesaj, Veritabani.MesajDurumuGonderiliyor,true);
            if (mesajList.size() > 0){
                if (!ChatApp.MesajTarihiBul(mesajClass.getTarih(), false).equals(ChatApp.MesajTarihiBul(mesajList.get(mesajList.size()-1).getTarih(), false))){
                    mesajClass.setTarihGoster(true);
                }
            }else{
                mesajClass.setTarihGoster(true);
            }
            mesajList.add(mesajClass);
            mesajAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(mesajList.size() - 1);
            veritabani.MesajGonder(mesajClass, mesajList.size()-1, telefonString, firebaseUser, MesajActivity.this);
            gonderText.setText("");
        }else{
            Toast.makeText(MesajActivity.this, getString(R.string.you_cannot_send_empty_messages), Toast.LENGTH_SHORT).show();
        }
    }

    public void MesajDurumunuGuncelle(int sira, Mesaj mesaj){
        if (mesajList.size() >= sira){
            mesajList.set(sira, mesaj);
            MesajFonksiyonlari.getInstance(MesajActivity.this).MesajDuzenle(telefonString, mesajList);
            mesajAdapter.notifyItemChanged(sira);
        }
    }

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
        MesajlariGuncelle(false);
        KisiBilgileriniGoster(false);
        KisininOnlineDurumunuGuncelle(false);
        Intent intent;
        if (getirilecekMesaj.equals(MesajFonksiyonlari.KaydedilecekTurArsiv)){
            intent = new Intent(MesajActivity.this, ArsivActivity.class);
        }else{
            intent = new Intent(MesajActivity.this, MainActivity.class);
        }
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
    protected void onStart() {
        super.onStart();
        MesajlariGuncelle(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        KisiBilgileriniGoster(true);
        KisininOnlineDurumunuGuncelle(true);
        Veritabani.DurumGuncelle(firebaseUser, true);
        ChatApp.SuankiKisiyiAyarla(telefonString);
    }

    @Override
    protected void onPause() {
        super.onPause();
        KisiBilgileriniGoster(false);
        KisininOnlineDurumunuGuncelle(false);
        Veritabani.DurumGuncelle(firebaseUser, false);
        ChatApp.SuankiKisiyiAyarla("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        MesajlariGuncelle(false);
        KisiBilgileriniGoster(false);
        KisininOnlineDurumunuGuncelle(false);
        ChatApp.SuankiKisiyiAyarla("");
    }

    @Override
    protected void onDestroy() {
        ChatApp.SuankiKisiyiAyarla("");
        super.onDestroy();
    }
}