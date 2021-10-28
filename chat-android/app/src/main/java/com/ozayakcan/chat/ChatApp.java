package com.ozayakcan.chat;

import android.app.Application;
import android.content.Context;
import android.text.format.DateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatApp extends Application {

    private static Context appContext;
    public static int MaxMesajKarakterSayisi = 20;
    public static String TarihSaatFormati = "dd/MM/yyyy HH:mm";
    public static String TarihFormati = "dd/MM/yyyy";
    public static String SaatFormati = "HH:mm";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso build = builder.build();
        build.setIndicatorsEnabled(false);
        build.setLoggingEnabled(true);
        Picasso.setSingletonInstance(build);
        appContext = getApplicationContext();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Veritabani veritabani = new Veritabani(appContext);
        if (firebaseUser != null){
            veritabani.DurumKontrol(firebaseUser);
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static String MesajBol(String mesaj, int bolunecek){
        List<String> bolunecekList = new ArrayList<>();
        int index = 0;
        while (index < mesaj.length()) {
            bolunecekList.add(mesaj.substring(index, Math.min(index + bolunecek,mesaj.length())));
            index += bolunecek;
        }
        return bolunecekList.get(0)+"...";
    }
    public static String MesajTarihiBul(long tarih){
        String tarihStr = DateFormat.format(TarihFormati, tarih).toString();
        String bugunTarih = DateFormat.format(TarihFormati, System.currentTimeMillis()).toString();
        String dunTarih = DateFormat.format(TarihFormati, System.currentTimeMillis() - (24 * 60 * 60 * 1000)).toString();
        String saat = DateFormat.format(SaatFormati, tarih).toString();
        if (tarihStr.equals(bugunTarih)){
            return saat;
        }else if (tarihStr.equals(dunTarih)){
            return getAppContext().getString(R.string.yesterday)+" "+saat;
        }else{
            return tarihStr;
        }
    }
}
