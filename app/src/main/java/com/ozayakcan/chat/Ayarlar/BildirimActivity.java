package com.ozayakcan.chat.Ayarlar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ozayakcan.chat.Ozellik.KullaniciAppCompatActivity;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.HashMap;

public class BildirimActivity extends KullaniciAppCompatActivity {

    private SharedPreference sharedPreference;

    private SwitchCompat bildirimDurumuSwitch, bildirimSesiSwitch, bildirimOncelikSwitch;

    private TextView titresimText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bildirim);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());

        sharedPreference = new SharedPreference(BildirimActivity.this);
        // Bildirim
        bildirimDurumuSwitch = findViewById(R.id.bildirimDurumuSwitch);
        bildirimDurumuSwitch.setChecked(sharedPreference.GetirBoolean(Veritabani.BildirimDurumuKey, true));
        RelativeLayout bildirimDurumu = findViewById(R.id.bildirimDurumu);
        bildirimDurumu.setOnClickListener(v -> BildirimDurumu());
        // Ses
        bildirimSesiSwitch = findViewById(R.id.bildirimSesiSwitch);
        bildirimSesiSwitch.setChecked(sharedPreference.GetirBoolean(Veritabani.BildirimSesiKey, true));
        RelativeLayout bildirimSesi = findViewById(R.id.bildirimSesi);
        bildirimSesi.setOnClickListener(v -> BildirimSesi());
        // Öncelik
        bildirimOncelikSwitch = findViewById(R.id.bildirimOncelikSwitch);
        bildirimOncelikSwitch.setChecked(sharedPreference.GetirBoolean(Veritabani.BildirimOncelikKey, true));
        RelativeLayout bildirimOncelik = findViewById(R.id.bildirimOncelik);
        bildirimOncelik.setOnClickListener(v -> BildirimOncelik());
        // Titreşim
        RelativeLayout bildirimTitresim = findViewById(R.id.bildirimTitresim);
        titresimText = findViewById(R.id.titresimText);
        TitresimYazisi((int) sharedPreference.GetirLong(Veritabani.BildirimTitresimKey, 0));
        bildirimTitresim.setOnClickListener(v -> Titresim());
    }

    private void BildirimDurumu() {
        Kaydet(bildirimDurumuSwitch, Veritabani.BildirimDurumuKey);
    }

    private void BildirimSesi() {
        Kaydet(bildirimSesiSwitch, Veritabani.BildirimSesiKey);
    }

    private void BildirimOncelik(){
        Kaydet(bildirimOncelikSwitch, Veritabani.BildirimOncelikKey);
    }

    private void Kaydet(SwitchCompat bildirimSwitch, String key){
        bildirimSwitch.setChecked(!bildirimSwitch.isChecked());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        reference.keepSynced(true);
        sharedPreference.KaydetBoolean(key, bildirimSwitch.isChecked());
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, bildirimSwitch.isChecked());
        reference.updateChildren(map, (error, ref) -> {
            if (error != null){
                Toast.makeText(BildirimActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Titresim() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BildirimActivity.this);
        alertDialog.setTitle(getString(R.string.vibration));
        alertDialog.setItems(R.array.vibration_settings, (dialog, which) -> {
            dialog.dismiss();
            Kaydet(which, Veritabani.BildirimTitresimKey);
            TitresimYazisi(which);
        });
        alertDialog.show();
    }

    private void TitresimYazisi(int which) {
        switch (which){
            case 0:
                titresimText.setText(getString(R.string.default1));
                break;
            case 1:
                titresimText.setText(getString(R.string.long1));
                break;
            case 2:
                titresimText.setText(getString(R.string.short1));
                break;

        }
    }

    private void Kaydet(int deger, String key) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        reference.keepSynced(true);
        sharedPreference.KaydetLong(key, deger);
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, deger);
        reference.updateChildren(map, (error, ref) -> {
            if (error != null){
                Toast.makeText(BildirimActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Geri() {
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