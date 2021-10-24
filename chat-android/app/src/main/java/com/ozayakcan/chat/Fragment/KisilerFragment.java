package com.ozayakcan.chat.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.R;

public class KisilerFragment extends Fragment {

    private Izinler izinler;
    private View view;
    public KisilerFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kisiler, container, false);
        izinler = new Izinler(getContext());
        if (izinler.KontrolEt(Manifest.permission.READ_CONTACTS)){
            KisileriBul();
        }else{
            izinler.SorYeniApi(Manifest.permission.READ_CONTACTS, kisiIzniResultLauncher);
        }
        return view;
    }

    ActivityResultLauncher<String> kisiIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    KisileriBul();
                }else{
                    KisiIzniUyariKutusu();
                }
            });

    private void KisiIzniUyariKutusu() {
        izinler.ZorunluIzinUyariKutusuYeniApi(Manifest.permission.READ_CONTACTS, kisiIzniResultLauncher);
    }

    private void KisileriBul(){

    }
}