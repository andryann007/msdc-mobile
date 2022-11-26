package com.example.msdc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.msdc.databinding.ActivityDetailBinding;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailActivity extends AppCompatActivity {
    private int id;
    private ApiService apiService;
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        String tipe = getIntent().getStringExtra("tipe");
        switch (tipe){
            case "movie":
                id = getIntent().getIntExtra("id", 0);
                setMovieDetails();
                break;

            case "tv":
                id = getIntent().getIntExtra("id", 0);
                setTVDetails();
                break;
        }
        binding.imageBackDetails.setOnClickListener(v-> onBackPressed());
    }

    private void setMovieDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<MovieDetails> call = apiService.getMovieDetails(String.valueOf(id), MainActivity.MYAPI_KEY);
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
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
                binding.loadingDetails.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat halaman detail!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTVDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<TVDetails> call = apiService.getTvDetails(String.valueOf(id), MainActivity.MYAPI_KEY);
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
                    binding.textTitle.setText(response.body().getName());
                    binding.textRunTime.setText("Episode Runtime : " + response.body().getEpisodeRuntime());
                    binding.textReleaseDate.setText("From : " + response.body().getFirstAirDate() + " Until : "
                            + response.body().getLastAirDate());
                    binding.textOverview.setText(response.body().getOverview());
                    binding.textLanguage.setText("Language : " + response.body().getOriginalLanguage());
                    binding.textStatus.setText("Status : " + response.body().getStatus());
                    binding.textBudgetOrSeasons.setText("Seasons : " + response.body().getNumberOfSeasons());
                    binding.textRevenueOrEpisodes.setText("Episodes : " + response.body().getNumberOfEpisodes());

                    binding.textPopularity.setText("Popularity : " + response.body().getPopularity());
                    binding.textTagline.setText("Tagline : " + response.body().getTagline());
                    binding.textVoteCount.setText("Vote Count : " + response.body().getVoteCount());
                    binding.textVoteAverage.setText("Vote Average : " + response.body().getVoteAverage());
                    binding.textHomePage.setText("Homepage : " + response.body().getHomepage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVDetails> call, @NonNull Throwable t) {
                binding.loadingDetails.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat halaman detail!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}