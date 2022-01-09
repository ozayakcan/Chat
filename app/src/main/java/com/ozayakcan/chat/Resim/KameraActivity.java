package com.ozayakcan.chat.Resim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

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
                Intent intent = new Intent(Veritabani.FotografCek);
                intent.putExtra(Veritabani.Fotograf, result.getData());
                LocalBroadcastManager.getInstance(KameraActivity.this).sendBroadcast(intent);
                Geri();
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