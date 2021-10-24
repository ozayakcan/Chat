package com.ozayakcan.chat;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class ChatApp extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        InitResim();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void InitFirebase(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    public void InitResim(){
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso build = builder.build();
        build.setIndicatorsEnabled(false);
        build.setLoggingEnabled(true);
        Picasso.setSingletonInstance(build);
    }
}
