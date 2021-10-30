package com.ozayakcan.chat.Bildirimler;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.ozayakcan.chat.Ozellik.Veritabani;

public class BildirimServisi extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Veritabani veritabani = new Veritabani(this);
            veritabani.TokenKaydet(firebaseUser, s);
        }
    }
}
