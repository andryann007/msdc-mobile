package com.example.msdc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.msdc.databinding.ActivityRegisterBinding;
import com.example.msdc.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private final List<MovieResult> movieResult = new ArrayList<>();
    private final List<TVResult> TVResult = new ArrayList<>();
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

        textSearchQuery = findViewById(R.id.textSearch);

        binding.rvSearch.setLayoutManager(new StaggeredGridLayoutManager(4, RecyclerView.VERTICAL));
        binding.loadingSearch.setVisibility(View.VISIBLE);

        movieSearchAdapter = new MovieSearchAdapter(movieResult, this);
        tvSearchAdapter = new TVSearchAdapter(TVResult, this);

        apiService = ApiClient.getClient().create(ApiService.class);
        query = getIntent().getStringExtra("searchFor");
        textSearchQuery.setText(HtmlCompat.fromHtml("You searched for : <b>" + query + "</b>",
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

        /*binding.rvSearch.addOnScrollListener(onScrolled(recyclerView, dx, dy) + {
            super.onScrolled(recyclerView, dx, dy);
            if (!binding.rvSearch.canScrollVertically(1)){
                if(currentPage <= totalPages){
                currentPage += 1;
                if(tipe.equals("Movies"))
                    searchForMovies();
                else searchForTV();
                }
            }
        });*/
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void searchForMovies() {
        Call<MovieRespon> call = apiService.searchMovie(MainActivity.MYAPI_KEY, MainActivity.LANGUAGE, query, currentPage);
        call.enqueue(new Callback<MovieRespon>() {
            @Override
            public void onResponse(Call<MovieRespon> call, Response<MovieRespon> response) {
                if(response.body() != null){
                    if(response.body().getResult().size() > 8){
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
            public void onFailure(Call<MovieRespon> call, Throwable t) {
                binding.loadingSearch.setVisibility(View.GONE);
                binding.textNoResults.setVisibility(View.VISIBLE);
                binding.textNoResults.setText("Something wrong, with connection !!!");
            }
        });
    }

    private void searchForTV() {
        Call<TVRespon> call = apiService.searchMovie(MainActivity.MYAPI_KEY, MainActivity.LANGUAGE, query, currentPage);
        call.enqueue(new Callback<TVRespon>() {
            @Override
            public void onResponse(Call<TVRespon> call, Response<TVRespon> response) {
                if(response.body() != null){
                    if(response.body().getResult().size() > 8){
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
            public void onFailure(Call<TVRespon> call, Throwable t) {
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