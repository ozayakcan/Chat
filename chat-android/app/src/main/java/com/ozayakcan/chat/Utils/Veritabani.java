package com.ozayakcan.chat.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ozayakcan.chat.Login.RegisterActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.R;

import java.util.HashMap;

public class Veritabani {

    public static String KullaniciTablosu = "Kullanıcılar";

    public static String IDKey = "id";
    public static String IsimKey = "isim";
    public static String ProfilResmiKey = "profilResmi";
    public static String TelefonKey = "telefon";
    public static String HakkimdaKey = "hakkimda";
    public static String KayitZamaniKey = "kayitZamani";

    public static String ProfilResmiDosyaAdi = "profil_resmi";

    public static String VarsayilanDeger = "varsayılan";

    public static String TarihSaatFormati = "dd/MM/yyyy HH:mm:ss.SSS";

    private Context mContext;

    public Veritabani(Context context){
        mContext = context;
    }

    public HashMap<String, Object> KayitHashMap(Kullanici kullanici, boolean tarih){
        HashMap<String, Object> map = new HashMap<>();
        map.put(Veritabani.IDKey, kullanici.getID());
        map.put(Veritabani.IsimKey, kullanici.getIsim());
        map.put(Veritabani.TelefonKey, kullanici.getTelefon());
        map.put(Veritabani.HakkimdaKey, kullanici.getHakkimda());
        if (tarih){
            map.put(Veritabani.KayitZamaniKey, kullanici.getKayitZamani());
        }
        return map;
    }
}
