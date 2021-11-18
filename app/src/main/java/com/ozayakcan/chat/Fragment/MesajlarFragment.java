package com.ozayakcan.chat.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.ozayakcan.chat.Adapter.MesajlarAdapter;
import com.ozayakcan.chat.ArsivActivity;
import com.ozayakcan.chat.Bildirimler.BildirimClass;
import com.ozayakcan.chat.ChatApp;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Model.Mesajlar;
import com.ozayakcan.chat.Ozellik.MesajFonksiyonlari;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MesajlarFragment extends Fragment {

    private FirebaseUser firebaseUser;
    public List<Mesajlar> mesajlarList;
    private RecyclerView mesajlarRW;
    public MesajlarAdapter mesajlarAdapter;
    private Veritabani veritabani;
    private final MainActivity mainActivity;
    private final ArsivActivity arsivActivity;
    private final Context mContext;
    private boolean mesajlarGosteriliyor = false;

    private boolean mesajlarArsivleniyor = false;
    private boolean mesajlarSiliniyor = false;
    private LinearLayout progressBarLayout;
    private TextView progressBarText;

    private String getirilecekMesajlar = MesajFonksiyonlari.KaydedilecekTur;

    public MesajlarFragment(MainActivity mainActivity) {
        this.mContext = mainActivity;
        this.mainActivity = mainActivity;
        this.arsivActivity = null;
        Init();
    }
    public MesajlarFragment(ArsivActivity arsivActivity) {
        this.mContext = arsivActivity;
        this.arsivActivity = arsivActivity;
        this.mainActivity = null;
        Init();
    }
    public void Init(){
        mesajlarList = new ArrayList<>();
        veritabani = new Veritabani(mContext);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mesajlar, container, false);
        progressBarLayout = view.findViewById(R.id.progressBarLayout);
        progressBarText = view.findViewById(R.id.progressBarText);
        mesajlarRW = view.findViewById(R.id.mesajlarRW);
        mesajlarRW.setHasFixedSize(true);
        mesajlarRW.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (arsivActivity != null){
            getirilecekMesajlar = MesajFonksiyonlari.KaydedilecekTurArsiv;
            mesajlarAdapter = new MesajlarAdapter(mesajlarList, arsivActivity);
        }
        if (mainActivity != null){
            getirilecekMesajlar = MesajFonksiyonlari.KaydedilecekTur;
            mesajlarAdapter = new MesajlarAdapter(mesajlarList, mainActivity);
        }
        mesajlarAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Collections.sort(mesajlarList, (o1, o2) -> Long.compare(o2.getMesaj().getTarih(), o1.getMesaj().getTarih()));
                mesajlarRW.setAdapter(mesajlarAdapter);
            }
        });
        MesajlariGetir();
        veritabani.MesajDurumuGuncelle(firebaseUser.getPhoneNumber(), false);
        return view;
    }

    private void MesajlariGetir() {
        if(mainActivity != null){
            if (mainActivity.MesajSecildi){
                return;
            }
        }
        mesajlarList.clear();
        List<String> kisiler = MesajFonksiyonlari.getInstance(mContext).CokKisiliMesajlariGetir(getirilecekMesajlar);
        for (String kisi : kisiler){
            List<Mesaj> mesajList = MesajFonksiyonlari.getInstance(mContext).MesajlariGetir(kisi, getirilecekMesajlar);
            if (mesajList.size() > 0){
                DatabaseReference kullaniciRef = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(kisi);
                kullaniciRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Kullanici kullanici = snapshot.getValue(Kullanici.class);
                        if (kullanici != null){
                            DatabaseReference kisilerRef = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.KisiTablosu).child(kisi);
                            kisilerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    Kullanici kisi = snapshot1.getValue(Kullanici.class);
                                    String isim = kisi != null ? kisi.getIsim() : kullanici.getIsim();
                                    long okunmamisMesaj = 0;
                                    for (Mesaj mesaj123 : mesajList){
                                        if (!mesaj123.isGonderen() && !mesaj123.isGoruldu()){
                                            okunmamisMesaj++;
                                        }
                                    }
                                    Mesajlar mesajlar = new Mesajlar(kullanici, mesajList.get(mesajList.size()-1), isim, okunmamisMesaj);

                                    boolean kullaniciMevcut = false;
                                    for (Mesajlar mesajlar123 : mesajlarList){
                                        if (mesajlar123.getKullanici().getTelefon().equals(mesajlar.getKullanici().getTelefon())){
                                            kullaniciMevcut = true;
                                            break;
                                        }
                                    }
                                    if (!kullaniciMevcut){
                                        mesajlarList.add(mesajlar);
                                        mesajlarAdapter.notifyDataSetChanged();
                                        MesajlariGuncelle(true);
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
    }

    private void MesajlariGuncelle(boolean goster) {
        if (mesajlarGosteriliyor != goster){
            if (goster){
                ChatApp.registerBroadcastReceiver(mMesaj2BroadcastReceiver, BildirimClass.MesajKey);
                ChatApp.registerBroadcastReceiver(mMesaj2BroadcastReceiver, BildirimClass.GorulduKey);
            }else{
                ChatApp.unregisterBroadcastReceiver(mMesaj2BroadcastReceiver);
            }
            mesajlarGosteriliyor = goster;
        }
    }
    private final BroadcastReceiver mMesaj2BroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(BildirimClass.MesajKey)){
            MesajlariGetir();
        }else if (intent.getAction().equals(BildirimClass.GorulduKey)){
            MesajlariGetir();
        }
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    public void MesajlariSil(List<Mesajlar> mesajlarArrayList, boolean arsivde){
        if (!mesajlarSiliniyor && !mesajlarArsivleniyor){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle(R.string.delete_messages);
            builder.setMessage(R.string.are_you_sure_you_want_to_delete_messages);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                mesajlarSiliniyor = true;
                progressBarText.setText(getString(R.string.messages_is_deleting));
                progressBarLayout.setVisibility(View.VISIBLE);
                for (Mesajlar mesajlar : mesajlarArrayList){
                    MesajFonksiyonlari.getInstance(mContext).MesajlariSil(mesajlar.getKullanici().getTelefon(), getirilecekMesajlar);
                }
                mesajlarList.removeAll(mesajlarArrayList);
                mesajlarAdapter.notifyDataSetChanged();
                progressBarLayout.setVisibility(View.GONE);
                progressBarText.setText("");
                mesajlarSiliniyor = false;
                if (mainActivity != null){
                    mainActivity.MesajMenusu();
                }
            });
            builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            if (arsivde){
                Toast.makeText(mContext, mContext.getString(R.string.there_is_already_an_unarchiving_or_deleting_progress), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext, mContext.getString(R.string.there_is_already_an_archiving_or_deleting_progress), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void MesajlariArsivle(List<Mesajlar> mesajlarArrayList, boolean arsivle){
        if (!mesajlarSiliniyor && !mesajlarArsivleniyor){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle(R.string.archive_messages);
            if (arsivle){
                builder.setMessage(R.string.are_you_sure_you_want_to_archive_messages);
            }else{
                builder.setMessage(R.string.are_you_sure_you_want_to_unarchive_messages);
            }
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                mesajlarArsivleniyor = true;
                if (arsivle){
                    progressBarText.setText(getString(R.string.messages_is_archiving));
                }else{
                    progressBarText.setText(getString(R.string.messages_is_unarchiving));
                }
                progressBarLayout.setVisibility(View.VISIBLE);
                for (Mesajlar mesajlar : mesajlarArrayList){
                    MesajFonksiyonlari.getInstance(mContext).MesajlarArsiv(mesajlar.getKullanici().getTelefon(), arsivle);
                }
                mesajlarList.removeAll(mesajlarArrayList);
                mesajlarAdapter.notifyDataSetChanged();
                progressBarLayout.setVisibility(View.GONE);
                progressBarText.setText("");
                mesajlarArsivleniyor = false;
                if (mainActivity != null){
                    mainActivity.MesajMenusu();
                }
            });
            builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            if (arsivle){
                Toast.makeText(mContext, mContext.getString(R.string.there_is_already_an_archiving_or_deleting_progress), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext, mContext.getString(R.string.there_is_already_an_unarchiving_or_deleting_progress), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MesajlariGuncelle(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        MesajlariGuncelle(false);
    }
}