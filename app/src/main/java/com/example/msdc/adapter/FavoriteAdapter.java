package com.example.msdc.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.msdc.ui.account.FavoriteMovieFragment;
import com.example.msdc.ui.account.FavoriteTVFragment;

import java.util.ArrayList;

public class FavoriteAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> fragmentList = new ArrayList<>();

    public FavoriteAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FavoriteMovieFragment();

            case 1:
                return new FavoriteTVFragment();
        }
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
