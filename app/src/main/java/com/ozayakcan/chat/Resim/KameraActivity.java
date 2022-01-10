package com.ozayakcan.chat.Resim;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class KameraActivity extends AppCompatActivity {

    private final String TAG = "Kamera";
    private final String FLASH = "flash";

    private int FLASH_KAPALI = 0;
    private int FLASH_ACIK = 1;
    private int FLASH_OTO = 2;

    CameraView kamera;
    private ImageView kameraBtn, flashBtn;

    private long FLASH_DURUMU = FLASH_ACIK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kamera);
        kamera = findViewById(R.id.camera);
        kameraBtn = findViewById(R.id.kameraBtn);

        //Kamera Fonksiyonları
        kamera.setLifecycleOwner(this);
        kamera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);

                String konum = ResimlerClass.getInstance(KameraActivity.this).MedyaKonumu()+"/";
                File klasor = new File(konum);
                konum = konum+System.currentTimeMillis()+ResimlerClass.VarsayilanResimUzantisi;
                File dosya = new File(konum);
                try {
                    if (!klasor.exists()) {
                        boolean b = klasor.mkdirs();
                    }
                    if (!dosya.exists()){
                        boolean b2 = dosya.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(dosya);
                    fos.write(result.getData());
                    fos.close();
                    Intent intent = new Intent(Veritabani.FotografCek);
                    intent.putExtra(Veritabani.Fotograf, dosya.getAbsolutePath());
                    LocalBroadcastManager.getInstance(KameraActivity.this).sendBroadcast(intent);
                    Geri();
                } catch (IOException e) {
                    Toast.makeText(KameraActivity.this, getString(R.string.could_not_take_a_photo), Toast.LENGTH_SHORT).show();
                    Geri();
                }
            }

            @Override
            public void onVideoTaken(@NonNull VideoResult result) {
                super.onVideoTaken(result);
            }
        });
        kameraBtn.setOnClickListener(v -> kamera.takePicture());

        //Flash
        flashBtn = findViewById(R.id.flashBtn);
        FLASH_DURUMU = SharedPreference.getInstance(KameraActivity.this).GetirLongOzel(TAG, FLASH, FLASH_ACIK);
        FlashResmiDegistir(FLASH_DURUMU);
        flashBtn.setOnClickListener(v -> FlashDurumuDegistir(FLASH_DURUMU));
    }

    private void FlashResmiDegistir(long flash_durumu) {
        if (flash_durumu == FLASH_KAPALI){
            flashBtn.setImageResource(R.drawable.ic_baseline_flash_off_24);
        }else if(flash_durumu == FLASH_OTO){
            flashBtn.setImageResource(R.drawable.ic_baseline_flash_auto_24);
        }else {
            flashBtn.setImageResource(R.drawable.ic_baseline_flash_on_24);
        }
    }
    private void FlashDurumuDegistir(long flash_durumu){
        if (flash_durumu == FLASH_KAPALI){
            FLASH_DURUMU = FLASH_ACIK;
            kamera.setFlash(Flash.ON);
            SharedPreference.getInstance(KameraActivity.this).KaydetLongOzel(TAG, FLASH, FLASH_ACIK);
        }else if(flash_durumu == FLASH_OTO){
            FLASH_DURUMU = FLASH_KAPALI;
            kamera.setFlash(Flash.OFF);
            SharedPreference.getInstance(KameraActivity.this).KaydetLongOzel(TAG, FLASH, FLASH_KAPALI);
        }else {
            FLASH_DURUMU = FLASH_OTO;
            kamera.setFlash(Flash.AUTO);
            SharedPreference.getInstance(KameraActivity.this).KaydetLongOzel(TAG, FLASH, FLASH_OTO);
        }
        FlashResmiDegistir(FLASH_DURUMU);
    }

    private void Geri() {
        finish();
        overridePendingTransition(R.anim.yukaridan_asagi_giris, R.anim.yukaridan_asagi_cikis);
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