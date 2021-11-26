package com.ozayakcan.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ozayakcan.chat.Ayarlar.AyarlarActivity;
import com.ozayakcan.chat.Bildirimler.BildirimClass;
import com.ozayakcan.chat.Fragment.KisilerFragment;
import com.ozayakcan.chat.Fragment.MesajlarFragment;
import com.ozayakcan.chat.Fragment.VPAdapter;
import com.ozayakcan.chat.Model.Mesajlar;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.Servisler.BaglantiServisi;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends KullaniciAppCompatActivity {

    ViewPager2 viewPager;
    Toolbar toolbar;
    TabLayout tabLayout;
    MesajlarFragment mesajlarFragment;
    KisilerFragment kisilerFragment;
    Veritabani veritabani;
    TextView baslik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //test
        Button testActivityBtn = findViewById(R.id.testActivityBtn);
        testActivityBtn.setVisibility(View.GONE);
        testActivityBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TestActivity.class)));
        BaglantiServisi.ServisiBaslat(MainActivity.this);
        toolbar = findViewById(R.id.toolbar);
        baslik = findViewById(R.id.baslik);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        veritabani = new Veritabani(MainActivity.this);
        veritabani.TokenYenile();
        BildirimClass.MesajBildiriminiKaldir(MainActivity.this);
        mesajlarFragment = new MesajlarFragment(MainActivity.this);
        kisilerFragment = new KisilerFragment(MainActivity.this);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        vpAdapter.fragmentEkle(mesajlarFragment, getString(R.string.messages));
        vpAdapter.fragmentEkle(kisilerFragment, getString(R.string.contacts));
        viewPager.setAdapter(vpAdapter);
        MesajMenusu();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0){
                    MesajMenusu();
                }else{
                    KisilerMenusu();
                }
            }
        });
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(vpAdapter.baslikGetir(position))).attach();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, BaglantiServisi.class));
    }

    @Override
    protected void onStop() {
        //stopService(new Intent(this, BaglantiServisi.class));
        super.onStop();
    }

    public void MesajMenusu(){
        MesajBasiliTut(false);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.mesajlar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuArsiv){
                startActivity(new Intent(MainActivity.this, ArsivActivity.class));
                overridePendingTransition(0,0);
                finish();
            }else if (item.getItemId() == R.id.menuAyarlar){
                AyarlariAc();
            }
            return false;
        });
    }
    public void KisilerMenusu(){
        MesajBasiliTut(false);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.kisiler);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuKisileriYenile){
                kisilerFragment.KisileriYenile();
            }else if (item.getItemId() == R.id.menuAyarlar){
                AyarlariAc();
            }
            return false;
        });
    }

    private void AyarlariAc() {
        Intent intent = new Intent(MainActivity.this, AyarlarActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

    public void MesajGoster(String id, String isim, String telefon, String profilResmi){
        Intent intent = new Intent(MainActivity.this, MesajActivity.class);
        intent.putExtra(Veritabani.IDKey, id);
        intent.putExtra(Veritabani.IsimKey, isim);
        intent.putExtra(Veritabani.TelefonKey, telefon);
        intent.putExtra(Veritabani.ProfilResmiKey, profilResmi);
        intent.putExtra(Veritabani.MesajTablosu, Veritabani.MesajTablosu);
        startActivity(intent);
        overridePendingTransition(R.anim.sagdan_sola_giris, R.anim.sagdan_sola_cikis);
        finish();
    }

    private int SecilenMesajSayisi = 0;
    public void SecilenMesajSayisiniGoster(boolean arttir) {
        if (arttir){
            SecilenMesajSayisi++;
        }else{
            SecilenMesajSayisi--;
        }
        if (SecilenMesajSayisi > 0){
            baslik.setText(String.valueOf(SecilenMesajSayisi));
        }else{
            baslik.setText(getString(R.string.app_name));
            MesajMenusu();
        }
    }
    public boolean MesajSecildi = false;
    @SuppressLint("NotifyDataSetChanged")
    public void MesajBasiliTut(boolean secildi){
        if (secildi){
            toolbar.setNavigationIcon(R.drawable.geri_butonu);
            toolbar.setNavigationOnClickListener(v -> MesajBasiliTut(false));
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.mesajlar_islev);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menuSil || item.getItemId() == R.id.menuArsivle){
                    List<Mesajlar> mesajlarArrayList = new ArrayList<>();
                    for (int i = 0; i < mesajlarFragment.mesajlarList.size(); i++) {
                        Mesajlar mesajlar = mesajlarFragment.mesajlarList.get(i);
                        if (mesajlar.isSecildi()) {
                            mesajlarArrayList.add(mesajlar);
                        }
                    }
                    if(item.getItemId() == R.id.menuSil){
                        mesajlarFragment.MesajlariSil(mesajlarArrayList, false);
                    }else{
                        mesajlarFragment.MesajlariArsivle(mesajlarArrayList, true);
                    }
                }
                return false;
            });
        }else{
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(v -> {

            });
            baslik.setText(getString(R.string.app_name));
            SecilenMesajSayisi = 0;
            for (int i = 0; i < mesajlarFragment.mesajlarList.size(); i++) {
                Mesajlar mesajlar = mesajlarFragment.mesajlarList.get(i);
                if (mesajlar.isSecildi()) {
                    mesajlarFragment.mesajlarList.get(i).setSecildi(false);
                    mesajlarFragment.mesajlarAdapter.notifyItemChanged(i);
                }
            }
        }
        MesajSecildi = secildi;
    }

    private void Geri() {
        if (MesajSecildi){
            MesajMenusu();
        }else{
            finish();
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