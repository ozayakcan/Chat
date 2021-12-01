package com.ozayakcan.chat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozayakcan.chat.ChatApp;
import com.ozayakcan.chat.MesajActivity;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.Ozellik.Metinler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.List;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.ViewHolder> {

    public static  final int MESAJ_TURU_SOL = 0;
    public static  final int MESAJ_TURU_SAG = 1;
    private final Context mContext;
    List<Mesaj> mesajList;


    public MesajAdapter(MesajActivity mesajActivity, List<Mesaj> mesajList){
        this.mContext = mesajActivity;
        this.mesajList = mesajList;
    }

    @NonNull
    @Override
    public MesajAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType == MESAJ_TURU_SAG ? R.layout.mesaj_sag : R.layout.mesaj_sol, parent, false));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MesajAdapter.ViewHolder holder, int position) {
        Mesaj mesaj = mesajList.get(position);
        holder.yeniMesajSayisiText.setText(mesaj.getYeniMesajSayisi() > 0 ? mContext.getString(R.string.s_new_messages).replace("%s", String.valueOf(mesaj.getYeniMesajSayisi())) : "");
        holder.yeniMesajSayisiLayout.setVisibility(mesaj.getYeniMesajSayisi() > 0 ? View.VISIBLE : View.GONE);
        holder.tarihText.setText(mesaj.isTarihGoster() ? ChatApp.MesajTarihiBul(mesaj.getTarih(), false) : "");
        holder.tarihLayout.setVisibility(mesaj.isTarihGoster() ? View.VISIBLE : View.GONE);
        holder.mesajText.setText(mesaj.getMesaj());
        holder.mesajText.setMovementMethod(BetterLinkMovementMethod.newInstance());
        Linkify.addLinks(holder.mesajText, Linkify.ALL);
        BetterLinkMovementMethod.linkify(Linkify.ALL, holder.mesajText).setOnLinkLongClickListener((textView, url) -> {
            Metinler.PanoyaKopyala(mContext, url);
            return true;
        });
        holder.saat.setText(DateFormat.format("HH:mm", mesaj.getTarih()).toString());
        holder.mesajDurumu.setText(mesaj.isGonderen()
                ? mesaj.isGoruldu()
                ? mContext.getString(R.string.seen)
                : mesaj.getMesajDurumu() == Veritabani.MesajDurumuGonderiliyor
                ? mContext.getString(R.string.sending)
                : mesaj.getMesajDurumu() == Veritabani.MesajDurumuGonderildi
                ? mContext.getString(R.string.sent) : "" : "");
        holder.mesajDurumu.setVisibility(mesaj.isGonderen()
                ? mesaj.isGoruldu()
                ? View.VISIBLE
                : mesaj.getMesajDurumu() == Veritabani.MesajDurumuGonderiliyor
                ? View.VISIBLE
                : mesaj.getMesajDurumu() == Veritabani.MesajDurumuGonderildi
                ? View.VISIBLE : View.GONE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mesajList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mesajLayout, tarihLayout, yeniMesajSayisiLayout;
        TextView mesajText, saat, mesajDurumu, tarihText, yeniMesajSayisiText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mesajLayout = itemView.findViewById(R.id.mesajLayout);
            mesajText = itemView.findViewById(R.id.mesajText);
            saat = itemView.findViewById(R.id.saat);
            mesajDurumu = itemView.findViewById(R.id.mesajDurumu);
            tarihLayout = itemView.findViewById(R.id.tarihLayout);
            tarihText = itemView.findViewById(R.id.tarihText);
            yeniMesajSayisiLayout = itemView.findViewById(R.id.yeniMesajSayisiLayout);
            yeniMesajSayisiText = itemView.findViewById(R.id.yeniMesajSayisiText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mesajList.get(position).isGonderen()){
            return MESAJ_TURU_SAG;
        }else{
            return MESAJ_TURU_SOL;
        }
    }
}
