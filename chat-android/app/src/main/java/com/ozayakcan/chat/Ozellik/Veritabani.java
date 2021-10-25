package com.ozayakcan.chat.Ozellik;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

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
                                        KisiEkle(kullanici.getID(), isim, telefonNumarasi, firebaseUser.getPhoneNumber());
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
