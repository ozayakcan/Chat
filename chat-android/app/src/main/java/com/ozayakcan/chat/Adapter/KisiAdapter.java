package com.ozayakcan.chat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KisiAdapter extends RecyclerView.Adapter<KisiAdapter.ViewHolder> {

    private Context mContext;
    private Resimler resimler;
    List<Kullanici> kullaniciList;

    public KisiAdapter(Context mContext, List<Kullanici> kullaniciList){
        this.mContext = mContext;
        this.kullaniciList = kullaniciList;
        resimler = new Resimler(mContext);
    }

    @NonNull
    @Override
    public KisiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.kisi_listesi, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull KisiAdapter.ViewHolder holder, int position) {
        Kullanici kullanici = kullaniciList.get(position);
        holder.kisiAdi.setText(kullanici.getIsim());
        holder.kisiHakkinda.setText(kullanici.getHakkimda());
        if (!kullanici.getProfilResmi().equals(Veritabani.VarsayilanDeger)){
            resimler.ResimGoster(kullanici.getProfilResmi(), holder.profilResmi, R.drawable.varsayilan_arkaplan);
        }
        if (holder.profilResmi.getDrawable().getConstantState().equals(mContext.getDrawable(R.drawable.varsayilan_arkaplan).getConstantState())){
            holder.kisiBasHarfi.setText(String.valueOf(kullanici.getIsim().charAt(0)));
        }
        holder.kisi.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return kullaniciList.size();
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
