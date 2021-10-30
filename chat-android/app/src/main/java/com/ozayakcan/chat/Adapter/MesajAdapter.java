package com.ozayakcan.chat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozayakcan.chat.MesajActivity;
import com.ozayakcan.chat.Model.Mesaj;
import com.ozayakcan.chat.R;

import java.util.List;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.ViewHolder> {

    public static  final int MESAJ_TURU_SOL = 0;
    public static  final int MESAJ_TURU_SAG = 1;

    private final MesajActivity mesajActivity;
    private final Context mContext;
    List<Mesaj> mesajList;


    public MesajAdapter(MesajActivity mesajActivity, List<Mesaj> mesajList){
        this.mesajActivity = mesajActivity;
        this.mContext = mesajActivity;
        this.mesajList = mesajList;
    }

    @NonNull
    @Override
    public MesajAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESAJ_TURU_SAG){
            view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_sag, parent, false);
        }else{
            view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_sol, parent, false);
        }
        return new ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MesajAdapter.ViewHolder holder, int position) {
        Mesaj mesaj = mesajList.get(position);

        holder.mesajText.setText(mesaj.getMesaj());
        holder.saat.setText(DateFormat.format("HH:mm", mesaj.getTarih()).toString());
        if (mesaj.isGonderen()){
            if (mesaj.isGoruldu()){
                holder.goruldu.setVisibility(View.VISIBLE);
            }else{
                holder.goruldu.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mesajList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mesajLayout;
        TextView mesajText, saat, goruldu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mesajLayout = itemView.findViewById(R.id.mesajLayout);
            mesajText = itemView.findViewById(R.id.mesajText);
            saat = itemView.findViewById(R.id.saat);
            goruldu = itemView.findViewById(R.id.goruldu);
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
