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

import com.ozayakcan.chat.ChatApp;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Mesajlar;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajlarAdapter extends RecyclerView.Adapter<MesajlarAdapter.ViewHolder> {

    private Resimler resimler;
    List<Mesajlar> mesajlarList;
    private final MainActivity mainActivity;
    private final Context mContext;

    public MesajlarAdapter( List<Mesajlar> mesajlarList, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.mContext = mainActivity;
        this.mesajlarList = mesajlarList;
    }

    @NonNull
    @Override
    public MesajlarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_listesi, parent, false);
        resimler = new Resimler(mContext);
        return new ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull MesajlarAdapter.ViewHolder holder, int position) {
        Mesajlar mesajlar = mesajlarList.get(position);
        if (mesajlar.getIsim().equals("")){
            holder.kisiAdi.setText(mesajlar.getKullanici().getTelefon());
        }else{
            holder.kisiAdi.setText(mesajlar.getIsim());
        }
        holder.sonMesaj.setText(mesajlar.getMesaj().getMesaj());
        holder.tarih.setText(ChatApp.MesajTarihiBul(mesajlar.getMesaj().getTarih()));
        if (mesajlar.getMesaj().isGonderen()){
            holder.okunmamisMesajLayout.setVisibility(View.GONE);
            if (mesajlar.getMesaj().isGoruldu()){
                holder.mesajDurumu.setText(mContext.getString(R.string.seen));
                holder.mesajDurumu.setVisibility(View.VISIBLE);
            }else if (mesajlar.getMesaj().getMesajDurumu() == Veritabani.MesajDurumuGonderiliyor){
                holder.mesajDurumu.setText(mContext.getString(R.string.sending));
                holder.mesajDurumu.setVisibility(View.VISIBLE);
            }else if (mesajlar.getMesaj().getMesajDurumu() == Veritabani.MesajDurumuGonderildi){
                holder.mesajDurumu.setText(mContext.getString(R.string.sent));
                holder.mesajDurumu.setVisibility(View.VISIBLE);
            }else{
                holder.mesajDurumu.setText("");
                holder.mesajDurumu.setVisibility(View.GONE);
            }
        }else{
            holder.mesajDurumu.setVisibility(View.VISIBLE);
            holder.mesajDurumu.setText("");
            if (mesajlar.getOkumamisMesaj() > 0){
                if (mesajlar.getOkumamisMesaj() >= 99){
                    holder.okunmamisMesaj.setText("99+");
                }else{
                    holder.okunmamisMesaj.setText(String.valueOf(mesajlar.getOkumamisMesaj()));
                }
                holder.okunmamisMesajLayout.setVisibility(View.VISIBLE);
            }
        }
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
        holder.mesaj.setOnLongClickListener(v -> {
            mainActivity.MesajBasiliTut(mesajlar.getKullanici().getID(), mesajlar.getKullanici().getIsim(), mesajlar.getKullanici().getTelefon(), mesajlar.getKullanici().getProfilResmi(), position);
            return true;
        });
        holder.mesaj.setOnClickListener(v -> {
            if (mainActivity != null){
                mainActivity.MesajGoster(mesajlar.getKullanici().getID(), mesajlar.getKullanici().getIsim(), mesajlar.getKullanici().getTelefon(), mesajlar.getKullanici().getProfilResmi());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mesajlarList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mesaj;
        public CircleImageView profilResmi;
        public TextView kisiBasHarfi, kisiAdi, tarih, sonMesaj, mesajDurumu, okunmamisMesaj;
        public RelativeLayout okunmamisMesajLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mesaj = itemView.findViewById(R.id.mesaj);
            profilResmi = itemView.findViewById(R.id.profilResmi);
            kisiBasHarfi = itemView.findViewById(R.id.kisiBasHarfi);
            kisiAdi = itemView.findViewById(R.id.kisiAdi);
            tarih = itemView.findViewById(R.id.tarih);
            sonMesaj = itemView.findViewById(R.id.sonMesaj);
            mesajDurumu = itemView.findViewById(R.id.mesajDurumu);
            okunmamisMesaj = itemView.findViewById(R.id.okunmamisMesaj);
            okunmamisMesajLayout = itemView.findViewById(R.id.okunmamisMesajLayout);
        }
    }
}
