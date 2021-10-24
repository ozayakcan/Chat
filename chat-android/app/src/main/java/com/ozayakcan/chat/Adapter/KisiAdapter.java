package com.ozayakcan.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.R;

import java.util.List;

public class KisiAdapter extends RecyclerView.Adapter<KisiAdapter.ViewHolder> {

    private Context mContext;
    List<Kullanici> kullaniciList;

    public KisiAdapter(Context mContext, List<Kullanici> kullaniciList){
        this.mContext = mContext;
        this.kullaniciList = kullaniciList;
    }

    @NonNull
    @Override
    public KisiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.kisi_listesi, parent, false);
        return new KisiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KisiAdapter.ViewHolder holder, int position) {
        holder.kisiTW.setText(kullaniciList.get(position).getIsim());
    }

    @Override
    public int getItemCount() {
        return kullaniciList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView kisiTW;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            kisiTW = itemView.findViewById(R.id.kisiTW);

        }
    }
}
