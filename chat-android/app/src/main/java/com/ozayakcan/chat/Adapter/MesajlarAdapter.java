package com.ozayakcan.chat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.MesajActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Model.Mesajlar;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajlarAdapter extends RecyclerView.Adapter<MesajlarAdapter.ViewHolder> {

    private Resimler resimler;
    List<Mesajlar> mesajlarList;
    private MainActivity mainActivity;
    private Context mContext;

    public MesajlarAdapter( List<Mesajlar> mesajlarList, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.mContext = mainActivity;
        this.mesajlarList = mesajlarList;
    }

    @NonNull
    @Override
    public MesajlarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.kisi_listesi, parent, false);
        resimler = new Resimler(mContext);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MesajlarAdapter.ViewHolder holder, int position) {
        Mesajlar mesajlar = mesajlarList.get(position);
        if (mesajlar.getIsim().equals("")){
            holder.kisiAdi.setText(mesajlar.getKullanici().getTelefon());
        }else{
            holder.kisiAdi.setText(mesajlar.getIsim());
        }

        holder.kisiHakkinda.setText(mesajlar.getMesaj().getMesaj());
        if (!mesajlar.getKullanici().getProfilResmi().equals(Veritabani.VarsayilanDeger)){
            resimler.ResimGoster(mesajlar.getKullanici().getProfilResmi(), holder.profilResmi, R.drawable.varsayilan_arkaplan);
        }
        if (mesajlar.getKullanici().getProfilResmi().equals(Veritabani.VarsayilanDeger)){
            if (mesajlar.getIsim().equals("")){
                holder.kisiBasHarfi.setText("#");
            }else{
                holder.kisiBasHarfi.setText(String.valueOf(mesajlar.getIsim().charAt(0)));
            }
        }
        holder.kisi.setOnClickListener(v -> {
            mainActivity.MesajGoster(mesajlar.getKullanici().getID(), mesajlar.getKullanici().getIsim(), mesajlar.getKullanici().getTelefon(), mesajlar.getKullanici().getProfilResmi());
        });
    }

    @Override
    public int getItemCount() {
        return mesajlarList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout kisi;
        public CircleImageView profilResmi;
        public TextView kisiBasHarfi, kisiAdi, kisiHakkinda;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kisi = itemView.findViewById(R.id.kisi);
            profilResmi = itemView.findViewById(R.id.profilResmi);
            kisiBasHarfi = itemView.findViewById(R.id.kisiBasHarfi);
            kisiAdi = itemView.findViewById(R.id.kisiAdi);
            kisiHakkinda = itemView.findViewById(R.id.kisiHakkinda);
        }
    }
}
