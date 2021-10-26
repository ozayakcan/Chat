package com.ozayakcan.chat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozayakcan.chat.Model.Mesajlar;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajlarAdapter extends RecyclerView.Adapter<MesajlarAdapter.ViewHolder> {

    private Context mContext;
    private Resimler resimler;
    List<Mesajlar> mesajlarList;

    public MesajlarAdapter(Context mContext, List<Mesajlar> mesajlarList){
        this.mContext = mContext;
        this.mesajlarList = mesajlarList;
        resimler = new Resimler(mContext);
    }

    @NonNull
    @Override
    public MesajlarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_listesi, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MesajlarAdapter.ViewHolder holder, int position) {
        Mesajlar mesajlar = mesajlarList.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        resimler.ResimGoster(mesajlar.getProfilResmi(), holder.profilResmi, R.drawable.varsayilan_arkaplan);
        if (mesajlar.getIsim().equals("")){
            if (mesajlar.getGonderen().equals(firebaseUser.getPhoneNumber())){
                holder.kisiAdi.setText(mesajlar.getAlan());
            }else{
                holder.kisiAdi.setText(mesajlar.getGonderen());
            }
            if (holder.profilResmi.getDrawable().getConstantState().equals(mContext.getDrawable(R.drawable.varsayilan_arkaplan).getConstantState())){
                holder.kisiBasHarfi.setText("#");
            }
        }else{
            holder.kisiAdi.setText(mesajlar.getIsim());
            if (holder.profilResmi.getDrawable().getConstantState().equals(mContext.getDrawable(R.drawable.varsayilan_arkaplan).getConstantState())){
                holder.kisiBasHarfi.setText(String.valueOf(mesajlar.getIsim().charAt(0)));
            }
        }
        holder.sonMesaj.setText(mesajlar.getSonMesaj());
        if (mesajlar.getOkunmamiMesajSayisi() > 0){
            if (mesajlar.getOkunmamiMesajSayisi() > 99){
                holder.mesajSayisi.setText("+99");
            }else{
                holder.mesajSayisi.setText(String.valueOf(mesajlar.getOkunmamiMesajSayisi()));
            }
            holder.okunmamisMesaj.setVisibility(View.VISIBLE);
        }else{
            holder.okunmamisMesaj.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mesajlarList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mesajlar;
        public CircleImageView profilResmi;
        public TextView kisiBasHarfi, kisiAdi, sonMesaj, mesajSayisi;
        public RelativeLayout okunmamisMesaj;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mesajlar = itemView.findViewById(R.id.mesajlar);
            profilResmi = itemView.findViewById(R.id.profilResmi);
            kisiBasHarfi = itemView.findViewById(R.id.kisiBasHarfi);
            kisiAdi = itemView.findViewById(R.id.kisiAdi);
            sonMesaj = itemView.findViewById(R.id.sonMesaj);
            okunmamisMesaj = itemView.findViewById(R.id.okunmamisMesaj);
            mesajSayisi = itemView.findViewById(R.id.mesajSayisi);
        }
    }
}
