package com.example.msdc.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.msdc.adapter.MovieSearchAdapter;
import com.example.msdc.adapter.TVSearchAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.api.TVResponse;
import com.example.msdc.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private final List<MovieResult> movieResult = new ArrayList<>();
    private final List<com.example.msdc.api.TVResult> TVResult = new ArrayList<>();
    private String query, tipe;
    private RecyclerView rvSearch;
    private MovieSearchAdapter movieSearchAdapter;
    private TVSearchAdapter tvSearchAdapter;
    private ApiService apiService;
    private ProgressBar loadingSearch;
    private TextView textNoResults, textSearchQuery;
    private int currentPage = 1;
    private int totalPages = 1;
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;

        binding.rvSearch.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.VERTICAL));
        binding.loadingSearch.setVisibility(View.VISIBLE);

        movieSearchAdapter = new MovieSearchAdapter(movieResult, this);
        tvSearchAdapter = new TVSearchAdapter(TVResult, this);

        apiService = ApiClient.getClient().create(ApiService.class);
        query = getIntent().getStringExtra("searchFor");
        toolbar.setTitle(HtmlCompat.fromHtml("You searched for : <b>" + query + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        tipe = getIntent().getStringExtra("tipe");

        switch(tipe){
            case "Movies" :
                searchForMovies();
                binding.rvSearch.setAdapter(movieSearchAdapter);
                break;

            case "TV Shows" :
                searchForTV();
                binding.rvSearch.setAdapter(tvSearchAdapter);
                break;
        }

        toolbar.setOnClickListener(v-> onBackPressed());
    }

    private void searchForMovies() {
        Call<MovieResponse> call = apiService.searchMovie(MainActivity.MYAPI_KEY, MainActivity.LANGUAGE, query, currentPage);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult().size() > 0){
                        binding.loadingSearch.setVisibility(View.GONE);
                        totalPages = response.body().getTotalPages();
                        int oldCount = movieResult.size();
                        movieResult.addAll(response.body().getResult());
                        movieSearchAdapter.notifyItemRangeInserted(oldCount, movieResult.size());
                        checkSize(response.body().getResult().size());
                    } else {
                        binding.loadingSearch.setVisibility(View.GONE);
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                binding.loadingSearch.setVisibility(View.GONE);
                binding.textNoResults.setVisibility(View.VISIBLE);
                binding.textNoResults.setText("Something wrong, with connection !!!");
            }
        });
    }

    private void searchForTV() {
        Call<TVResponse> call = apiService.searchTv(MainActivity.MYAPI_KEY, MainActivity.LANGUAGE, query, currentPage);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult().size() > 0){
                        binding.loadingSearch.setVisibility(View.GONE);
                        totalPages = response.body().getTotalPages();
                        int oldCount = TVResult.size();
                        TVResult.addAll(response.body().getResult());
                        tvSearchAdapter.notifyItemRangeInserted(oldCount, TVResult.size());
                        checkSize(response.body().getResult().size());
                    } else {
                        binding.loadingSearch.setVisibility(View.GONE);
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                binding.loadingSearch.setVisibility(View.GONE);
                binding.textNoResults.setVisibility(View.VISIBLE);
                binding.textNoResults.setText("Something wrong, with connection !!!");
            }
        });
    }

    private void checkSize(int result){
        if(result==0){
            binding.textNoResults.setVisibility(View.VISIBLE);
        } else {
            binding.textNoResults.setVisibility(View.GONE);
        }
    }
}