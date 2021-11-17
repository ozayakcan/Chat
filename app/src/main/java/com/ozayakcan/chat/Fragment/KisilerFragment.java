package com.ozayakcan.chat.Fragment;

import android.Manifest;
import android.os.Bundle;
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
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.ArrayList;
import java.util.List;

public class KisilerFragment extends Fragment {

    private Izinler izinler;
    private Veritabani veritabani;
    private FirebaseUser firebaseUser;
    private RecyclerView kisilerRW;
    private KisiAdapter kisiAdapter;
    private List<Kullanici> kullaniciList;
    private final MainActivity mainActivity;
    public KisilerFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kisiler, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        izinler = new Izinler(getContext());
        veritabani = new Veritabani(getContext());
        kisilerRW = view.findViewById(R.id.kisilerRW);
        kisilerRW.setHasFixedSize(true);
        kisilerRW.setLayoutManager(new LinearLayoutManager(getActivity()));
        kullaniciList = new ArrayList<>();
        if (izinler.KontrolEt(Manifest.permission.READ_CONTACTS)){
            KisileriGuncelle();
        }else{
            izinler.Sor(Manifest.permission.READ_CONTACTS, kisiIzniResultLauncher);
        }
        return view;
    }

    private void KisileriGuncelle() {
        veritabani.KisileriEkle(firebaseUser, this::KisileriGoster);
    }

    private void KisileriGoster() {
        DatabaseReference kisilerRef = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu + "/" + firebaseUser.getPhoneNumber() + "/" + Veritabani.KisiTablosu);
        kisilerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kullaniciList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                    if (kullanici != null){
                        kullaniciList.add(kullanici);
                    }
                }
                if (mainActivity != null){
                    kisiAdapter = new KisiAdapter(kullaniciList, mainActivity);
                }
                kisilerRW.setAdapter(kisiAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ActivityResultLauncher<String> kisiIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    KisileriGuncelle();
                }else{
                    KisiIzniUyariKutusu();
                }
            });

    private void KisiIzniUyariKutusu() {
        izinler.ZorunluIzinUyariKutusu(Manifest.permission.READ_CONTACTS, kisiIzniResultLauncher);
    }
}