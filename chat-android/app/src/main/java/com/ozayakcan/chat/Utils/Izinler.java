package com.ozayakcan.chat.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ozayakcan.chat.R;

public class Izinler {

    public static final int PROFIL_RESMI_KAMERA_IZIN_KODU = 1920;
    public static final int PROFIL_RESMI_DOSYA_IZIN_KODU = 1453;

    public static final int RESIM_YUKLE_SONUC = 1923;

    private final Context mContext;

    public Izinler(Context context) {mContext = context;}

    public boolean KontrolEt(String izin){
        if(ContextCompat.checkSelfPermission(mContext, izin) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }
    public void Sor(String izinler, int IZIN_KODU){
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{izinler}, IZIN_KODU);
    }
    public void ZorunluIzinUyariKutusu(String izinler, int IZIN_KODU){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(R.string.permission_denied);
        builder.setMessage(R.string.you_must_grant_required_permissions);
        builder.setPositiveButton(R.string.grant, (dialog, which) -> {
            Sor(izinler, IZIN_KODU);
        });
        builder.setNegativeButton(R.string.quit, (dialog, which) -> {
            ((Activity) mContext).finish();
            System.exit(0);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
