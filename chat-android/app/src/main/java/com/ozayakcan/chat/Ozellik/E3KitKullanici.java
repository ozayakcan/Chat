package com.ozayakcan.chat.Ozellik;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.virgilsecurity.android.common.model.java.EThreeParams;
import com.virgilsecurity.android.ethree.interaction.EThree;
import com.virgilsecurity.common.callback.OnCompleteListener;
import com.virgilsecurity.common.callback.OnResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class E3KitKullanici {

    private final Context mContext;

    public String authToken;
    public String virgilToken;
    public EThree eThree;
    public String kimlik;

    private final String TAG = "E3KitKullanici";
    private final String SIFRE = "_sifre";

    private final String SUNUCU_URL = "https://ChatApp.rodanel.repl.co";

    public static final String EThreeKey = "eThree";
    public static final String VirgilTokenKey = "virgilToken";
    public static final String AuthTokenKey = "authToken";

    public E3KitKullanici(Context context, String kimlik) {
        this.mContext = context;
        this.kimlik = kimlik;
    }
    public interface Tamamlandi{
        void Basarili(EThree eThree);
        void Basarisiz(Throwable hata);
    }
    public void KullaniciyiGetir(Tamamlandi tamamlandi) {
        Yetkilendir(kimlik, new OnResultListener<String>() {
            @Override
            public void onSuccess(String s) {
                SharedPreference sharedPreference = new SharedPreference(mContext);
                try {
                    JSONObject object = new JSONObject(s);
                    authToken = (String) object.get("authToken");
                } catch (final JSONException e) {
                    Log.e(TAG, "VirgilJWT TOKEN YANLIŞ", e);
                }
                EThreeParams params = new EThreeParams(kimlik, () -> {
                    virgilToken = VirgilJwt(authToken);
                    if (!virgilToken.equals("")){
                        sharedPreference.KaydetString(VirgilTokenKey, virgilToken);
                        sharedPreference.KaydetString(AuthTokenKey, authToken);
                    }
                    return virgilToken;
                }, mContext);
                eThree = new EThree(params);
                String sifre = kimlik+SIFRE;
                if (!eThree.hasLocalPrivateKey()){
                    eThree.register().addCallback(new OnCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Kullanıcı kaydedildi.");
                            eThree.backupPrivateKey(sifre).addCallback(new OnCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "Key yedeklendi 1.");
                                }

                                @Override
                                public void onError(@NonNull Throwable throwable) {
                                    Log.e(TAG, "Key yedeklenemedi 1."+throwable.getMessage());
                                }
                            });
                            tamamlandi.Basarili(eThree);
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            eThree.restorePrivateKey(sifre).addCallback(new OnCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "Key geri alındı.");
                                    tamamlandi.Basarili(eThree);
                                }

                                @Override
                                public void onError(@NonNull Throwable throwable) {
                                    Log.e(TAG, "Key geri alınamadı."+throwable.getMessage());
                                    eThree.rotatePrivateKey().addCallback(new OnCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "Key sıfırlandı.");
                                            tamamlandi.Basarili(eThree);
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable throwable) {
                                            Log.e(TAG, "Key sıfırlanamadı."+throwable.getMessage());
                                            tamamlandi.Basarisiz(throwable);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else{
                    eThree.backupPrivateKey(sifre).addCallback(new OnCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Key yedeklendi 2.");
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            Log.e(TAG, "Key yedeklenemedi 2."+throwable.getMessage());
                        }
                    });
                    tamamlandi.Basarili(eThree);
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                tamamlandi.Basarisiz(throwable);
            }
        });
    }
    private void Yetkilendir(String kimlik, OnResultListener<String> onResultListener){
        try {
            String url = SUNUCU_URL + "/authenticate";
            URL object = new URL(url);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");

            JSONObject cred = new JSONObject();

            cred.put("identity", kimlik);

            OutputStream wr = con.getOutputStream();
            wr.write(cred.toString().getBytes(StandardCharsets.UTF_8));
            wr.close();

            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                onResultListener.onSuccess(sb.toString());
            } else {
                onResultListener.onError(new Throwable("Some connection error"));
            }
        } catch (JSONException | IOException exception) {
            onResultListener.onError(exception);
        }

    }

    private String VirgilJwt(String authToken) {
        try {
            String url = SUNUCU_URL + "/virgil-jwt";
            URL object = new URL(url);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + authToken);
            con.setRequestMethod("GET");

            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                JSONObject jsonObject = new JSONObject(sb.toString());

                return jsonObject.getString("virgilToken");
            } else {
                Log.e(TAG, "Bağlantı hatası");
                return "";
            }
        } catch (IOException exception) {
            Log.e(TAG, "Bağlantı hatası");
            return "";
        } catch (JSONException e) {
            Log.e(TAG, "Virgil JWT json hatası");
            return "";
        }
    }
}
