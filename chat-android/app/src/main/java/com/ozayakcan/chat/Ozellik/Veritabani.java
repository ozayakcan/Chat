package com.ozayakcan.chat.Ozellik;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Model.Kullanici;

import java.util.HashMap;

public class Veritabani {

    public static String KullaniciTablosu = "Kullanıcılar";
    public static String KisiTablosu = "Kişiler";

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
    public static void KisiEkle(String eklenecekID, String eklenecekIsim, String eklenecekTelefon, String telefon) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Veritabani.IDKey, eklenecekID);
        map.put(Veritabani.IsimKey, eklenecekIsim);
        map.put(Veritabani.TelefonKey, eklenecekTelefon);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KisiTablosu).child(telefon).child(eklenecekTelefon);
		databaseReference.updateChildren(map);
    }

    public static void KisiSil(String telefon) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KisiTablosu).child(telefon);
		databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().setValue(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
