package com.ozayakcan.chat.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class VPAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragmentler;
    private final ArrayList<String> basliklar;

    public VPAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.fragmentler = new ArrayList<>();
        this.basliklar = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentler.get(position);
    }

    public void fragmentEkle(Fragment fragment, String baslik){
        fragmentler.add(fragment);
        basliklar.add(baslik);
    }

    public String baslikGetir(int position){
        return basliklar.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentler.size();
    }
}
