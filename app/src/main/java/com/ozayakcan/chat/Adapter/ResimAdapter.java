package com.ozayakcan.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ozayakcan.chat.Model.Resim;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.Resim.ResimlerClass;

import java.io.File;
import java.util.List;

public class ResimAdapter extends ArrayAdapter<Resim> {

    private final List<Resim> resimList;
    private final Context mContext;

    public ResimAdapter(@NonNull Context context, List<Resim> resimList) {
        super(context, 0, resimList);
        this.mContext = context;
        this.resimList = resimList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View resimListView = convertView;
        if (resimListView == null){
            resimListView = LayoutInflater.from(mContext).inflate(R.layout.item_resim, parent, false);
        }
        Resim resim = resimList.get(position);
        ImageView resimIW = resimListView.findViewById(R.id.resim);
        ResimlerClass.getInstance(mContext).ResimGoster(new File(resim.getKonum()), resimIW, R.drawable.ic_baseline_image_100);
        return resimListView;
    }
}
