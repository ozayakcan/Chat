package com.ozayakcan.chat.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ozayakcan.chat.R;
import com.ozayakcan.chat.SSActivity;

public class Izinler {

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
