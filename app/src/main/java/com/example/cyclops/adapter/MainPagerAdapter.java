package com.example.cyclops.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cyclops.view.TodayFragment;
import com.example.cyclops.view.HabitListFragment;
import com.example.cyclops.view.StatsFragment;
import com.example.cyclops.view.DiscoverFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TodayFragment();
            case 1:
                return new HabitListFragment();
            case 2:
                return new StatsFragment();
            case 3:
                return new DiscoverFragment();
            default:
                return new TodayFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // 四个主要页面
    }
}