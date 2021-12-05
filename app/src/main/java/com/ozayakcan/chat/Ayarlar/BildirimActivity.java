package com.ozayakcan.chat.Ayarlar;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
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

    private SwitchCompat bildirimDurumuSwitch, bildirimSesiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bildirim);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());

        sharedPreference = new SharedPreference(BildirimActivity.this);
        bildirimDurumuSwitch = findViewById(R.id.bildirimDurumuSwitch);
        bildirimDurumuSwitch.setChecked(sharedPreference.GetirBoolean(Veritabani.BildirimDurumuKey, true));
        LinearLayout bildirimDurumu = findViewById(R.id.bildirimDurumu);
        bildirimDurumu.setOnClickListener(v -> BildirimDurumu());
        bildirimSesiSwitch = findViewById(R.id.bildirimSesiSwitch);
        bildirimSesiSwitch.setChecked(sharedPreference.GetirBoolean(Veritabani.BildirimSesiKey, true));
        LinearLayout bildirimSesi = findViewById(R.id.bildirimSesi);
        bildirimSesi.setOnClickListener(v -> BildirimSesi());
    }
    private void BildirimDurumu() {
        BildirimAyarlari(bildirimDurumuSwitch, Veritabani.BildirimDurumuKey);
    }

    private void BildirimSesi() {
        BildirimAyarlari(bildirimSesiSwitch, Veritabani.BildirimSesiKey);
    }

    private void BildirimAyarlari(SwitchCompat bildirimSwitch, String key){
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