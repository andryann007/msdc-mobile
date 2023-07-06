package com.example.msdc.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

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
import com.example.msdc.api.TVResult;
import com.example.msdc.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private final List<MovieResult> movieResults = new ArrayList<>();
    private final List<TVResult> tvResults = new ArrayList<>();
    private String query;
    private MovieSearchAdapter movieSearchAdapter;
    private TVSearchAdapter tvSearchAdapter;
    private ApiService apiService;
    private int page = 1;
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;

        binding.rvSearch.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.VERTICAL));
        binding.loadingSearch.setVisibility(View.VISIBLE);

        movieSearchAdapter = new MovieSearchAdapter(movieResults, this);
        tvSearchAdapter = new TVSearchAdapter(tvResults, this);

        apiService = ApiClient.getClient().create(ApiService.class);
        query = getIntent().getStringExtra("searchFor");
        toolbar.setTitle(HtmlCompat.fromHtml("You searched for : <b>" + query + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        String type = getIntent().getStringExtra("type");

        switch(type){
            case "Movies" :
                searchForMovies(page);
                binding.rvSearch.setAdapter(movieSearchAdapter);

                binding.rvSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if(!binding.rvSearch.canScrollVertically(1)){
                            page++;
                            searchForMovies(page);
                        }
                    }
                });
                break;

            case "TV Shows" :
                searchForTV(page);
                binding.rvSearch.setAdapter(tvSearchAdapter);

                binding.rvSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        if(!binding.rvSearch.canScrollVertically(1)){
                            page++;
                            searchForTV(page);
                        }
                    }
                });
                break;
        }

        toolbar.setOnClickListener(v-> onBackPressed());
    }

    private void searchForMovies(int page) {
        int limit = 15;

        Call<MovieResponse> call = apiService.searchMovie(MainActivity.MY_API_KEY, MainActivity.LANGUAGE, query, page, limit);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        binding.loadingSearch.setVisibility(View.GONE);

                        int oldCount = movieResults.size();
                        movieResults.addAll(response.body().getResult());
                        movieSearchAdapter.notifyItemRangeInserted(oldCount, movieResults.size());
                    } else {
                        binding.loadingSearch.setVisibility(View.GONE);
                        binding.textNoResults.setVisibility(View.VISIBLE);
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                binding.loadingSearch.setVisibility(View.GONE);

                binding.textNoResults.setText("No Results, Try Search Again !!!");
                binding.textNoResults.setVisibility(View.VISIBLE);
            }
        });
    }

    private void searchForTV(int page) {
        int limit = 15;

        Call<TVResponse> call = apiService.searchTv(MainActivity.MY_API_KEY, MainActivity.LANGUAGE, query, page, limit);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        binding.loadingSearch.setVisibility(View.GONE);

                        int oldCount = tvResults.size();
                        tvResults.addAll(response.body().getResult());
                        tvSearchAdapter.notifyItemRangeInserted(oldCount, tvResults.size());
                    } else {
                        binding.loadingSearch.setVisibility(View.GONE);
                        binding.textNoResults.setVisibility(View.VISIBLE);
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                binding.loadingSearch.setVisibility(View.GONE);

                binding.textNoResults.setText("No Results, Try Search Again !!!");
                binding.textNoResults.setVisibility(View.VISIBLE);
            }
        });
    }
}