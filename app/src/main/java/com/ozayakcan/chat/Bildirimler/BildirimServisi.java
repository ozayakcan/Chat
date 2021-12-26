package com.ozayakcan.chat.Bildirimler;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ozayakcan.chat.ChatApp;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Veritabani;

import java.util.Objects;

public class BildirimServisi extends FirebaseMessagingService {

    private final String TAG = BildirimServisi.class.getSimpleName();

    private FirebaseUser firebaseUser;
    @Override
    public void onCreate() {
        super.onCreate();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
            if (bildirimTuru != null) {
                if (bildirimTuru.equals(BildirimClass.MesajKey)) {
                    BildirimClass.getInstance(getApplicationContext()).MesajBildirimi();
                } else if (bildirimTuru.equals(BildirimClass.GorulduKey)) {
                    BildirimClass.getInstance(getApplicationContext()).GorulduGuncelle(remoteMessage.getData().get(BildirimClass.KisiKey));
                }else if(bildirimTuru.equals(BildirimClass.AramaKey)
                        || bildirimTuru.equals(BildirimClass.YanitlaKey)
                        || bildirimTuru.equals(BildirimClass.ReddetKey)
                        || bildirimTuru.equals(BildirimClass.MesgulKey)){
                    BildirimClass.getInstance(getApplicationContext()).Arama(firebaseUser, bildirimTuru, remoteMessage.getData().get(BildirimClass.KisiKey), remoteMessage.getData().get(BildirimClass.KameraKey) != null && Objects.equals(remoteMessage.getData().get(BildirimClass.KameraKey), "1"));
                }
            }
        }
    }
}
