package com.ozayakcan.chat.Fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Adapter.KisiAdapter;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.ArrayList;
import java.util.List;

public class KisilerFragment extends Fragment {

    private Izinler izinler;
    private View view;
    private Context mContext;
    private FirebaseUser firebaseUser;
    private RecyclerView kisilerRW;
    private KisiAdapter kisiAdapter;
    private List<Kullanici> kullaniciList;
    public KisilerFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kisiler, container, false);
        izinler = new Izinler(getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        kisilerRW = view.findViewById(R.id.kisilerRW);
        kisilerRW.setHasFixedSize(true);
        kisilerRW.setLayoutManager(new LinearLayoutManager(getActivity()));

        kullaniciList = new ArrayList<>();
        if (izinler.KontrolEt(Manifest.permission.READ_CONTACTS)){
            KisileriBul();
        }else{
            izinler.Sor(Manifest.permission.READ_CONTACTS, kisiIzniResultLauncher);
        }
        return view;
    }

    ActivityResultLauncher<String> kisiIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    KisileriBul();
                }else{
                    KisiIzniUyariKutusu();
                }
            });

    private void KisiIzniUyariKutusu() {
        izinler.ZorunluIzinUyariKutusu(Manifest.permission.READ_CONTACTS, kisiIzniResultLauncher);
    }

    private void KisileriBul(){
        //Kişiler veritabanından çekiliyor
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KisiTablosu).child(firebaseUser.getPhoneNumber());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kullaniciList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                    if (kullanici != null){
                        kullaniciList.add(kullanici);
                    }
                }
                kisiAdapter = new KisiAdapter(getContext(), kullaniciList);
                kisilerRW.setAdapter(kisiAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Karışıklık olmaması için veritabanındaki kişiler siliniyor
        Veritabani.KisiSil(firebaseUser.getPhoneNumber());
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
                        String telefonNumarasi = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(telefonNumarasi);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                                if (kullanici != null){
                                    if (!kullanici.getTelefon().equals(firebaseUser.getPhoneNumber())){
                                        Veritabani.KisiEkle(kullanici.getID(), isim, telefonNumarasi, firebaseUser.getPhoneNumber());
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
}