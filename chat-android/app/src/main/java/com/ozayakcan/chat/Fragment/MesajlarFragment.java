package com.ozayakcan.chat.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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
import com.ozayakcan.chat.ChatApp;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Model.Mesajlar;
import com.ozayakcan.chat.Ozellik.E3KitKullanici;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.virgilsecurity.android.common.model.EThreeParams;
import com.virgilsecurity.android.ethree.interaction.EThree;
import com.virgilsecurity.common.callback.OnResultListener;
import com.virgilsecurity.sdk.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MesajlarFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private List<Mesajlar> mesajlarList;
    private RecyclerView mesajlarRW;
    private MesajlarAdapter mesajlarAdapter;
    private Veritabani veritabani;
    private final MainActivity mainActivity;
    private final Context mContext;
    private DatabaseReference kullanicilarRef;
    private boolean mesajlarGosteriliyor = false;

    private boolean mesajlarArsivleniyor = false;
    private LinearLayout arsivProgressBarLayout;

    private EThree eThree;
    private SharedPreference sharedPreference;

    public MesajlarFragment(MainActivity mainActivity) {
        this.mContext = mainActivity;
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mesajlar, container, false);
        veritabani = new Veritabani(mainActivity);
        sharedPreference = new SharedPreference(mainActivity);
        arsivProgressBarLayout = view.findViewById(R.id.arsivProgressBarLayout);
        mesajlarRW = view.findViewById(R.id.mesajlarRW);
        mesajlarRW.setHasFixedSize(true);
        mesajlarRW.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mesajlarList = new ArrayList<>();
        mesajlarAdapter = new MesajlarAdapter(mesajlarList, mainActivity);
        mesajlarAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Collections.sort(mesajlarList, (o1, o2) -> Long.compare(o2.getMesaj().getTarih(), o1.getMesaj().getTarih()));
                mesajlarRW.setAdapter(mesajlarAdapter);
            }
        });
        kullanicilarRef = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(firebaseUser.getPhoneNumber());
        kullanicilarRef.keepSynced(true);
        if (sharedPreference.GetirString(E3KitKullanici.VirgilTokenKey, "").equals("")){
            E3KitKullanici e3KitKullanici = new E3KitKullanici(mainActivity, firebaseUser.getUid());
            new Thread(() -> e3KitKullanici.KullaniciyiGetir(new E3KitKullanici.Tamamlandi() {
                @Override
                public void Basarili(EThree kullanici) {
                    eThree = kullanici;
                    MesajlariBul(true);
                    veritabani.MesajDurumuGuncelle(firebaseUser.getPhoneNumber(), false);
                }

                @Override
                public void Basarisiz(Throwable hata) {
                    Log.e("Chatapp", "Başarısız: "+hata.getMessage());
                    ((Activity) mainActivity).runOnUiThread(() -> Toast.makeText(mainActivity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show());
                }
            })).start();
        }else{
            EThreeParams eThreeParams = new EThreeParams(firebaseUser.getUid(),
                    () -> sharedPreference.GetirString(E3KitKullanici.VirgilTokenKey, ""),
                    mainActivity);
            eThree = new EThree(eThreeParams);
            MesajlariBul(true);
            veritabani.MesajDurumuGuncelle(firebaseUser.getPhoneNumber(), false);
        }
        return view;
    }
    private void MesajlariBul(boolean goster) {
        if (eThree != null){
            if (mesajlarGosteriliyor != goster){
                if (goster){
                    kullanicilarRef.addValueEventListener(mesajlarValueEventListener);
                }else{
                    kullanicilarRef.removeEventListener(mesajlarValueEventListener);
                }
                mesajlarGosteriliyor = goster;
            }
        }
    }
    private final ValueEventListener mesajlarValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            mesajlarList.clear();
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
                                    if (kullanici != null){
                                        veritabani.MesajDurumuGuncelle(kullanici.getTelefon(), true);
                                        DatabaseReference kisiyiKontrolEt = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.KisiTablosu).child(kullanici.getTelefon());
                                        kisiyiKontrolEt.keepSynced(true);
                                        kisiyiKontrolEt.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot5) {
                                                eThree.findUser(kullanici.getID()).addCallback(new OnResultListener<Card>() {
                                                    @Override
                                                    public void onSuccess(Card card) {
                                                        Kullanici kullanici1 = snapshot5.getValue(Kullanici.class);
                                                        long okunmamisMesaj = 0;
                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                            Mesaj mesaj1 = dataSnapshot1.getValue(Mesaj.class);
                                                            if(mesaj1 != null){
                                                                if (!mesaj1.isGonderen()){
                                                                    if (!mesaj1.isGoruldu()){
                                                                        okunmamisMesaj++;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (mesaj.isGonderen()){
                                                            mesaj.setMesaj(eThree.authDecrypt(mesaj.getMesaj()));
                                                        }else{
                                                            if (card != null){
                                                                mesaj.setMesaj(eThree.authDecrypt(mesaj.getMesaj(), card));
                                                            }else{
                                                                mesaj.setMesaj(getString(R.string.this_message_could_not_be_decrypted));
                                                            }
                                                        }
                                                        if (kullanici1 != null){
                                                            long finalOkunmamisMesaj1 = okunmamisMesaj;
                                                            mainActivity.runOnUiThread(() -> MesajGoster(kullanici, mesaj, kullanici1.getIsim(), finalOkunmamisMesaj1));
                                                        }else{
                                                            long finalOkunmamisMesaj = okunmamisMesaj;
                                                            mainActivity.runOnUiThread(() -> MesajGoster(kullanici, mesaj, "", finalOkunmamisMesaj));
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable throwable) {
                                                        Kullanici kullanici1 = snapshot5.getValue(Kullanici.class);
                                                        long okunmamisMesaj = 0;
                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                            Mesaj mesaj1 = dataSnapshot1.getValue(Mesaj.class);
                                                            if(mesaj1 != null){
                                                                if (!mesaj1.isGonderen()){
                                                                    if (!mesaj1.isGoruldu()){
                                                                        okunmamisMesaj++;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (mesaj.isGonderen()){
                                                            mesaj.setMesaj(eThree.authDecrypt(mesaj.getMesaj()));
                                                        }else{
                                                            mesaj.setMesaj(getString(R.string.this_message_could_not_be_decrypted));
                                                        }
                                                        if (kullanici1 != null){
                                                            long finalOkunmamisMesaj1 = okunmamisMesaj;
                                                            mainActivity.runOnUiThread(() -> MesajGoster(kullanici, mesaj, kullanici1.getIsim(), finalOkunmamisMesaj1));
                                                        }else{
                                                            long finalOkunmamisMesaj = okunmamisMesaj;
                                                            mainActivity.runOnUiThread(() -> MesajGoster(kullanici, mesaj, "", finalOkunmamisMesaj));
                                                        }
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
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    @SuppressLint("NotifyDataSetChanged")
    private void MesajGoster(Kullanici kullanici, Mesaj mesaj, String isim , long okunmamisMesaj){
        Mesajlar mesajlar = new Mesajlar(kullanici, mesaj, isim, okunmamisMesaj);
        if (mesajlar.getMesaj().getMesaj().length() > ChatApp.MaxMesajKarakterSayisi){
            mesajlar.getMesaj().setMesaj(ChatApp.MesajBol(mesajlar.getMesaj().getMesaj(), ChatApp.MaxMesajKarakterSayisi));
        }
        if (mesajlarList.size() > 0){
            boolean iceriyor = false;
            int index = -1;
            for(int i = 0; i < mesajlarList.size(); i++){
                if (mesajlarList.get(i).getKullanici().getTelefon().equals(mesajlar.getKullanici().getTelefon())){
                    iceriyor = true;
                    index = i;
                    break;
                }
            }
            if (iceriyor){
                mesajlarList.set(index, mesajlar);
                mesajlarAdapter.notifyItemChanged(index);
            }else{
                mesajlarList.add(mesajlar);
                mesajlarAdapter.notifyDataSetChanged();
            }
        }else {
            mesajlarList.add(mesajlar);
            mesajlarAdapter.notifyDataSetChanged();
        }
    }

    public void MesajlariSil(FirebaseUser firebaseUser2, String telefon, int index){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setTitle(R.string.delete_messages);
        builder.setMessage(R.string.are_you_sure_you_want_to_delete_messages);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(firebaseUser2.getPhoneNumber());
            databaseReference.keepSynced(true);
            HashMap<String, Object> mesajSilMap = new HashMap<>();
            mesajSilMap.put(telefon, null);
            databaseReference.updateChildren(mesajSilMap, (error, ref) -> {
                if (error == null){
                    mesajlarAdapter.notifyItemRemoved(index);
                }else{
                    Toast.makeText(mContext, mContext.getString(R.string.messages_could_not_be_deleted), Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void MesajlariArsivle(FirebaseUser firebaseUser2, String telefon, int index){
        if (!mesajlarArsivleniyor){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle(R.string.archive_messages);
            builder.setMessage(R.string.are_you_sure_you_want_to_archive_messages);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                arsivProgressBarLayout.setVisibility(View.VISIBLE);
                mesajlarArsivleniyor = true;
                DatabaseReference mesajlariArsivleRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.MesajTablosu).child(firebaseUser2.getPhoneNumber()).child(telefon);
                databaseReference.keepSynced(true);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, Object> arsivleMap = new HashMap<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            arsivleMap.put(Veritabani.ArsivTablosu+"/"+firebaseUser2.getPhoneNumber()+"/"+telefon+"/"+dataSnapshot.getKey(), dataSnapshot.getValue());
                            arsivleMap.put(Veritabani.MesajTablosu+"/"+firebaseUser2.getPhoneNumber()+"/"+telefon+"/"+dataSnapshot.getKey(), null);
                        }
                        mesajlariArsivleRef.updateChildren(arsivleMap, (error, ref) -> {
                            if (error == null){
                                mesajlarAdapter.notifyItemRemoved(index);
                                mesajlarAdapter.notifyItemChanged(index);
                            }else {
                                Toast.makeText(mContext, mContext.getString(R.string.messages_could_not_be_arhived), Toast.LENGTH_SHORT).show();
                            }
                            mesajlarArsivleniyor = false;
                            arsivProgressBarLayout.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext, mContext.getString(R.string.messages_could_not_be_arhived), Toast.LENGTH_SHORT).show();
                        arsivProgressBarLayout.setVisibility(View.GONE);
                        mesajlarArsivleniyor = false;
                    }
                });
            });
            builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            Toast.makeText(mContext, mContext.getString(R.string.there_is_already_an_archiving_progress), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MesajlariBul(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        MesajlariBul(false);
    }
}