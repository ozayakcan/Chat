package com.ozayakcan.chat.Baglanti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BaglantiReceiver extends BroadcastReceiver {
    private final BaglantiListener baglantiListener;

    BaglantiReceiver(BaglantiListener listener) {
        baglantiListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        baglantiListener.Degisti(baglandi(context));

    }

    public static boolean baglandi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public interface BaglantiListener {
        void Degisti(boolean baglandi);
    }
}
