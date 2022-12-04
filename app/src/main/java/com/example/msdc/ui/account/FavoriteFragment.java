package com.example.msdc.ui.account;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msdc.adapter.FavoriteAdapter;
import com.example.msdc.databinding.FragmentFavoriteBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FavoriteAdapter favoriteAdapter = new FavoriteAdapter(getChildFragmentManager(), getLifecycle());
        favoriteAdapter.createFragment(binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(favoriteAdapter);
        return root;
    }
}