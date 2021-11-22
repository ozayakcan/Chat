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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

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
        Toast.makeText(getApplicationContext(), baglandi ? "İnternet bağlantısı kuruldu." : "İnternet bağlantısı kesildi.", Toast.LENGTH_SHORT).show();
    }

}
