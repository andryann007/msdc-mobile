package com.example.msdc.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.msdc.adapter.ImageAdapter;
import com.example.msdc.adapter.MovieAdapter;
import com.example.msdc.adapter.TVAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.MovieDetails;
import com.example.msdc.api.MovieRespon;
import com.example.msdc.api.MovieResult;
import com.example.msdc.api.TVDetails;
import com.example.msdc.api.TVRespon;
import com.example.msdc.api.TVResult;
import com.example.msdc.databinding.ActivityDetailBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailActivity extends AppCompatActivity {
    private ApiService apiService;
    private int movie_id;
    private int tv_id;
    private ActivityDetailBinding binding;
    private MovieAdapter movieRecommendationsAdapter, movieSimilarAdapter;
    private TVAdapter tvRecommendationsAdapter, tvSimilarAdapter;
    private final List<MovieResult> movieRecommendationsResult = new ArrayList<>();
    private int totalPagesMovieRecommendations = 1;
    private final List<MovieResult> movieSimilarResult = new ArrayList<>();
    private int totalPagesMovieSimilar = 1;
    private final List<TVResult> tvRecommendationsResult = new ArrayList<>();
    private int totalPagesTVRecommendations = 1;
    private final List<TVResult> tvSimilarResult = new ArrayList<>();
    private int totalPagesTVSimilar = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        String tipe = getIntent().getStringExtra("tipe");
        if(Objects.equals(tipe, "movie")){
            movie_id = getIntent().getIntExtra("movie_id", 0);
            setMovieDetails();
        } else if(Objects.equals(tipe, "tv")){
            tv_id = getIntent().getIntExtra("tv_id", 0);
            setTVDetails();
        }

        binding.imageBackDetails.setOnClickListener(v-> onBackPressed());
    }

    private void setMovieDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<MovieDetails> call = apiService.getMovieDetails(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<MovieDetails>(){

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {
                if(response.body() != null){
                    binding.loadingDetails.setVisibility(View.GONE);
                    binding.scrollView.setVisibility(View.VISIBLE);
                    binding.imagePosterBack.setVisibility(View.VISIBLE);

                    ImageAdapter.setPosterURL(binding.imagePosterBack, response.body().getPosterPath());
                    ImageAdapter.setPosterURL(binding.imageBackdrop, response.body().getBackdropPath());
                    ImageAdapter.setPosterURL(binding.imagePoster, response.body().getPosterPath());
                    binding.textTitle.setText(response.body().getTitle());
                    binding.textRunTime.setText("Runtime : " + response.body().getRuntime() + " Minutes");
                    binding.textReleaseDate.setText("Released on : " + response.body().getReleaseDate());
                    binding.textOverview.setText(response.body().getOverview());
                    binding.textLanguage.setText("Language : " + response.body().getLanguage());
                    binding.textStatus.setText("Status : " + response.body().getStatus());

                    Double budget = Double.parseDouble(response.body().getBudget());
                    String budgetFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(budget);
                    binding.textBudgetOrSeasons.setText("Budget : " + budgetFormatted);

                    Double revenue = Double.parseDouble(response.body().getRevenue());
                    String revenueFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(revenue);
                    binding.textRevenueOrEpisodes.setText("Revenue : " + revenueFormatted);

                    binding.textPopularity.setText("Popularity : " + response.body().getPopularity());
                    binding.textTagline.setText("Tagline : " + response.body().getTagline());
                    binding.textVoteCount.setText("Vote Count : " + response.body().getVoteCount());
                    binding.textVoteAverage.setText("Vote Average : " + response.body().getVoteAverage());
                    binding.textHomePage.setText("Homepage : " + response.body().getHomepage());

                    setRecommendationsMovie();
                    setSimilarMovie();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
                binding.loadingDetails.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat halaman detail!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecommendationsMovie(){
        movieRecommendationsAdapter = new MovieAdapter(movieRecommendationsResult, this);

        getRecommendationsMovie();
        binding.rvMovieRecommendations.setAdapter(movieRecommendationsAdapter);
    }

    private void getRecommendationsMovie(){
        Call<MovieRespon> call = apiService.getMovieRecommendations(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<MovieRespon>() {
            @Override
            public void onResponse(@NonNull Call<MovieRespon> call, @NonNull Response<MovieRespon> response) {
                assert response.body() != null;
                totalPagesMovieRecommendations = response.body().getTotalPages();
                if(response.body().getResult()!=null){
                    int oldCount = movieRecommendationsResult.size();
                    movieRecommendationsResult.addAll(response.body().getResult());
                    movieRecommendationsAdapter.notifyItemChanged(oldCount, movieRecommendationsResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieRespon> call, @NonNull Throwable t) {
            }
        });
    }

    private void setSimilarMovie(){
        movieSimilarAdapter = new MovieAdapter(movieSimilarResult, this);

        getSimilarMovie();
        binding.rvMovieSimilar.setAdapter(movieSimilarAdapter);
    }

    private void getSimilarMovie(){
        Call<MovieRespon> call = apiService.getMovieSimilar(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<MovieRespon>() {
            @Override
            public void onResponse(@NonNull Call<MovieRespon> call, @NonNull Response<MovieRespon> response) {
                assert response.body() != null;
                totalPagesMovieSimilar = response.body().getTotalPages();
                if(response.body().getResult()!=null){
                    int oldCount = movieSimilarResult.size();
                    movieSimilarResult.addAll(response.body().getResult());
                    movieSimilarAdapter.notifyItemChanged(oldCount, movieSimilarResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieRespon> call, @NonNull Throwable t) {
            }
        });
    }

    private void setTVDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<TVDetails> call = apiService.getTvDetails(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<TVDetails>(){

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<TVDetails> call, @NonNull Response<TVDetails> response) {
                if(response.body() != null){
                    binding.loadingDetails.setVisibility(View.GONE);
                    binding.scrollView.setVisibility(View.VISIBLE);
                    binding.imagePosterBack.setVisibility(View.VISIBLE);

                    ImageAdapter.setPosterURL(binding.imagePosterBack, response.body().getPosterPath());
                    ImageAdapter.setPosterURL(binding.imageBackdrop, response.body().getBackdropPath());
                    ImageAdapter.setPosterURL(binding.imagePoster, response.body().getPosterPath());
                    binding.textTitle.setText("Name : " + response.body().getName());
                    binding.textRunTime.setText("Episode Runtime : " + response.body().getEpisodeRuntime());
                    binding.textReleaseDate.setText("From : " + response.body().getFirstAirDate() + " - " + response.body().getLastAirDate());
                    binding.textOverview.setText(response.body().getOverview());
                    binding.textLanguage.setText("Language : " + response.body().getOriginalLanguage());
                    binding.textStatus.setText("Status : " + response.body().getStatus());
                    binding.textBudgetOrSeasons.setText("Number of Seasons : " + response.body().getNumberOfSeasons());
                    binding.textRevenueOrEpisodes.setText("Number of Episodes : " + response.body().getNumberOfEpisodes());

                    binding.textPopularity.setText("Popularity : " + response.body().getPopularity());
                    binding.textTagline.setText("Tagline : " + response.body().getTagline());
                    binding.textVoteCount.setText("Vote Count : " + response.body().getVoteCount());
                    binding.textVoteAverage.setText("Vote Average : " + response.body().getVoteAverage());
                    binding.textHomePage.setText("Homepage : " + response.body().getHomepage());

                    binding.textMovieRecommendations.setText("TV Recommendations");
                    binding.textMovieSimilar.setText("Similar TV");

                    setRecommendationsTV();
                    setSimilarTV();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVDetails> call, @NonNull Throwable t) {
                binding.loadingDetails.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat halaman detail dari " + tv_id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecommendationsTV(){
        tvRecommendationsAdapter = new TVAdapter(tvRecommendationsResult, this);

        getRecommendationsTV();
        binding.rvMovieRecommendations.setAdapter(tvRecommendationsAdapter);
    }

    private void getRecommendationsTV(){
        Call<TVRespon> call = apiService.getTVRecommendations(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<TVRespon>() {
            @Override
            public void onResponse(@NonNull Call<TVRespon> call, @NonNull Response<TVRespon> response) {
                assert response.body() != null;
                totalPagesTVRecommendations = response.body().getTotalPages();
                if(response.body().getResult()!=null){
                    int oldCount = tvRecommendationsResult.size();
                    tvRecommendationsResult.addAll(response.body().getResult());
                    tvRecommendationsAdapter.notifyItemChanged(oldCount, tvRecommendationsResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVRespon> call, @NonNull Throwable t) {
            }
        });
    }

    private void setSimilarTV(){
        tvSimilarAdapter = new TVAdapter(tvSimilarResult, this);

        getSimilarTV();
        binding.rvMovieSimilar.setAdapter(tvSimilarAdapter);
    }

    private void getSimilarTV(){
        Call<TVRespon> call = apiService.getTVSimilar(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<TVRespon>() {
            @Override
            public void onResponse(@NonNull Call<TVRespon> call, @NonNull Response<TVRespon> response) {
                assert response.body() != null;
                totalPagesTVSimilar = response.body().getTotalPages();
                if(response.body().getResult()!=null){
                    int oldCount = tvSimilarResult.size();
                    tvSimilarResult.addAll(response.body().getResult());
                    tvSimilarAdapter.notifyItemChanged(oldCount, tvSimilarResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVRespon> call, Throwable t) {
            }
        });
    }
}