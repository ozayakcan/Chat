package com.ozayakcan.chat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ozayakcan.chat.R;

public class MesajlarFragment extends Fragment {

    private View view;

    public MesajlarFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kisiler, container, false);
        return view;
    }
}