package com.ozayakcan.chat.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharedPreference {

    private static SharedPreference mInstance;
    private final Context mContext;
    private static final String SHARED_PREF_ADI = "Chat";

    private SharedPreference(Context context) {mContext = context;}

    public static synchronized SharedPreference getInstance(Context context) {
        if(mInstance == null){
            mInstance = new SharedPreference(context);
        }
        return mInstance;
    }

    public void KaydetString(String key, String value){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, value);

        editor.apply();
    }

    public String GetirString(String key, String defaultVal){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        return sharedPreference.getString(key, defaultVal);
    }

    public void KaydetBoolean(String key, boolean value){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public boolean GetirBoolean(String key, boolean defaultVal){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        return sharedPreference.getBoolean(key, defaultVal);
    }

    public void KaydetLong(String key, long value){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putLong(key, value);
        editor.apply();
    }
    public long GetirLong(String key, long defaultVal){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        return sharedPreference.getLong(key, defaultVal);
    }

    public void TumunuTemizle(){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.clear();
        editor.apply();
    }
    public void BiriniTemizle(String key){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        if (sharedPreference.contains(key)){
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.remove(key).apply();
        }
    }
    public void CokluTemizle(String baslayan){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreference.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(baslayan)) {
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.remove(key).apply();
            }
        }
    }

}
