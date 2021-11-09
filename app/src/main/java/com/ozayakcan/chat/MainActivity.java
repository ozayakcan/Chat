package com.ozayakcan.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozayakcan.chat.Bildirimler.BildirimClass;
import com.ozayakcan.chat.Fragment.KisilerFragment;
import com.ozayakcan.chat.Fragment.MesajlarFragment;
import com.ozayakcan.chat.Fragment.VPAdapter;
import com.ozayakcan.chat.Ozellik.Veritabani;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    ViewPager2 viewPager;
    Toolbar toolbar;
    TabLayout tabLayout;
    MesajlarFragment mesajlarFragment;
    Veritabani veritabani;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //test
        Button testActivityBtn = findViewById(R.id.testActivityBtn);
        testActivityBtn.setVisibility(View.GONE);
        testActivityBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TestActivity.class)));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.anamenu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuArsiv){
                startActivity(new Intent(MainActivity.this, ArsivActivity.class));
                overridePendingTransition(0,0);
                finish();
            }
            return false;
        });
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        veritabani = new Veritabani(MainActivity.this);
        veritabani.TokenYenile();
        BildirimClass.MesajBildiriminiKaldir(MainActivity.this);
        mesajlarFragment = new MesajlarFragment(MainActivity.this);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        vpAdapter.fragmentEkle(mesajlarFragment, getString(R.string.messages));
        vpAdapter.fragmentEkle(new KisilerFragment(MainActivity.this), getString(R.string.contacts));
        viewPager.setAdapter(vpAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(vpAdapter.baslikGetir(position))).attach();
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
    public void MesajBasiliTut(String id, String isim, String telefon, String profilResmi, int index){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.AltMenuTema);
        View altMenuView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_mesaj_islevleri, findViewById(R.id.altMenuLayout));
        altMenuView.findViewById(R.id.mesajiGoruntule).setOnClickListener(v -> {
            MesajGoster(id, isim, telefon, profilResmi);
            bottomSheetDialog.dismiss();
        });
        altMenuView.findViewById(R.id.mesajiArsivle).setOnClickListener(v -> {
            mesajlarFragment.MesajlariArsivle(telefon, index, true);
            bottomSheetDialog.dismiss();
        });
        altMenuView.findViewById(R.id.mesajiSil).setOnClickListener(v -> {
            mesajlarFragment.MesajlariSil(telefon, index, false);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(altMenuView);
        bottomSheetDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Veritabani.DurumGuncelle(firebaseUser, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Veritabani.DurumGuncelle(firebaseUser, false);
    }

}