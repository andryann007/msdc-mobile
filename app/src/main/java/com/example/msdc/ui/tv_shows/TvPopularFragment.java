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
import com.example.msdc.databinding.FragmentTvPopularBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TvPopularFragment extends Fragment {
    private ApiService apiService;
    private TVGridAdapter tvPopularAdapter;
    private final List<TVResult> tvPopularResults = new ArrayList<>();

    private RecyclerView rvTvPopular;
    private ProgressBar loadingTvPopular;
    private TextView textNoResult;

    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";
    private int page = 1;
    private FragmentTvPopularBinding binding;

    public TvPopularFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentTvPopularBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setPopularTV(root);

        return root;
    }

    private void setPopularTV(View view) {
        rvTvPopular = view.findViewById(R.id.rvTvPopularList);
        loadingTvPopular = view.findViewById(R.id.loadingTvPopularList);
        textNoResult = view.findViewById(R.id.textNoTvPopularResult);

        tvPopularAdapter = new TVGridAdapter(tvPopularResults, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        rvTvPopular.setLayoutManager(gridLayoutManager);
        rvTvPopular.setAdapter(tvPopularAdapter);
        rvTvPopular.setHasFixedSize(true);
        rvTvPopular.setItemViewCacheSize(9);
        rvTvPopular.setDrawingCacheEnabled(true);
        rvTvPopular.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        getPopularTV(page);

        rvTvPopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvTvPopular.canScrollVertically(1)){
                    page++;
                    getPopularTV(page);
                }
            }
        });
    }

    private void getPopularTV(int page){
        int limit = 9;
        Call<TVResponse> call = apiService.getTvPopular(MY_API_KEY, LANGUAGE, page, limit);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    loadingTvPopular.setVisibility(View.GONE);
                    rvTvPopular.setVisibility(View.VISIBLE);

                    int oldCount = tvPopularResults.size();
                    tvPopularResults.addAll(response.body().getResult());
                    tvPopularAdapter.notifyItemRangeInserted(oldCount, tvPopularResults.size());
                } else if(tvPopularResults.isEmpty()){
                    loadingTvPopular.setVisibility(View.GONE);
                    textNoResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                loadingTvPopular.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Popular TV Shows !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}