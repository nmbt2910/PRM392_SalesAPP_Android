package com.prm392.salesapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProductListFragment();
            case 1:
                return new CartFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new ProductListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}