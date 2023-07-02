package com.example.msdc.ui.tv_shows;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.adapter.TVGridAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.TVResponse;
import com.example.msdc.api.TVResult;
import com.example.msdc.databinding.FragmentTvAiringTodayBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TvAiringTodayFragment extends Fragment {
    private ApiService apiService;
    private TVGridAdapter tvAiringTodayAdapter;
    private final List<TVResult> tvAiringTodayResults = new ArrayList<>();

    private RecyclerView rvTvAiringToday;
    private ProgressBar loadingTvAiringToday;
    private TextView textNoResult;

    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";
    private int page = 1;

    private FragmentTvAiringTodayBinding binding;

    public TvAiringTodayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentTvAiringTodayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setOnAiringTV(root);

        return root;
    }

    private void setOnAiringTV(View view) {
        rvTvAiringToday = view.findViewById(R.id.rvTvAiringTodayList);
        loadingTvAiringToday = view.findViewById(R.id.loadingTvAiringTodayList);
        textNoResult = view.findViewById(R.id.textNoTvAiringTodayResult);

        tvAiringTodayAdapter = new TVGridAdapter(tvAiringTodayResults, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        rvTvAiringToday.setLayoutManager(gridLayoutManager);
        rvTvAiringToday.setAdapter(tvAiringTodayAdapter);
        rvTvAiringToday.setHasFixedSize(true);
        rvTvAiringToday.setItemViewCacheSize(9);
        rvTvAiringToday.setDrawingCacheEnabled(true);
        rvTvAiringToday.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        getOnAiringTV(page);

        rvTvAiringToday.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvTvAiringToday.canScrollVertically(1)){
                    page++;
                    getOnAiringTV(page);
                }
            }
        });
    }

    private void getOnAiringTV(int page){
        int limit = 9;
        Call<TVResponse> call = apiService.getTvAiringToday(MY_API_KEY, LANGUAGE, page, limit);
        call.enqueue(new Callback<TVResponse>(){
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    loadingTvAiringToday.setVisibility(View.GONE);
                    rvTvAiringToday.setVisibility(View.VISIBLE);

                    int oldCount = tvAiringTodayResults.size();
                    tvAiringTodayResults.addAll(response.body().getResult());
                    tvAiringTodayAdapter.notifyItemRangeInserted(oldCount, tvAiringTodayResults.size());
                } else if(tvAiringTodayResults.isEmpty()){
                    loadingTvAiringToday.setVisibility(View.GONE);
                    textNoResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                loadingTvAiringToday.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Airing TV Shows !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}