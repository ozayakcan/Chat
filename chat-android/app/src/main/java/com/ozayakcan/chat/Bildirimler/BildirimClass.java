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
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.MesajActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BildirimClass {

    private final Context mContext;
    private BildirimClass(Context context){
        this.mContext = context;
    }

    public static synchronized BildirimClass getInstance(Context context){
        return new BildirimClass(context);
    }

    public static String FCM_URL = "https://fcm.googleapis.com/";
    public static String BildirimTuruKey = "bildirimTuru";
    public static String MesajKey = "mesaj";
    public static String MesajBildirimiIO = "0";

    public void MesajBildirimi() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && firebaseUser.getPhoneNumber() != null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(firebaseUser.getPhoneNumber());
            databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int sira = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        sira++;
                        if (dataSnapshot.getKey() != null){
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.KisiTablosu).child(dataSnapshot.getKey());
                            databaseReference1.keepSynced(true);
                            int finalSira = sira;
                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    Kullanici kullanici = snapshot1.getValue(Kullanici.class);
                                    String isim = dataSnapshot.getKey();
                                    if (kullanici != null){
                                        isim = kullanici.getIsim();
                                    }
                                    String sonIsim = isim;
                                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(dataSnapshot.getKey());
                                    databaseReference2.keepSynced(true);
                                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                            Kullanici kullanici1 = snapshot2.getValue(Kullanici.class);
                                            if (kullanici1 != null){
                                                DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(firebaseUser.getPhoneNumber()).child(dataSnapshot.getKey());
                                                databaseReference3.keepSynced(true);
                                                databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapsho3) {
                                                        List<Mesaj> mesajList = new ArrayList<>();
                                                        for (DataSnapshot dataSnapshot1 : snapsho3.getChildren()){
                                                            Mesaj mesaj = dataSnapshot1.getValue(Mesaj.class);
                                                            if (mesaj != null && !mesaj.isGonderen() && !mesaj.isGoruldu()){
                                                                mesajList.add(mesaj);
                                                            }
                                                        }
                                                        if (mesajList.size() > 0){
                                                            MesajBildirimiGoster(kullanici1, mesajList, sonIsim, finalSira);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
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
    private void MesajBildirimiGoster(Kullanici kullanici, List<Mesaj> mesajList, String isim, int sira) {
        Intent acilacakActivity = new Intent(mContext, MesajActivity.class);
        acilacakActivity.putExtra(Veritabani.IDKey, kullanici.getID());
        acilacakActivity.putExtra(Veritabani.IsimKey, isim);
        acilacakActivity.putExtra(Veritabani.TelefonKey, kullanici.getTelefon());
        acilacakActivity.putExtra(Veritabani.ProfilResmiKey, kullanici.getProfilResmi());
        acilacakActivity.putExtra(Veritabani.MesajTablosu, Veritabani.MesajTablosu);
        int id  = Integer.parseInt(MesajBildirimiIO+sira);
        PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resultPendingIntent = PendingIntent.getActivity(
                    mContext,
                    id,
                    acilacakActivity,
                    PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT
            );
        }else{
            resultPendingIntent = PendingIntent.getActivity(
                    mContext,
                    id,
                    acilacakActivity,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,MesajKey);
        Notification notification;
        mBuilder.setSmallIcon(R.drawable.varsayilan_bildirim_simgesi).setTicker(kullanici.getTelefon()).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(isim);
        if (mesajList.size() > 1){
            mBuilder.setStyle(cokluBildirim(isim, mesajList));
        }
        int bildirimID = R.drawable.ic_profil_resmi;
        Bitmap profilResmi;
        if (!kullanici.getProfilResmi().equals(Veritabani.VarsayilanDeger)){
            InputStream inputStream;
            try {
                URL kisiUrl = new URL(kullanici.getProfilResmi());
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
        if (mesajList.size() > 1){
            mBuilder.setContentText(mContext.getResources().getString(R.string.s_new_messages).replace("%s", mesajList.size()+""));
        }else {
            mBuilder.setContentText(mesajList.get(0).getMesaj());
        }
        mBuilder.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        mBuilder.setOnlyAlertOnce(false);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setVibrate(new long[] {NotificationCompat.DEFAULT_VIBRATE, 0, NotificationCompat.DEFAULT_VIBRATE, 0});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri varsayilanBildirimSesi = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(varsayilanBildirimSesi);
        notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MesajKey,
                    MesajKey,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(MesajKey);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(id, notification);
    }

    private NotificationCompat.InboxStyle cokluBildirim(String isim, List<Mesaj> mesajList){
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        for (int i = 0; i < mesajList.size(); i++){
            inbox.addLine(mesajList.get(i).getMesaj());
        }
        inbox.setBigContentTitle(isim).setSummaryText(mContext.getString(R.string.s_new_messages).replace("%s", mesajList.size()+""));
        return inbox;
    }
}
