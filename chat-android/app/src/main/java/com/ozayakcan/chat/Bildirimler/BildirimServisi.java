package com.ozayakcan.chat.Bildirimler;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ozayakcan.chat.Ozellik.E3KitKullanici;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.virgilsecurity.android.common.model.EThreeParams;
import com.virgilsecurity.android.ethree.interaction.EThree;

public class BildirimServisi extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Veritabani veritabani = new Veritabani(getApplicationContext());
            veritabani.TokenKaydet(firebaseUser, s);
        }
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser.getPhoneNumber() != null){
            SharedPreference sharedPreference = new SharedPreference(getApplicationContext());
            if (sharedPreference.GetirString(E3KitKullanici.VirgilTokenKey, "").equals("")){
                E3KitKullanici e3KitKullanici = new E3KitKullanici(getApplicationContext(), firebaseUser.getPhoneNumber());
                new Thread(() -> e3KitKullanici.KullaniciyiGetir(new E3KitKullanici.Tamamlandi() {
                    @Override
                    public void Basarili(EThree kullanici) {
                        BildirimiAyarla(remoteMessage, kullanici);
                    }

                    @Override
                    public void Basarisiz(Throwable hata) {
                        Log.e("Chatapp", "Başarısız: "+hata.getMessage());
                    }
                })).start();
            }else{
                EThreeParams eThreeParams = new EThreeParams(firebaseUser.getPhoneNumber(),
                        () -> sharedPreference.GetirString(E3KitKullanici.VirgilTokenKey, ""),
                        getApplicationContext());
                EThree eThree = new EThree(eThreeParams);
                BildirimiAyarla(remoteMessage, eThree);
            }
        }
    }
    private void BildirimiAyarla(RemoteMessage remoteMessage, EThree eThree){
        if (remoteMessage.getData().size() > 0){
            String bildirimTuru = remoteMessage.getData().get(BildirimClass.BildirimTuruKey);
            if (bildirimTuru != null && bildirimTuru.equals(BildirimClass.MesajKey)){
                BildirimClass.getInstance(getApplicationContext(), eThree).MesajBildirimi();
            }
        }
    }
}
