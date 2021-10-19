package com.ozayakcan.chat.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class E2EE {


    private static E2EE mInstance;
    private final Context mContext;

    private E2EE(Context context) {mContext = context;}
    public static synchronized E2EE getInstance(Context context) {
        if(mInstance == null){
            mInstance = new E2EE(context);
        }
        return mInstance;
    }

    public SecretKey KeyOlustur() throws UnsupportedEncodingException {
        if (SharedPreference.getInstance(mInstance.mContext).GetirString("E2EEKEY", "None").equals("None")) {
            byte[] b = new byte[16];
            new Random().nextBytes(b);
            String key = Base64.encodeToString(b, Base64.DEFAULT);
            SharedPreference.getInstance(mInstance.mContext).KaydetString("E2EEKEY", key);
        }
        return new SecretKeySpec(Base64.decode(SharedPreference.getInstance(mContext).GetirString("E2EEKEY", "NONE"), Base64.DEFAULT), "AES");
    }

    @SuppressLint("GetInstance")
    public byte[] Sifrele(String mesaj, SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(mesaj.getBytes(StandardCharsets.UTF_8));
    }

    @SuppressLint("GetInstance")
    public String Coz(byte[] sifreliMesaj, SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(sifreliMesaj), StandardCharsets.UTF_8);
    }

    /* Test Kodları

    try {
        SecretKey key = E2EE.getInstance(this).KeyOlustur();
        String denemeMesaj = "Mehaba bu bir test Mesajıdır.";
        byte[] sifreliMesaj = E2EE.getInstance(RegisterActivity.this).Sifrele(denemeMesaj, key);
        String normalMesaj = E2EE.getInstance(this).Coz(sifreliMesaj, key);
        testE2EE.setText("Şifreli Mesaj:"+ Arrays.toString(sifreliMesaj) +"\nNormal Mesaj:"+normalMesaj);
    } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
        testE2EE.setText(e.getMessage());
    }

     */
}
