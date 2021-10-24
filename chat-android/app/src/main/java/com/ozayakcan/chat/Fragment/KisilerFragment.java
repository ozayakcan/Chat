package com.ozayakcan.chat.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ozayakcan.chat.R;

public class KisilerFragment extends Fragment {

    private Context mContext;

    public KisilerFragment(Context context) {
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kisiler, container, false);
    }
}