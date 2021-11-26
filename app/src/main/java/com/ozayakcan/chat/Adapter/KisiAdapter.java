package com.ozayakcan.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Resimler.ResimlerClass;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KisiAdapter extends RecyclerView.Adapter<KisiAdapter.ViewHolder> {

    private ResimlerClass resimlerClass;
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
        resimlerClass = new ResimlerClass(mContext);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KisiAdapter.ViewHolder holder, int position) {
        Kullanici kullanici = kullaniciList.get(position);
        holder.kisiAdi.setText(kullanici.getIsim());
        holder.kisiHakkinda.setText(kullanici.getHakkimda());
        if (!kullanici.getProfilResmi().equals(Veritabani.VarsayilanDeger)){
            resimlerClass.ResimGoster(kullanici.getProfilResmi(), holder.profilResmi, R.drawable.varsayilan_arkaplan);
        }
        holder.kisiBasHarfi.setText(kullanici.getProfilResmi().equals(Veritabani.VarsayilanDeger)
                ? String.valueOf(kullanici.getIsim().charAt(0)) : "");
        holder.kisiBilgileriLayout.setOnClickListener(v -> mainActivity.MesajGoster(kullanici.getID(), kullanici.getIsim(), kullanici.getTelefon(), kullanici.getProfilResmi()));
        holder.profilResmiLayout.setOnClickListener(v -> resimlerClass.ProfilResmiGoruntule(holder.kisiAdi.getText().toString(), kullanici.getProfilResmi()));
    }

    @Override
    public int getItemCount() {
        return kullaniciList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout kisiBilgileriLayout;
        public RelativeLayout profilResmiLayout;
        public CircleImageView profilResmi;
        public TextView kisiBasHarfi, kisiAdi, kisiHakkinda;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilResmiLayout = itemView.findViewById(R.id.profilResmiLayout);
            kisiBilgileriLayout = itemView.findViewById(R.id.kisiBilgileriLayout);
            profilResmi = itemView.findViewById(R.id.profilResmi);
            kisiBasHarfi = itemView.findViewById(R.id.kisiBasHarfi);
            kisiAdi = itemView.findViewById(R.id.kisiAdi);
            kisiHakkinda = itemView.findViewById(R.id.kisiHakkinda);
        }
    }
}
