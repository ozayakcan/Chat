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

import com.ozayakcan.chat.ArsivActivity;
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
    private final ArsivActivity arsivActivity;
    private final Context mContext;

    public MesajlarAdapter( List<Mesajlar> mesajlarList, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.arsivActivity = null;
        this.mContext = mainActivity;
        this.mesajlarList = mesajlarList;
    }
    public MesajlarAdapter( List<Mesajlar> mesajlarList, ArsivActivity arsivActivity){
        this.arsivActivity = arsivActivity;
        this.mainActivity = null;
        this.mContext = arsivActivity;
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
        holder.secim.setVisibility(mesajlar.isSecildi() ? View.VISIBLE : View.GONE);
        holder.kisiAdi.setText(mesajlar.getIsim().equals("") ? mesajlar.getKullanici().getTelefon() : mesajlar.getIsim());
        holder.sonMesaj.setText(ChatApp.MesajBol(mesajlar.getMesaj().getMesaj(), ChatApp.MaxMesajKarakterSayisi));
        holder.tarih.setText(ChatApp.MesajTarihiBul(mesajlar.getMesaj().getTarih(), true));
        holder.okunmamisMesaj.setText(mesajlar.getOkumamisMesaj() > 0
                ? mesajlar.getOkumamisMesaj() >= 99
                ? "99+" : String.valueOf(mesajlar.getOkumamisMesaj()) : "");
        holder.okunmamisMesajLayout.setVisibility(mesajlar.getMesaj().isGonderen()
                ? View.GONE
                : mesajlar.getOkumamisMesaj() > 0
                ? View.VISIBLE : View.GONE);
        holder.mesajDurumu.setText(mesajlar.getMesaj().isGonderen()
                ? mesajlar.getMesaj().isGoruldu()
                ? mContext.getString(R.string.seen)
                : mesajlar.getMesaj().getMesajDurumu() == Veritabani.MesajDurumuGonderiliyor
                ? mContext.getString(R.string.sending)
                : mesajlar.getMesaj().getMesajDurumu() == Veritabani.MesajDurumuGonderildi
                ? mContext.getString(R.string.sent) : "" : "");
        holder.mesajDurumu.setVisibility(mesajlar.getMesaj().isGonderen()
                ? mesajlar.getMesaj().isGoruldu()
                ? View.VISIBLE
                : mesajlar.getMesaj().getMesajDurumu() == Veritabani.MesajDurumuGonderiliyor
                ? View.VISIBLE
                : mesajlar.getMesaj().getMesajDurumu() == Veritabani.MesajDurumuGonderildi
                ? View.VISIBLE : View.GONE : View.VISIBLE);
        if (!mesajlar.getKullanici().getProfilResmi().equals(Veritabani.VarsayilanDeger)){
            resimler.ResimGoster(mesajlar.getKullanici().getProfilResmi(), holder.profilResmi, R.drawable.varsayilan_arkaplan);
        }
        holder.kisiBasHarfi.setText(mesajlar.getKullanici().getProfilResmi().equals(Veritabani.VarsayilanDeger)
                ? mesajlar.getIsim().equals("")
                ? "#" : String.valueOf(mesajlar.getIsim().charAt(0)) : "");
        holder.mesaj.setOnLongClickListener(v -> {
            mesajlar.setSecildi(!mesajlar.isSecildi());
            holder.secim.setVisibility(mesajlar.isSecildi() ? View.VISIBLE : View.GONE);
            if (mainActivity != null){
                mainActivity.MesajBasiliTut(true);
                mainActivity.SecilenMesajSayisiniGoster(mesajlar.isSecildi());
            }
            if (arsivActivity != null){
                arsivActivity.MesajBasiliTut(true);
                arsivActivity.SecilenMesajSayisiniGoster(mesajlar.isSecildi());
            }
            return true;
        });
        holder.mesaj.setOnClickListener(v -> {
            if (mainActivity != null){
                if (mainActivity.MesajSecildi){
                    mesajlar.setSecildi(!mesajlar.isSecildi());
                    holder.secim.setVisibility(mesajlar.isSecildi() ? View.VISIBLE : View.GONE);
                    mainActivity.SecilenMesajSayisiniGoster(mesajlar.isSecildi());
                }else{
                    mainActivity.MesajGoster(mesajlar.getKullanici().getID(), mesajlar.getIsim(), mesajlar.getKullanici().getTelefon(), mesajlar.getKullanici().getProfilResmi());
                }
            }
            if (arsivActivity != null){
                if (arsivActivity.MesajSecildi){
                    mesajlar.setSecildi(!mesajlar.isSecildi());
                    holder.secim.setVisibility(mesajlar.isSecildi() ? View.VISIBLE : View.GONE);
                    arsivActivity.SecilenMesajSayisiniGoster(mesajlar.isSecildi());
                }else{
                    arsivActivity.MesajGoster(mesajlar.getKullanici().getID(), mesajlar.getIsim(), mesajlar.getKullanici().getTelefon(), mesajlar.getKullanici().getProfilResmi());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mesajlarList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mesaj, secim;
        public CircleImageView profilResmi;
        public TextView kisiBasHarfi, kisiAdi, tarih, sonMesaj, mesajDurumu, okunmamisMesaj;
        public RelativeLayout okunmamisMesajLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mesaj = itemView.findViewById(R.id.mesaj);
            profilResmi = itemView.findViewById(R.id.profilResmi);
            kisiBasHarfi = itemView.findViewById(R.id.kisiBasHarfi);
            secim = itemView.findViewById(R.id.secim);
            kisiAdi = itemView.findViewById(R.id.kisiAdi);
            tarih = itemView.findViewById(R.id.tarih);
            sonMesaj = itemView.findViewById(R.id.sonMesaj);
            mesajDurumu = itemView.findViewById(R.id.mesajDurumu);
            okunmamisMesaj = itemView.findViewById(R.id.okunmamisMesaj);
            okunmamisMesajLayout = itemView.findViewById(R.id.okunmamisMesajLayout);
        }
    }
}
