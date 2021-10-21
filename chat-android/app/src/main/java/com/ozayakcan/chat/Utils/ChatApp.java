package com.ozayakcan.chat.Utils;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class ChatApp {

    private static ChatApp mInstance;
    private final Context mContext;

    private ChatApp(Context context) {mContext = context;}

    public static synchronized ChatApp getInstance(Context context) {
        if(mInstance == null){
            mInstance = new ChatApp(context);
        }
        return mInstance;
    }

    public void Init(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.downloader(new OkHttp3Downloader(mContext, Integer.MAX_VALUE));
        Picasso build = builder.build();
        build.setIndicatorsEnabled(false);
        build.setLoggingEnabled(true);
        Picasso.setSingletonInstance(build);
    }
}
