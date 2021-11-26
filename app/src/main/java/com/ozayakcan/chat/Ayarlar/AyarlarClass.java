package com.ozayakcan.chat.Ayarlar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class AyarlarClass {

    private final Context mContext;

    public AyarlarClass(Context context){
        this.mContext = context;
    }

    public static AyarlarClass getInstance(Context context){
        return new AyarlarClass(context);
    }

    public void AyarlariAc(){
        Intent intent = new Intent(mContext, AyarlarActivity.class);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
        ((Activity) mContext).overridePendingTransition(0,0);
    }
}
