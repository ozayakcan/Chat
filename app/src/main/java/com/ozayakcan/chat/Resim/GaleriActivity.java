package com.ozayakcan.chat.Resim;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ozayakcan.chat.Adapter.ResimAdapter;
import com.ozayakcan.chat.Model.Resim;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.passiondroid.imageeditorlib.ImageEditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GaleriActivity extends AppCompatActivity {

    private GridView resimlerGW;
    private Executor executor;
    private Handler handler;

    private List<Resim> resimlerList;
    private ResimAdapter resimAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeri);
        resimlerGW = findViewById(R.id.resimlerGW);
        String kullanici = getIntent().getStringExtra(Veritabani.IsimKey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());
        TextView baslik = findViewById(R.id.baslik);
        if (kullanici != null){
            baslik.setText(getString(R.string.send_s).replace("%s", kullanici));
        }
        resimlerList = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        if (Izinler.getInstance(GaleriActivity.this).KontrolEt(Manifest.permission.READ_EXTERNAL_STORAGE)){
            ResimleriGoster();
        }else{
            Izinler.getInstance(GaleriActivity.this).Sor(Manifest.permission.READ_EXTERNAL_STORAGE, dosyaIzniResultLauncher);
        }
    }
    @SuppressWarnings("deprecation")
    private void ResimleriGoster(){
        executor.execute(() -> {
            resimlerList.clear();
            Cursor cursor;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Codigo version de api 29 en adelante

                cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID},
                        null,
                        null,
                        MediaStore.Images.Media.DATE_MODIFIED + " DESC"
                );

                if (null == cursor) {
                    return;
                }

                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        resimlerList.add(new Resim(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(id)).toString()));
                    } while (cursor.moveToNext());
                }
            } else {

                cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Images.Media.DATE_MODIFIED + " DESC"
                );

                if (null == cursor) {
                    return;
                }

                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        resimlerList.add(new Resim(cursor.getString(id)));

                    } while (cursor.moveToNext());
                }

            }
            cursor.close();
            handler.post(() -> {
                resimAdapter = new ResimAdapter(GaleriActivity.this, resimlerList);
                resimlerGW.setAdapter(resimAdapter);
                resimlerGW.setOnItemClickListener((parent, view, position, id) -> {
                    if (resimlerList.size() > position){
                        Resim resim = resimlerList.get(position);
                        ResimGonder(resim.getKonum());
                    }
                });
                resimAdapter.notifyDataSetChanged();
                resimlerGW.invalidate();
            });
        });
    }
    ActivityResultLauncher<String> dosyaIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    ResimleriGoster();
                }else{
                    Toast.makeText(GaleriActivity.this, getString(R.string.you_must_grant_file_permission), Toast.LENGTH_SHORT).show();
                    Geri();
                }
            });
    public void ResimGonder(String resimKonumu){
        if (Izinler.getInstance(GaleriActivity.this).KontrolEt(Manifest.permission.READ_EXTERNAL_STORAGE)){
            File resim = new File(resimKonumu);
            if (resim.exists()){
                String kopyalanacakKlasor = ResimlerClass.getInstance(GaleriActivity.this).MedyaKonumu()+"/";
                File yeniKonum = new File(kopyalanacakKlasor+System.currentTimeMillis()+ResimlerClass.VarsayilanResimUzantisi);
                ResimlerClass.getInstance(GaleriActivity.this).Kopyala(resim, yeniKonum, kopyalanacakKlasor, new ResimlerClass.KopyalaListener() {
                    @Override
                    public void Tamamlandi(String konum) {
                        new ImageEditor.Builder(GaleriActivity.this, konum).setStickerAssets(ResimlerClass.Sticker_Dosya_Adi).open();
                    }

                    @Override
                    public void Tamamlanamadi(String hata) {
                        Toast.makeText(GaleriActivity.this, getString(R.string.image_could_not_open), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(GaleriActivity.this, getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
            }
        }else{
            Izinler.getInstance(GaleriActivity.this).Sor(Manifest.permission.READ_EXTERNAL_STORAGE, dosyaIzniResultLauncher);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ImageEditor.RC_IMAGE_EDITOR){
            if (resultCode == Activity.RESULT_OK && data != null){
                Intent intent = new Intent(Veritabani.FotografCek);
                intent.putExtra(Veritabani.Fotograf, data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH));
                LocalBroadcastManager.getInstance(GaleriActivity.this).sendBroadcast(intent);
                Geri();
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        ResimleriGoster();
    }
}