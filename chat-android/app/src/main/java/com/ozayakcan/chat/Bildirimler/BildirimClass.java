package com.ozayakcan.chat.Bildirimler;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.MesajActivity;
import com.ozayakcan.chat.Model.BildirimMesaj;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.virgilsecurity.android.ethree.interaction.EThree;
import com.virgilsecurity.common.callback.OnResultListener;
import com.virgilsecurity.sdk.cards.Card;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BildirimClass {

    private final Context mContext;
    private EThree eThree;
    private BildirimClass(Context context, EThree eThree){
        this.mContext = context;
        this.eThree = eThree;
    }

    public static synchronized BildirimClass getInstance(Context context, EThree eThree){
        return new BildirimClass(context, eThree);
    }

    public static String FCM_URL = "https://fcm.googleapis.com/";
    public static String BildirimTuruKey = "bildirimTuru";
    public static String MesajKey = "mesaj";
    public static int MesajBildirimiID = 1923;
    public static int MaxMesajSayisi = 7;

    public void MesajBildirimi() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && firebaseUser.getPhoneNumber() != null){
            DatabaseReference mesajKisileriRef= FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(firebaseUser.getPhoneNumber());
            mesajKisileriRef.keepSynced(true);
            mesajKisileriRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot mesajKisileriSnapshot) {
                    List<BildirimMesaj> bildirimMesajList = new ArrayList<>();
                    long mesajKisiSayisi = 0;
                    for (DataSnapshot mesajKisileriDataSnapshot : mesajKisileriSnapshot.getChildren()){
                        mesajKisiSayisi++;
                        long finalMesajKisiSayisi = mesajKisiSayisi;
                        if (mesajKisileriDataSnapshot.getKey() != null){
                            DatabaseReference kullaniyicibul = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(mesajKisileriDataSnapshot.getKey());
                            kullaniyicibul.keepSynced(true);
                            kullaniyicibul.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot kullaniciSnapshot) {
                                    Kullanici kullanici = kullaniciSnapshot.getValue(Kullanici.class);
                                    DatabaseReference kisiyiBul = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.KisiTablosu).child(mesajKisileriDataSnapshot.getKey());
                                    kisiyiBul.keepSynced(true);
                                    kisiyiBul.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot kisiSnapshot) {
                                            Kullanici kisi = kisiSnapshot.getValue(Kullanici.class);
                                            Kullanici asilKullanici = kullanici;
                                            String isim = mesajKisileriDataSnapshot.getKey();
                                            if (kisi != null){
                                                isim = kisi.getIsim();
                                                asilKullanici = kisi;
                                            }
                                            DatabaseReference mesajlarRef = mesajKisileriDataSnapshot.getRef();
                                            mesajlarRef.keepSynced(true);
                                            Kullanici sonAsilKullanici = asilKullanici;
                                            String sonIsim = isim;
                                            mesajlarRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot mesajlarSnapshot) {
                                                    eThree.findUser(sonAsilKullanici.getID()).addCallback(new OnResultListener<Card>() {
                                                        @Override
                                                        public void onSuccess(Card card) {
                                                            long mesajSayisi = 0;
                                                            for (DataSnapshot mesajlarDataSnapshot : mesajlarSnapshot.getChildren()){
                                                                Mesaj mesaj = mesajlarDataSnapshot.getValue(Mesaj.class);
                                                                if (mesaj != null && !mesaj.isGonderen() && !mesaj.isGoruldu() && sonAsilKullanici != null){
                                                                    mesajSayisi++;
                                                                    String cozulenMesaj;
                                                                    if (card != null){
                                                                        cozulenMesaj = eThree.authDecrypt(mesaj.getMesaj(), card);
                                                                    }else{
                                                                        cozulenMesaj = mContext.getString(R.string.this_message_could_not_be_decrypted);
                                                                    }
                                                                    BildirimMesaj bildirimMesaj = new BildirimMesaj(sonAsilKullanici.getID(), sonIsim, sonAsilKullanici.getProfilResmi(), sonAsilKullanici.getTelefon(), cozulenMesaj, mesaj.getTarih(), mesajSayisi);
                                                                    bildirimMesajList.add(bildirimMesaj);
                                                                }
                                                            }
                                                            if (mesajKisileriSnapshot.getChildrenCount() == finalMesajKisiSayisi && bildirimMesajList.size() > 0){
                                                                MesajBildirimiGoster(bildirimMesajList);
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(@NonNull Throwable throwable) {
                                                            long mesajSayisi = 0;
                                                            for (DataSnapshot mesajlarDataSnapshot : mesajlarSnapshot.getChildren()){
                                                                Mesaj mesaj = mesajlarDataSnapshot.getValue(Mesaj.class);
                                                                if (mesaj != null && !mesaj.isGonderen() && !mesaj.isGoruldu() && sonAsilKullanici != null){
                                                                    mesajSayisi++;
                                                                    BildirimMesaj bildirimMesaj = new BildirimMesaj(sonAsilKullanici.getID(), sonIsim, sonAsilKullanici.getProfilResmi(), sonAsilKullanici.getTelefon(), mContext.getString(R.string.this_message_could_not_be_decrypted), mesaj.getTarih(), mesajSayisi);
                                                                    bildirimMesajList.add(bildirimMesaj);
                                                                }
                                                            }
                                                            if (mesajKisileriSnapshot.getChildrenCount() == finalMesajKisiSayisi && bildirimMesajList.size() > 0){
                                                                MesajBildirimiGoster(bildirimMesajList);
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void MesajBildirimiGoster(List<BildirimMesaj> bildirimMesajList) {
        Collections.sort(bildirimMesajList);
        List<BildirimMesaj> bildirimMesajSayisi;
        bildirimMesajSayisi = bildirimMesajList;
        Collections.sort(bildirimMesajSayisi, (o1, o2) -> Long.compare(o2.getMesajSayisi(), o1.getMesajSayisi()));
        Intent acilacakActivity;
        if (bildirimMesajSayisi.get(0).getMesajSayisi() == bildirimMesajList.size()){
            acilacakActivity = new Intent(mContext, MesajActivity.class);
            acilacakActivity.putExtra(Veritabani.IDKey, bildirimMesajList.get(0).getID());
            acilacakActivity.putExtra(Veritabani.IsimKey, bildirimMesajList.get(0).getIsim());
            acilacakActivity.putExtra(Veritabani.TelefonKey, bildirimMesajList.get(0).getTelefon());
            acilacakActivity.putExtra(Veritabani.ProfilResmiKey, bildirimMesajList.get(0).getProfilResmi());
            acilacakActivity.putExtra(Veritabani.MesajTablosu, Veritabani.MesajTablosu);
        }else{
            acilacakActivity = new Intent(mContext, MainActivity.class);
        }
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(
                    mContext,
                    MesajBildirimiID,
                    acilacakActivity,
                    PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_ONE_SHOT
            );
        }else{
            pendingIntent = PendingIntent.getActivity(
                    mContext,
                    MesajBildirimiID,
                    acilacakActivity,
                    PendingIntent.FLAG_ONE_SHOT
            );
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,MesajKey);
        Notification notification;
        if (bildirimMesajSayisi.get(0).getMesajSayisi() == bildirimMesajList.size()){
            mBuilder.setSmallIcon(R.drawable.varsayilan_bildirim_simgesi)
                    .setTicker(bildirimMesajList.get(0).getTelefon())
                    .setContentTitle(bildirimMesajList.get(0).getIsim());
        }else{
            mBuilder.setSmallIcon(R.drawable.varsayilan_bildirim_simgesi).setContentTitle(mContext.getString(R.string.app_name));
        }
        mBuilder.setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        if (bildirimMesajSayisi.get(0).getMesajSayisi() == bildirimMesajList.size()){
            if (bildirimMesajList.size() > 1){
                mBuilder.setStyle(cokluBildirim(bildirimMesajList, true));
            }
        }else{
            mBuilder.setStyle(cokluBildirim(bildirimMesajList, false));
        }
        if (bildirimMesajSayisi.get(0).getMesajSayisi() == bildirimMesajList.size()){
            int bildirimID = R.drawable.ic_profil_resmi;
            Bitmap profilResmi;
            if (!bildirimMesajList.get(0).getProfilResmi().equals(Veritabani.VarsayilanDeger)){
                InputStream inputStream;
                try {
                    URL kisiUrl = new URL(bildirimMesajList.get(0).getProfilResmi());
                    HttpURLConnection httpURLConnection = (HttpURLConnection) kisiUrl.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    profilResmi = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    profilResmi = BitmapFactory.decodeResource(mContext.getResources(), bildirimID);
                }
            }else{
                profilResmi = BitmapFactory.decodeResource(mContext.getResources(), bildirimID);
            }
            mBuilder.setLargeIcon(profilResmi);
        }
        if (bildirimMesajList.size() > 1){
            mBuilder.setContentText(mContext.getResources().getString(R.string.s_new_messages).replace("%s", bildirimMesajList.size()+""));
        }else {
            mBuilder.setContentText(bildirimMesajList.get(0).getMesaj());
        }
        mBuilder.setOnlyAlertOnce(false);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setVibrate(new long[] {NotificationCompat.DEFAULT_VIBRATE, 0, NotificationCompat.DEFAULT_VIBRATE, 0});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri varsayilanBildirimSesi = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(varsayilanBildirimSesi);
        notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MesajKey,
                    MesajKey,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(MesajKey);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(MesajBildirimiID, notification);
    }

    private NotificationCompat.InboxStyle cokluBildirim(List<BildirimMesaj> bildirimMesajList, boolean birKisi) {
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        int baslangic = bildirimMesajList.size() - 1;
        if (bildirimMesajList.size() > MaxMesajSayisi){
            baslangic = MaxMesajSayisi - 1;
        }
        for (int i = baslangic; i >= 0; i--) {
            if (birKisi){
                inbox.addLine(bildirimMesajList.get(i).getMesaj());
            }else{
                inbox.addLine(bildirimMesajList.get(i).getIsim() + ": " + bildirimMesajList.get(i).getMesaj());
            }
        }
        if (birKisi){
            inbox.setBigContentTitle(bildirimMesajList.get(0).getIsim());
        }else{
            inbox.setBigContentTitle(mContext.getString(R.string.app_name));
        }
        inbox.setSummaryText(mContext.getString(R.string.s_new_messages).replace("%s", bildirimMesajList.size()+""));
        return inbox;
    }
    public static void MesajBildiriminiKaldir(Context mContext){
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MesajKey,
                    MesajKey,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(MesajKey);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.cancel(MesajBildirimiID);
    }
}
