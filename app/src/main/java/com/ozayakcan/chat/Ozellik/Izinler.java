package com.ozayakcan.chat.Ozellik;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.ozayakcan.chat.R;

public class Izinler {

    private final Context mContext;

    public Izinler(Context context) {mContext = context;}

    public boolean KontrolEt(String izin){
        return ContextCompat.checkSelfPermission(mContext, izin) == PackageManager.PERMISSION_GRANTED;
    }
    public void Sor(String izin, ActivityResultLauncher<String> kisiIzniResultLauncher){
       kisiIzniResultLauncher.launch(izin);
    }
    public void ZorunluIzinUyariKutusu(String izinler, ActivityResultLauncher<String> kisiIzniResultLauncher){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(R.string.permission_denied);
        builder.setMessage(R.string.you_must_grant_required_permissions);
        builder.setPositiveButton(R.string.grant, (dialog, which) -> Sor(izinler, kisiIzniResultLauncher));
        builder.setNegativeButton(R.string.dismiss, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
