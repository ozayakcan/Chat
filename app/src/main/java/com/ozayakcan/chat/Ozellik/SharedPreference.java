package com.ozayakcan.chat.Ozellik;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharedPreference {

    public static final String kullaniciKaydedildi = "Kayit";
    private final Context mContext;
    private static final String SHARED_PREF_ADI = "Chat";

    public SharedPreference(Context context) {mContext = context;}


    public void KaydetString(String key, String deger){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, deger);

        editor.apply();
    }

    public String GetirString(String key, String varsayilanDeger){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        return sharedPreference.getString(key, varsayilanDeger);
    }

    public void KaydetBoolean(String key, boolean deger){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean(key, deger);
        editor.apply();
    }
    public boolean GetirBoolean(String key, boolean varsayilanDeger){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        return sharedPreference.getBoolean(key, varsayilanDeger);
    }

    public void KaydetLong(String key, long deger){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putLong(key, deger);
        editor.apply();
    }
    public long GetirLong(String key, long varsayilanDeger){
        SharedPreferences sharedPreference = mContext.getSharedPreferences(SHARED_PREF_ADI, MODE_PRIVATE);
        return sharedPreference.getLong(key, varsayilanDeger);
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
