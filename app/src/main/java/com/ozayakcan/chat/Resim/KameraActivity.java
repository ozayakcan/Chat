package com.ozayakcan.chat.Resim;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.AudioCodec;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.controls.VideoCodec;
import com.ozayakcan.chat.MesajActivity;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.passiondroid.imageeditorlib.ImageEditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class KameraActivity extends AppCompatActivity {

    private final String TAG = "Kamera";

    private final String FLASH = "flash";

    private final int FLASH_KAPALI = 0;
    private final int FLASH_ACIK = 1;
    private final int FLASH_OTO = 2;

    private long FLASH_DURUMU = FLASH_ACIK;

    private final String KAMERA_YONU = "kamera_yonu";

    private final long KAMERA_ARKA = 0;
    private final long KAMERA_ON = 1;

    private long KAMERA_DURUMU = KAMERA_ARKA;

    private int VIDEO_SURESI = 30000;
    private String VIDEO_DOSYA_ADI = System.currentTimeMillis()+".mp4";

    CameraView kamera;
    private ImageView flashBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kamera);
        kamera = findViewById(R.id.camera);
        KameraButonView kameraBtn = findViewById(R.id.kameraBtn);
        if (Izinler.getInstance(KameraActivity.this).KontrolEt(Manifest.permission.READ_EXTERNAL_STORAGE)){
            String medyaKlasoru = ResimlerClass.getInstance(KameraActivity.this).MedyaKonumu()+"/";
            File klasor = new File(medyaKlasoru);
            if (klasor.exists()){
                MedyalariSil(klasor);
            }
        }
        //Kamera Fonksiyonları
        kamera.setLifecycleOwner(this);
        kamera.setVideoMaxDuration(VIDEO_SURESI);
        kamera.setVideoCodec(VideoCodec.DEVICE_DEFAULT);
        kamera.setAudioCodec(AudioCodec.DEVICE_DEFAULT);
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
                        Log.d(TAG, b+"");
                    }
                    if (!dosya.exists()){
                        boolean b2 = dosya.createNewFile();
                        Log.d(TAG, b2+"");
                    }
                    FileOutputStream fos = new FileOutputStream(dosya);
                    fos.write(result.getData());
                    fos.close();
                    new ImageEditor.Builder(KameraActivity.this, dosya.getAbsolutePath()).open();
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
        kameraBtn.setKameraButonListener(new KameraButonView.KameraButonListener() {
            @Override
            public void FotografCek() {
                kamera.setMode(Mode.PICTURE);
                kamera.takePicture();
            }

            @Override
            public void VideoyuBaslat() {
                /*kameraBtn.setBackground(ContextCompat.getDrawable(KameraActivity.this, R.drawable.yuvarlak_arkaplan_kirmizi));
                kamera.setMode(Mode.VIDEO);
                VIDEO_DOSYA_ADI = System.currentTimeMillis()+".mp4";
                kamera.takeVideoSnapshot(new File(ResimlerClass.getInstance(KameraActivity.this).MedyaKonumu()+"/"+VIDEO_DOSYA_ADI));*/
            }

            @Override
            public void VideoyuDurdur() {
                /*kamera.postDelayed(() -> {
                    kameraBtn.setBackground(ContextCompat.getDrawable(KameraActivity.this, R.drawable.yuvarlak_arkaplan_beyaz));
                    kamera.stopVideo();
                }, 500);*/
            }
        });

        //Flash
        flashBtn = findViewById(R.id.flashBtn);
        FLASH_DURUMU = SharedPreference.getInstance(KameraActivity.this).GetirLongOzel(TAG, FLASH, FLASH_ACIK);
        FlashiAyarla(FLASH_DURUMU);
        flashBtn.setOnClickListener(v -> FlashDegistir(FLASH_DURUMU));
        //Kamera Yönü
        ImageView kameraDegistirBtn = findViewById(R.id.kameraDegistirBtn);
        KAMERA_DURUMU = SharedPreference.getInstance(KameraActivity.this).GetirLongOzel(TAG, KAMERA_YONU, KAMERA_ARKA);
        KameraAyarla(KAMERA_DURUMU);
        kameraDegistirBtn.setOnClickListener(v -> KameraDegistir(KAMERA_DURUMU));
    }
    private void FlashiAyarla(long flash_durumu) {
        if (flash_durumu == FLASH_KAPALI){
            flashBtn.setImageResource(R.drawable.ic_baseline_flash_off_24);
            kamera.setFlash(Flash.OFF);
        }else if(flash_durumu == FLASH_OTO){
            flashBtn.setImageResource(R.drawable.ic_baseline_flash_auto_24);
            kamera.setFlash(Flash.AUTO);
        }else {
            flashBtn.setImageResource(R.drawable.ic_baseline_flash_on_24);
            kamera.setFlash(Flash.ON);
        }
        SharedPreference.getInstance(KameraActivity.this).KaydetLongOzel(TAG, FLASH, flash_durumu);
    }
    private void FlashDegistir(long flash_durumu){
        if (flash_durumu == FLASH_KAPALI){
            FLASH_DURUMU = FLASH_ACIK;
        }else if(flash_durumu == FLASH_OTO){
            FLASH_DURUMU = FLASH_KAPALI;
        }else {
            FLASH_DURUMU = FLASH_OTO;
        }
        FlashiAyarla(FLASH_DURUMU);
    }

    private void KameraAyarla(long kamera_durumu) {
        kamera.setFacing(kamera_durumu == KAMERA_ON ? Facing.FRONT : Facing.BACK);
        SharedPreference.getInstance(KameraActivity.this).KaydetLongOzel(TAG, KAMERA_YONU, kamera_durumu);
    }
    private void KameraDegistir(long kamera_durumu) {
        if (kamera_durumu == KAMERA_ON){
            KAMERA_DURUMU = KAMERA_ARKA;
        }else{
            KAMERA_DURUMU = KAMERA_ON;
        }
        KameraAyarla(KAMERA_DURUMU);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ImageEditor.RC_IMAGE_EDITOR){
            if (resultCode == Activity.RESULT_OK && data != null){
                Intent intent = new Intent(Veritabani.FotografCek);
                intent.putExtra(Veritabani.Fotograf, data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH));
                LocalBroadcastManager.getInstance(KameraActivity.this).sendBroadcast(intent);
                Geri();
            }
        }
    }

    private void MedyalariSil(File klasor) {
        if (klasor.isDirectory()){
            for (File dosya : klasor.listFiles()){
                MedyalariSil(dosya);
            }
        }
        boolean b = klasor.delete();
        Log.d("Silindi", b+"");
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