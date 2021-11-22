package com.ozayakcan.chat.Baglanti;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Bildirimler.BildirimClass;
import com.ozayakcan.chat.Bildirimler.BildirimServisi;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Ozellik.MesajFonksiyonlari;
import com.ozayakcan.chat.Ozellik.Veritabani;

import java.util.List;

public class BaglantiServisi extends JobService implements BaglantiReceiver.BaglantiListener {

    private static final String TAG = BaglantiServisi.class.getSimpleName();

    private BaglantiReceiver baglantiReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Servis oluşturuldu");
        baglantiReceiver = new BaglantiReceiver(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void ServisiBaslat(Context context) {
        JobInfo jobInfo = new JobInfo.Builder(0, new ComponentName(context, BaglantiServisi.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob " + baglantiReceiver);
        registerReceiver(baglantiReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob");
        unregisterReceiver(baglantiReceiver);
        return false;
    }

    @Override
    public void Degisti(boolean baglandi) {
        if(baglandi){
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                List<String> kisiler = MesajFonksiyonlari.getInstance(getApplicationContext()).BildirimGonderilecekKisiler();
                if (kisiler.size() > 0){
                    for (String kisi : kisiler){
                        Log.d(TAG, kisi);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(kisi);
                        databaseReference.keepSynced(true);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                                if (kullanici != null){
                                    BildirimClass.MesajBildirimiYolla(kullanici.getFcmToken(), new BildirimClass.BildirimListener() {
                                        @Override
                                        public void Gonderildi() {
                                            List<Mesaj> mesajList = MesajFonksiyonlari.getInstance(getApplicationContext()).MesajlariGetir(kisi, MesajFonksiyonlari.KaydedilecekTur);
                                            for (int i = mesajList.size()-1; i >= 0; i--){
                                                Mesaj mesaj = mesajList.get(i);
                                                if (mesaj.isGonderen()){
                                                    if (mesaj.getMesajDurumu() == Veritabani.MesajDurumuGonderiliyor){
                                                        mesaj.setMesajDurumu(Veritabani.MesajDurumuGonderildi);
                                                        mesajList.set(i, mesaj);
                                                    }else{
                                                        break;
                                                    }
                                                }
                                            }
                                            MesajFonksiyonlari.getInstance(getApplicationContext()).MesajDuzenle(kisi, mesajList);
                                            MesajFonksiyonlari.getInstance(getApplicationContext()).BildirimGonderilecekKisiyiSil(kisi);
                                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(BildirimClass.MesajKey));
                                        }

                                        @Override
                                        public void Gonderilmedi() {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }, 3000);
        }
    }

}
