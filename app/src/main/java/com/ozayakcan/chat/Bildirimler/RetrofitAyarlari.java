package com.ozayakcan.chat.Bildirimler;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitAyarlari {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAs3t8a5g:APA91bH3RimLmKlwDpDCXKFZ3azDurqmvJH9Pn-ppOf0ZuCZdcwbPys3Miq_-qzyGh5lkBDFJvrGNwaJ9S7e_Ijexa4S13ydZ7pFe5IyQ086CaZt8zFnf-5iXUfi0WbC59dniUKyvu4x"
            }
    )

    @POST("fcm/send")
    Call<Sonuc> bildirimGonder(@Body Gonder body);
}
