package com.ozayakcan.chat.Ozellik;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.R;

import java.util.HashMap;
import java.util.Map;

public class Veritabani {

    public static String KullaniciTablosu = "Kullanicilar";
    public static String KisiTablosu = "kisiler";
    public static String MesajTablosu = "Mesajlar";

    public static String IDKey = "id";
    public static String IsimKey = "isim";
    public static String OnlineDurumuKey = "onlineDurumu";
    public static String ProfilResmiKey = "profilResmi";
    public static String TelefonKey = "telefon";
    public static String HakkimdaKey = "hakkimda";
    public static String KayitZamaniKey = "kayitZamani";
    public static String SonGorulmeKey = "sonGorulme";

    public static String MesajKey = "mesaj";
    public static String TarihKey = "tarih";
    public static String MesajDurumuKey = "mesajDurumu";
    public static String GonderenKey = "gonderen";
    public static String GorulduKey = "goruldu";
    public static long MesajDurumuGonderiliyor = 0;
    public static long MesajDurumuGonderildi = 1;
    public static long MesajDurumuBendenSilindi = 3;
    public static long MesajDurumuHerkestenSilindi = 4;

    public static String ProfilResmiDosyaAdi = "profil_resmi";

    public static String VarsayilanDeger = "varsayılan";

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
        map.put(Veritabani.OnlineDurumuKey, kullanici.getOnlineDurumu());
        if (tarih){
            map.put(Veritabani.KayitZamaniKey, ServerValue.TIMESTAMP);
        }
        return map;
    }
    public void KisileriEkle(FirebaseUser firebaseUser){
        //Veritabanındaki kişiler siliniyor
        KisiSil(firebaseUser.getPhoneNumber());
        //Rehberdeki kişiler veritabanına ekleniyor
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor != null && cursor.moveToNext()) {
                String id = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String isim = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));

                if (cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String telefonNumarasiNormal = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String telefonNumarasi = telefonNumarasiNormal.replace(" ", "");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(telefonNumarasi);
                        databaseReference.keepSynced(true);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                                if (kullanici != null){
                                    if (!kullanici.getTelefon().equals(firebaseUser.getPhoneNumber())){
                                        kullanici.setIsim(isim);
                                        KisiEkle(kullanici, firebaseUser.getPhoneNumber());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    pCur.close();
                }
            }
        }
    }
    public static void KisiEkle(Kullanici kullanici, String telefon) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Veritabani.IDKey, kullanici.getID());
        map.put(Veritabani.IsimKey, kullanici.getIsim());
        map.put(Veritabani.TelefonKey, kullanici.getTelefon());
        map.put(Veritabani.ProfilResmiKey, kullanici.getProfilResmi());
        map.put(Veritabani.HakkimdaKey, kullanici.getHakkimda());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu+"/"+telefon+"/"+Veritabani.KisiTablosu).child(kullanici.getTelefon());
		databaseReference.updateChildren(map);
    }

    public static void KisiSil(String telefon) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu+"/"+telefon+"/"+Veritabani.KisiTablosu);
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
    public void MesajGonder(String mesaj, String gonderilecekTelefon, FirebaseUser firebaseUser){
        Map<String, String> tarih = ServerValue.TIMESTAMP;
        DatabaseReference ekleBir = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(firebaseUser.getPhoneNumber()).child(gonderilecekTelefon);
        ekleBir.keepSynced(true);
        HashMap<String, Object> mapBir = new HashMap<>();
        mapBir.put(Veritabani.MesajKey, mesaj);
        mapBir.put(Veritabani.TarihKey, tarih);
        mapBir.put(Veritabani.MesajDurumuKey, Veritabani.MesajDurumuGonderiliyor);
        mapBir.put(Veritabani.GonderenKey, true);
        mapBir.put(Veritabani.GorulduKey, false);
        DatabaseReference ekleBirPush = ekleBir.push();
        ekleBirPush.keepSynced(true);

        DatabaseReference ekleIki = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(gonderilecekTelefon).child(firebaseUser.getPhoneNumber());
        ekleIki.keepSynced(true);
        HashMap<String, Object> mapIki = new HashMap<>();
        mapIki.put(Veritabani.MesajKey, mesaj);
        mapIki.put(Veritabani.TarihKey, tarih);
        mapIki.put(Veritabani.MesajDurumuKey, Veritabani.MesajDurumuGonderiliyor);
        mapIki.put(Veritabani.GonderenKey, false);
        mapIki.put(Veritabani.GorulduKey, false);
        ekleBirPush.setValue(mapBir, (error, ref) -> {
            if (error == null){
                ref.keepSynced(true);
                HashMap<String, Object> basarili = new HashMap<>();
                basarili.put(Veritabani.MesajDurumuKey, Veritabani.MesajDurumuGonderildi);
                ref.updateChildren(basarili);
                String key = ref.getKey();
                if (key != null){
                    DatabaseReference ekleIkiPush = ekleIki.child(key);
                    ekleIkiPush.keepSynced(true);
                    ekleIkiPush.setValue(mapIki, (error2, ref2) -> {
                        if (error2 == null){
                            ref2.keepSynced(true);
                            HashMap<String, Object> basarili2 = new HashMap<>();
                            basarili2.put(Veritabani.MesajDurumuKey, Veritabani.MesajDurumuGonderildi);
                            ref2.updateChildren(basarili2);
                        }
                    });
                }
            }
        });

    }
    public void MesajDurumuGuncelle(String telefon, boolean karsi){
        DatabaseReference onlineDurumu = FirebaseDatabase.getInstance().getReference(".info/connected");
        onlineDurumu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean baglandi = snapshot.getValue(Boolean.class);
                if (baglandi){
                    DatabaseReference guncelle = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(telefon);
                    guncelle.keepSynced(true);
                    guncelle.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                DatabaseReference guncelle2 = guncelle.child(dataSnapshot.getKey());
                                guncelle2.keepSynced(true);
                                guncelle2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                        for (DataSnapshot dataSnapshot2 : snapshot2.getChildren()){
                                            Mesaj mesaj = snapshot2.getValue(Mesaj.class);
                                            if (mesaj.isGonderen() != karsi){
                                                HashMap<String, Object> guncelleMap = new HashMap<>();
                                                guncelleMap.put(Veritabani.MesajDurumuKey, Veritabani.MesajDurumuGonderildi);
                                                DatabaseReference databaseReference13 = dataSnapshot2.getRef();
                                                databaseReference13.keepSynced(true);
                                                databaseReference13.updateChildren(guncelleMap);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void DurumGuncelle(FirebaseUser firebaseUser ,boolean durum){
        DatabaseReference durumGuncelle = FirebaseDatabase.getInstance().getReference(KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        HashMap<String, Object> durumGuncelleMap = new HashMap<>();
        durumGuncelleMap.put(OnlineDurumuKey, durum);
        if (!durum) {
            durumGuncelleMap.put(SonGorulmeKey, ServerValue.TIMESTAMP);
        }
        durumGuncelle.updateChildren(durumGuncelleMap);
    }
}
