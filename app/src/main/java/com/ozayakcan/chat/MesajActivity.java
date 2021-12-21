package com.ozayakcan.chat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Adapter.MesajAdapter;
import com.ozayakcan.chat.Bildirimler.BildirimClass;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.MesajFonksiyonlari;
import com.ozayakcan.chat.Ozellik.Metinler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.Resimler.ResimlerClass;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajActivity extends KullaniciAppCompatActivity {

    private RecyclerView mesajlarRW;
    private TextView gonderText, altUyari;
    private LinearLayout gonderBtnLayout, gonderTextLayout, kaydirBtnLayout;
    private TextView durum;

    private boolean ilkAcilis = true;

    private String telefonString;
    private String tabloString = Veritabani.MesajTablosu;
    private MesajAdapter mesajAdapter;
    private List<Mesaj> mesajList;
    private DatabaseReference kisiBilgileriRef;
    private DatabaseReference onlineDurumuRef;
    private String idString;

    private String getirilecekMesaj = MesajFonksiyonlari.KaydedilecekTur;
    private int KlavyeYuksekligi = 0;

    boolean kaydir = true;
    private int sonMesaj = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> Geri());

        View view = findViewById(R.id.constraintLayout);
        view.post(() -> klavyePopup.Baslat());

        Intent intent = getIntent();
        idString = intent.getStringExtra(Veritabani.IDKey);
        String isimString = intent.getStringExtra(Veritabani.IsimKey);
        telefonString = intent.getStringExtra(Veritabani.TelefonKey);
        String profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);
        String intentTabloString = intent.getStringExtra(Veritabani.MesajTablosu);
        gonderText = findViewById(R.id.gonderText);
        if (intentTabloString != null && !intentTabloString.equals("")){
            tabloString = intentTabloString;
            if (tabloString.equals(Veritabani.ArsivTablosu)){
                getirilecekMesaj = MesajFonksiyonlari.KaydedilecekTurArsiv;
            }else{
                getirilecekMesaj = MesajFonksiyonlari.KaydedilecekTur;
                gonderText.requestFocus();
                Metinler.getInstance(MesajActivity.this).KlavyeAc();
            }
        }
        mesajlarRW = findViewById(R.id.mesajlarRW);
        mesajlarRW.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mesajlarRW.setLayoutManager(linearLayoutManager);

        gonderBtnLayout = findViewById(R.id.gonderBtnLayout2);
        gonderTextLayout = findViewById(R.id.gonderTextLayout2);
        kaydirBtnLayout = findViewById(R.id.kaydirBtnLayout2);
        kaydirBtnLayout.setOnClickListener(v -> AltaKaydir());
        altUyari = findViewById(R.id.altUyari);
        Uyari(tabloString.equals(Veritabani.ArsivTablosu), getString(R.string.you_cannot_send_messages_in_the_archive));

        mesajList = new ArrayList<>();
        mesajAdapter = new MesajAdapter(MesajActivity.this, mesajList);
        mesajlarRW.setAdapter(mesajAdapter);
        mesajlarRW.addOnScrollListener(scrollListener);
        CircleImageView profilResmi = findViewById(R.id.profilResmi);
        TextView kisiBasHarfi = findViewById(R.id.kisiBasHarfi);
        TextView isim = findViewById(R.id.isim);
        durum = findViewById(R.id.durum);
        ResimlerClass.getInstance(MesajActivity.this).ResimGoster(profilResmiString, profilResmi, R.drawable.varsayilan_arkaplan);
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
        kisiBilgileriRef.keepSynced(true);
        onlineDurumuRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        KisiBilgileriniGoster(true);
        KisininOnlineDurumunuGuncelle(true);
        gonderBtnLayout.setOnClickListener(v -> MesajGonder());
        MesajlariGoster(0);
    }

    private boolean YeniMesajlar(boolean temizle, int ekle) {
        for (int i = mesajList.size() - 1; i >= 0; i--){
            Mesaj mesaj = mesajList.get(i);
            if (mesaj.getYeniMesajSayisi() > 0){
                if (temizle){
                    mesaj.setYeniMesajSayisi(0);
                }else{
                    mesaj.setYeniMesajSayisi(mesaj.getYeniMesajSayisi() + ekle);
                }
                MesajDurumunuGuncelle(i, mesaj);
                return true;
            }
        }
        return false;
    }
    private void AltaKaydir() {
        mesajlarRW.smoothScrollToPosition(mesajList.size()-1);
    }

    private final int KAYDIR_SCROLLSTATE = 0;
    private final int KAYDIR_SCROLLED = 1;

    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    Kaydir(recyclerView, KAYDIR_SCROLLSTATE);
                    Kaydir2();
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    kaydir = false;
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            Kaydir(recyclerView, KAYDIR_SCROLLED);
        }
    };

    private void Kaydir(RecyclerView recyclerView, int durum) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) manager;
        if (linearLayoutManager1 != null){
            int sonMesaj1 = Math.max(linearLayoutManager1.findLastVisibleItemPosition(), 0);
            if(durum == KAYDIR_SCROLLSTATE){
                sonMesaj = sonMesaj1;
                kaydir = !(sonMesaj1 < linearLayoutManager1.getItemCount()-1);
            }
            if(durum == KAYDIR_SCROLLED){
                if(linearLayoutManager1.getItemCount() > 0){
                    kaydirBtnLayout.setVisibility(sonMesaj1 < linearLayoutManager1.getItemCount()-1 ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    private void Kaydir2() {
        if (kaydir){
            YeniMesajlar(true, 0);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void KlavyeYuksekligiDegisti(int yukseklik) {
        if (yukseklik > 0){
            KlavyeYuksekligi = yukseklik;
        }
        mesajlarRW.scrollToPosition(kaydir ? mesajList.size()-1 : sonMesaj);
        super.KlavyeYuksekligiDegisti(yukseklik);
    }

    private void Uyari(boolean goster, String uyariYazisi) {
        if (goster){
            gonderTextLayout.setVisibility(View.GONE);
            gonderBtnLayout.setVisibility(View.GONE);
            altUyari.setText(uyariYazisi);
            altUyari.setVisibility(View.VISIBLE);
        }else{
            altUyari.setText(uyariYazisi);
            altUyari.setVisibility(View.GONE);
            gonderTextLayout.setVisibility(View.VISIBLE);
            gonderBtnLayout.setVisibility(View.VISIBLE);
        }
    }
    private void GorulduOlarakIsaretle() {
        if (getirilecekMesaj.equals(MesajFonksiyonlari.KaydedilecekTur)){
            DatabaseReference gorulduKisiRef = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(telefonString);
            gorulduKisiRef.keepSynced(true);
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
    }
    @SuppressLint("NotifyDataSetChanged")
    private void MesajlariGoster(int sira){
        if (sira >= 0){
            List<Mesaj> mesajlar = MesajFonksiyonlari.getInstance(MesajActivity.this).MesajlariGetir(telefonString, getirilecekMesaj);
            for (int i = sira; i < mesajlar.size(); i++){
                Mesaj mesaj = mesajlar.get(i);
                if (sira > 0 && sira == i && !kaydir){
                    if (!YeniMesajlar(false, mesajlar.size() - mesajList.size())){
                        mesaj.setYeniMesajSayisi(mesajlar.size() - mesajList.size());
                    }
                }else if (sira == 0){
                    mesaj.setYeniMesajSayisi(0);
                }
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
            if (ilkAcilis){
                sonMesaj = Math.max(mesajList.size() - 1, 0);
                kaydir = true;
                ilkAcilis = false;
            }
            if (kaydir){
                mesajlarRW.scrollToPosition(mesajList.size()-1);
            }
            MesajlariGuncelle(true);
        }
        GorulduOlarakIsaretle();
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

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getirilecekMesaj.equals(MesajFonksiyonlari.KaydedilecekTur)){
                if(intent.getAction().equals(BildirimClass.MesajKey)){
                    if (getirilecekMesaj.equals(MesajFonksiyonlari.KaydedilecekTur)){
                        MesajlariGoster(mesajList.size());
                    }
                }else if (intent.getAction().equals(BildirimClass.GorulduKey)){
                    if (getirilecekMesaj.equals(MesajFonksiyonlari.KaydedilecekTur)){
                        if (intent.getStringExtra(BildirimClass.KisiKey).equals(telefonString)){
                            for (int i = mesajList.size() - 1; i >= 0; i--){
                                Mesaj mesaj = mesajList.get(i);
                                if (mesajList.get(i).isGonderen() && !mesajList.get(i).isGoruldu()){
                                    mesaj.setMesajDurumu(Veritabani.MesajDurumuGonderildi);
                                    mesaj.setGoruldu(true);
                                    mesajList.set(i, mesaj);
                                }
                            }
                            mesajAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    };



    @SuppressLint("NotifyDataSetChanged")
    private void MesajGonder() {
        if (getirilecekMesaj.equals(MesajFonksiyonlari.KaydedilecekTurArsiv)){
            return;
        }
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
            mesajlarRW.scrollToPosition(mesajList.size() - 1);
            Veritabani.getInstance(MesajActivity.this).MesajGonder(mesajClass, mesajList.size()-1, telefonString, firebaseUser, MesajActivity.this);
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
                if (kullanici.isOnlineDurumu()){
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
        ChatApp.SuankiKisiyiAyarla(telefonString);
    }

    @Override
    protected void onPause() {
        super.onPause();
        KisiBilgileriniGoster(false);
        KisininOnlineDurumunuGuncelle(false);
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
        klavyePopup.Durdur();
    }
}