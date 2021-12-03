package com.ozayakcan.chat.Bildirimler;

import com.ozayakcan.chat.BuildConfig;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitAyarlari {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key="+BuildConfig.FCM_KEY
            }
    )

    @POST("fcm/send")
    Call<Sonuc> bildirimGonder(@Body Gonder body);
}
