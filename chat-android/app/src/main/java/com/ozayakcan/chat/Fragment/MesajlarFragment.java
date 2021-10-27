package com.ozayakcan.chat.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Adapter.MesajlarAdapter;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Model.Mesajlar;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.ArrayList;
import java.util.List;

public class MesajlarFragment extends Fragment {

    private View view;
    private MainActivity mainActivity;
    private FirebaseUser firebaseUser;
    private List<Mesajlar> mesajlarList;
    private RecyclerView mesajlarRW;
    private MesajlarAdapter mesajlarAdapter;

    public MesajlarFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mesajlar, container, false);
        mesajlarRW = view.findViewById(R.id.mesajlarRW);
        mesajlarRW.setHasFixedSize(true);
        mesajlarRW.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mesajlarList = new ArrayList<>();
        MesajlariBul();
        return view;
    }

    private void MesajlariBul() {
        mesajlarList.clear();
        DatabaseReference kullanicilar = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(firebaseUser.getPhoneNumber());
        kullanicilar.keepSynced(true);
        kullanicilar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Query query1 = dataSnapshot.getRef().orderByKey().limitToLast(1);
                    query1.keepSynced(true);
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot3) {
                            for (DataSnapshot dataSnapshot10 : snapshot3.getChildren()){
                                Mesaj mesaj = dataSnapshot10.getValue(Mesaj.class);
                                DatabaseReference kisiyiBul = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(snapshot3.getKey());
                                kisiyiBul.keepSynced(true);
                                kisiyiBul.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot4) {
                                        Kullanici kullanici = snapshot4.getValue(Kullanici.class);
                                        DatabaseReference kisiyiKontrolEt = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.KisiTablosu).child(kullanici.getTelefon());
                                        kisiyiKontrolEt.keepSynced(true);
                                        kisiyiKontrolEt.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot5) {
                                                Kullanici kullanici1 = snapshot5.getValue(Kullanici.class);
                                                long okunmamiMesaj = 0;
                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                    Mesaj mesaj1 = dataSnapshot1.getValue(Mesaj.class);
                                                    if (!mesaj1.isGonderen()){
                                                        if (!mesaj1.isGoruldu()){
                                                            okunmamiMesaj++;
                                                        }
                                                    }
                                                }
                                                if (kullanici1 != null){
                                                    MesajGoster(kullanici, mesaj, kullanici1.getIsim(), okunmamiMesaj);
                                                }else{
                                                    MesajGoster(kullanici, mesaj, "", okunmamiMesaj);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
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
    private void MesajGoster(Kullanici kullanici, Mesaj mesaj, String isim , long okunmamisMesaj){
        Mesajlar mesajlar = new Mesajlar(kullanici, mesaj, isim, okunmamisMesaj);
        mesajlarList.add(mesajlar);
        mesajlarAdapter = new MesajlarAdapter(mesajlarList, mainActivity);
        mesajlarRW.setAdapter(mesajlarAdapter);
    }
}