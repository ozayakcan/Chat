package com.ozayakcan.chat.Resim;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class KameraActivity extends AppCompatActivity {

    CameraView kamera;
    private ImageView kameraBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kamera);
        kamera = findViewById(R.id.camera);
        kameraBtn = findViewById(R.id.kameraBtn);
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