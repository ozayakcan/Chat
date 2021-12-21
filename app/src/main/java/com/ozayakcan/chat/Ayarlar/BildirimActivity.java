package com.ozayakcan.chat.Ayarlar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private SwitchCompat bildirimDurumuSwitch, bildirimSesiSwitch, bildirimOncelikSwitch;

    private TextView tonText, titresimText, isikText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bildirim);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());

        // Bildirim
        bildirimDurumuSwitch = findViewById(R.id.bildirimDurumuSwitch);
        bildirimDurumuSwitch.setChecked(SharedPreference.getInstance(BildirimActivity.this).GetirBoolean(Veritabani.BildirimDurumuKey, true));
        RelativeLayout bildirimDurumu = findViewById(R.id.bildirimDurumu);
        bildirimDurumu.setOnClickListener(v -> BildirimDurumu());
        // Ses
        bildirimSesiSwitch = findViewById(R.id.bildirimSesiSwitch);
        bildirimSesiSwitch.setChecked(SharedPreference.getInstance(BildirimActivity.this).GetirBoolean(Veritabani.BildirimSesiKey, true));
        RelativeLayout bildirimSesi = findViewById(R.id.bildirimSesi);
        bildirimSesi.setOnClickListener(v -> BildirimSesi());
        // Bildirim Sesi
        LinearLayout bildirimTon = findViewById(R.id.bildirimTon);
        tonText = findViewById(R.id.tonText);
        BildirimSesiGoster(SharedPreference.getInstance(BildirimActivity.this).GetirString(Veritabani.BildirimTonuKey, "").equals("") ?
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) : Uri.parse(SharedPreference.getInstance(BildirimActivity.this).GetirString(Veritabani.BildirimTonuKey, "")));
        bildirimTon.setOnClickListener(v -> BildirimSesiDegistir());
        // Titreşim
        LinearLayout bildirimTitresim = findViewById(R.id.bildirimTitresim);
        titresimText = findViewById(R.id.titresimText);
        YaziDegistir((int) SharedPreference.getInstance(BildirimActivity.this).GetirLong(Veritabani.BildirimTitresimKey, 0), titresimText, R.array.titresim_ayarlari);
        bildirimTitresim.setOnClickListener(v -> Diyalog(R.string.vibration, R.array.titresim_ayarlari, Veritabani.BildirimTitresimKey, titresimText));
        // Işık
        LinearLayout bildirimIsik = findViewById(R.id.bildirimIsik);
        isikText = findViewById(R.id.isikText);
        YaziDegistir((int) SharedPreference.getInstance(BildirimActivity.this).GetirLong(Veritabani.BildirimIsigiKey, 1), isikText, R.array.bildirim_isigi);
        bildirimIsik.setOnClickListener(v -> Diyalog(R.string.light, R.array.bildirim_isigi, Veritabani.BildirimIsigiKey, isikText));
        // Öncelik
        bildirimOncelikSwitch = findViewById(R.id.bildirimOncelikSwitch);
        bildirimOncelikSwitch.setChecked(SharedPreference.getInstance(BildirimActivity.this).GetirBoolean(Veritabani.BildirimOncelikKey, true));
        RelativeLayout bildirimOncelik = findViewById(R.id.bildirimOncelik);
        bildirimOncelik.setOnClickListener(v -> BildirimOncelik());
    }

    private void BildirimSesiDegistir() {
        Intent bildirimSesiIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        bildirimSesiIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        bildirimSesiIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        bildirimSesiIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.notification_tone));
        bildirimSesiIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, SharedPreference.getInstance(BildirimActivity.this).GetirString(Veritabani.BildirimTonuKey, "").equals("") ?
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) : Uri.parse(SharedPreference.getInstance(BildirimActivity.this).GetirString(Veritabani.BildirimTonuKey, "")));
        bildirimSesiActivityResult.launch(bildirimSesiIntent);
    }
    ActivityResultLauncher<Intent> bildirimSesiActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent veri = result.getData();
                    if (veri != null){
                        Uri ses = veri.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                        if (ses != null){
                            SharedPreference.getInstance(BildirimActivity.this).KaydetString(Veritabani.BildirimTonuKey, ses.toString());
                            BildirimSesiGoster(ses);
                        }
                    }
                }
            });

    private void BildirimSesiGoster(Uri ses) {
        Ringtone muzik = RingtoneManager.getRingtone(BildirimActivity.this, ses);
        tonText.setText(muzik.getTitle(BildirimActivity.this));
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
        SharedPreference.getInstance(BildirimActivity.this).KaydetBoolean(key, bildirimSwitch.isChecked());
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, bildirimSwitch.isChecked());
        reference.updateChildren(map, (error, ref) -> {
            if (error != null){
                Toast.makeText(BildirimActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Diyalog(int baslik, int diziID, String key, TextView textView) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BildirimActivity.this);
        alertDialog.setTitle(getString(baslik));
        alertDialog.setItems(diziID, (dialog, which) -> {
            dialog.dismiss();
            Kaydet(which, key);
            YaziDegistir(which, textView, diziID);
        });
        alertDialog.show();
    }

    private void YaziDegistir(int konum, TextView textView, int diziID) {
        String[] dizi = getResources().getStringArray(diziID);
        if (konum >= dizi.length){
            textView.setText(dizi[0]);
        }else{
            textView.setText(dizi[konum]);
        }
    }

    private void Kaydet(int deger, String key) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        reference.keepSynced(true);
        SharedPreference.getInstance(BildirimActivity.this).KaydetLong(key, deger);
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