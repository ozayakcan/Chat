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

public class E3KitKullanici {


    private static E3KitKullanici mInstance;
    private final Context mContext;

    private E3KitKullanici(Context context) {mContext = context;}
    public static synchronized E3KitKullanici getInstance(Context context) {
        if(mInstance == null){
            mInstance = new E3KitKullanici(context);
        }
        return mInstance;
    }
}
