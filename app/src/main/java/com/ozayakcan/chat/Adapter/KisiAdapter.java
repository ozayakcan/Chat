package com.ozayakcan.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Resimler;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KisiAdapter extends RecyclerView.Adapter<KisiAdapter.ViewHolder> {

    private Resimler resimler;
    List<Kullanici> kullaniciList;
    private final MainActivity mainActivity;
    private final Context mContext;

    public KisiAdapter( List<Kullanici> kullaniciList, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.mContext = mainActivity;
        this.kullaniciList = kullaniciList;
    }

    @NonNull
    @Override
    public KisiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.kisi_listesi, parent, false);
        resimler = new Resimler(mContext);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KisiAdapter.ViewHolder holder, int position) {
        Kullanici kullanici = kullaniciList.get(position);
        holder.kisiAdi.setText(kullanici.getIsim());
        holder.kisiHakkinda.setText(kullanici.getHakkimda());
        if (!kullanici.getProfilResmi().equals(Veritabani.VarsayilanDeger)){
            resimler.ResimGoster(kullanici.getProfilResmi(), holder.profilResmi, R.drawable.varsayilan_arkaplan);
        }
        holder.kisiBasHarfi.setText(kullanici.getProfilResmi().equals(Veritabani.VarsayilanDeger)
                ? String.valueOf(kullanici.getIsim().charAt(0)) : "");
        holder.kisi.setOnClickListener(v -> mainActivity.MesajGoster(kullanici.getID(), kullanici.getIsim(), kullanici.getTelefon(), kullanici.getProfilResmi()));
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
