package com.ozayakcan.chat.Ozellik;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ozayakcan.chat.Bildirimler.BildirimClass;
import com.ozayakcan.chat.MesajActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;

import java.util.HashMap;

public class Veritabani {

    public static String KullaniciTablosu = "Kullanicilar";
    public static String KisiTablosu = "kisiler";
    public static String MesajTablosu = "Mesajlar";
    public static String ArsivTablosu = "Arsiv";

    public static String IDKey = "id";
    public static String IsimKey = "isim";
    public static String OnlineDurumuKey = "onlineDurumu";
    public static String ProfilResmiKey = "profilResmi";
    public static String TelefonKey = "telefon";
    public static String HakkimdaKey = "hakkimda";
    public static String BildirimDurumuKey = "bildirimDurumu";
    public static String BildirimSesiKey = "bildirimSesi";
    public static String BildirimOncelikKey = "bildirimOncelik";
    public static String BildirimTitresimKey = "bildirimTitresim";
    public static String VarsayilanHakkimdaYazisi(Context context) {
        return "";
    }
    public static String KayitZamaniKey = "kayitZamani";
    public static String SonGorulmeKey = "sonGorulme";
    public static String FCMTokenKey = "fcmToken";

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

    private final Context mContext;

    public Veritabani(Context context){
        mContext = context;
    }

    public void TokenYenile(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("Token Alınamadı", task.getException());
                return;
            }
            String token = task.getResult();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null){
                TokenKaydet(firebaseUser, token);
            }
        });
    }

    public String TokenAl(){
        SharedPreference sharedPreference = new SharedPreference(mContext);
        return sharedPreference.GetirString(FCMTokenKey, "0");
    }

    public void TokenKaydet(FirebaseUser firebaseUser, String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        reference.keepSynced(true);
        HashMap<String, Object> tokenMap = new HashMap<>();
        tokenMap.put(FCMTokenKey, token);
        reference.updateChildren(tokenMap, (error, ref) -> {
            if (error == null){
                SharedPreference sharedPreference = new SharedPreference(mContext);
                sharedPreference.KaydetString(FCMTokenKey, token);
            }
        });
    }

    public HashMap<String, Object> KayitHashMap(Kullanici kullanici, boolean tarih){
        HashMap<String, Object> map = new HashMap<>();
        map.put(Veritabani.IDKey, kullanici.getID());
        map.put(Veritabani.IsimKey, kullanici.getIsim());
        map.put(Veritabani.TelefonKey, kullanici.getTelefon());
        map.put(Veritabani.HakkimdaKey, kullanici.getHakkimda());
        map.put(Veritabani.OnlineDurumuKey, kullanici.isOnlineDurumu());
        if (tarih){
            map.put(Veritabani.KayitZamaniKey, ServerValue.TIMESTAMP);
        }
        return map;
    }
    public interface KisilerListener{
        void Tamamlandi();
    }
    public void KisileriEkle(FirebaseUser firebaseUser, KisilerListener kisilerListener){
        //Veritabanındaki kişiler siliniyor
        KisiSil(firebaseUser.getPhoneNumber());
        //Rehberdeki kişiler veritabanına ekleniyor
        ((Activity) mContext).runOnUiThread(() -> {
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0){
                    while (cursor.moveToNext()) {
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
                                                KisiEkle(kullanici, firebaseUser.getPhoneNumber(), kisilerListener);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        kisilerListener.Tamamlandi();
                                    }
                                });
                            }
                            pCur.close();
                        }
                    }
                }
                cursor.close();
            }
        });
    }
    public static void KisiEkle(Kullanici kullanici, String telefon, KisilerListener kisilerListener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Veritabani.IDKey, kullanici.getID());
        map.put(Veritabani.IsimKey, kullanici.getIsim());
        map.put(Veritabani.TelefonKey, kullanici.getTelefon());
        map.put(Veritabani.ProfilResmiKey, kullanici.getProfilResmi());
        map.put(Veritabani.HakkimdaKey, kullanici.getHakkimda());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu+"/"+telefon+"/"+Veritabani.KisiTablosu).child(kullanici.getTelefon());
		databaseReference.keepSynced(true);
        databaseReference.updateChildren(map, (error, ref) -> kisilerListener.Tamamlandi());
    }

    public static void KisiSil(String telefon) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu+"/"+telefon+"/"+Veritabani.KisiTablosu);
		databaseReference.keepSynced(true);
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
    public void MesajGonder(Mesaj normalMesaj, int sira, String gonderilecekTelefon, FirebaseUser firebaseUser, MesajActivity mesajActivity){
        DatabaseReference ekleBir = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(gonderilecekTelefon).child(firebaseUser.getPhoneNumber());
        ekleBir.keepSynced(true);
        HashMap<String, Object> mapBir = new HashMap<>();
        mapBir.put(Veritabani.MesajKey, normalMesaj.getMesaj());
        MesajFonksiyonlari.getInstance(mContext).BildirimGonderilecekKisiyiEkle(gonderilecekTelefon);
        DatabaseReference ekleBirPush = ekleBir.push();
        ekleBirPush.keepSynced(true);
        ekleBirPush.setValue(mapBir, (error, ref) -> {
            if (error == null){
                normalMesaj.setMesajDurumu(Veritabani.MesajDurumuGonderildi);
                normalMesaj.setMesajKey(ekleBirPush.getKey());
                mesajActivity.MesajDurumunuGuncelle(sira, normalMesaj);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(KullaniciTablosu).child(gonderilecekTelefon);
                databaseReference.keepSynced(true);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Kullanici kullanici = snapshot.getValue(Kullanici.class);
                        if (kullanici != null){
                            BildirimClass.MesajBildirimiYolla(kullanici.getFcmToken(), new BildirimClass.BildirimListener() {
                                @Override
                                public void Gonderildi() {
                                    MesajFonksiyonlari.getInstance(mContext).BildirimGonderilecekKisiyiSil(gonderilecekTelefon);
                                }

                                @Override
                                public void Gonderilmedi() {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
    public void DurumKontrol(FirebaseUser firebaseUser){
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference sonGorulmeRef = firebaseDatabase.getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.SonGorulmeKey);
        sonGorulmeRef.keepSynced(true);
        final DatabaseReference onlineDurumuRef = firebaseDatabase.getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.OnlineDurumuKey);
        onlineDurumuRef.keepSynced(true);
        final DatabaseReference baglantiKontrol = firebaseDatabase.getReference(".info/connected");
        baglantiKontrol.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    sonGorulmeRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                    onlineDurumuRef.onDisconnect().setValue(false);
                    onlineDurumuRef.setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public static void DurumGuncelle(FirebaseUser firebaseUser, boolean durum){
        DatabaseReference durumGuncelle = FirebaseDatabase.getInstance().getReference(KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        durumGuncelle.keepSynced(true);
        HashMap<String, Object> durumGuncelleMap = new HashMap<>();
        durumGuncelleMap.put(OnlineDurumuKey, durum);
        if (!durum) {
            durumGuncelleMap.put(SonGorulmeKey, ServerValue.TIMESTAMP);
        }
        durumGuncelle.updateChildren(durumGuncelleMap);
    }
}
