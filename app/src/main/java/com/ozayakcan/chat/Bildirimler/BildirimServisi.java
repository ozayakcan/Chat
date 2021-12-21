package com.ozayakcan.chat.Bildirimler;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ozayakcan.chat.Ozellik.Veritabani;

public class BildirimServisi extends FirebaseMessagingService {

    private final String TAG = BildirimServisi.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Servis oluşturuldu");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Servis durduruldu");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e(TAG, "Yeni token oluşturuldu.");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Veritabani.getInstance(getApplicationContext()).TokenKaydet(firebaseUser, s);
        }
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "Bildirim alındı.");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            BildirimiAyarla(remoteMessage);
        }
    }
    private void BildirimiAyarla(RemoteMessage remoteMessage){
        if (remoteMessage.getData().size() > 0){
            String bildirimTuru = remoteMessage.getData().get(BildirimClass.BildirimTuruKey);
            if (bildirimTuru != null){
                if (bildirimTuru.equals(BildirimClass.MesajKey)){
                    BildirimClass.getInstance(getApplicationContext()).MesajBildirimi();
                }else if (bildirimTuru.equals(BildirimClass.GorulduKey)){
                    BildirimClass.getInstance(getApplicationContext()).GorulduGuncelle(remoteMessage.getData().get(BildirimClass.KisiKey));
                }
            }
        }
    }
}
