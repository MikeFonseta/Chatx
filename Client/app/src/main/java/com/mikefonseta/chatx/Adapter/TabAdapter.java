package com.mikefonseta.chatx.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mikefonseta.chatx.Fragment.TabFragment;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TabFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
