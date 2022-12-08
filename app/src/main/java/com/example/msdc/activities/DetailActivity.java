package com.example.msdc.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import com.example.msdc.R;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        binding.toolbar.setOnClickListener(v-> onBackPressed());
    }

    private void setMovieDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<MovieDetails> call = apiService.getMovieDetails(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<MovieDetails>(){

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {
                if(response.body() != null){
                    Objects.requireNonNull(getSupportActionBar()).setTitle(response.body().getTitle());
                    binding.loadingDetails.setVisibility(View.GONE);
                    binding.scrollView.setVisibility(View.VISIBLE);
                    binding.imagePosterBack.setVisibility(View.VISIBLE);

                    ImageAdapter.setPosterURL(binding.imagePosterBack, response.body().getPosterPath());
                    ImageAdapter.setBackdropURL(binding.imageBackdrop, response.body().getBackdropPath());
                    ImageAdapter.setPosterURL(binding.imagePoster, response.body().getPosterPath());
                    setHtmlText(binding.textTitle, "Movie Title", response.body().getTitle());
                    setHtmlText(binding.textRunTime, "Runtime", response.body().getRuntime() + " Minutes");
                    setHtmlText(binding.textReleaseDate, "Released on", response.body().getReleaseDate());
                    setHtmlText(binding.textOverview, "Overview", response.body().getOverview());
                    setHtmlText(binding.textLanguage, "Language", response.body().getLanguage());
                    setHtmlText(binding.textStatus, "Status", response.body().getStatus());

                    Double budget = Double.parseDouble(response.body().getBudget());
                    String budgetFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(budget);
                    setHtmlText(binding.textBudgetOrSeasons, "Budget", budgetFormatted);

                    Double revenue = Double.parseDouble(response.body().getRevenue());
                    String revenueFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(revenue);
                    setHtmlText(binding.textRevenueOrEpisodes, "Revenue", revenueFormatted);

                    setHtmlText(binding.textPopularity, "Popularity", response.body().getPopularity());

                    if(response.body().getTagline().isEmpty()){
                        setHtmlEmptyText(binding.textTagline, "Tagline", "No Tagline Yet!!!");
                    } else{
                        setHtmlText(binding.textTagline, "Tagline", response.body().getTagline());
                    }

                    if(response.body().getVoteCount().isEmpty()){
                        setHtmlEmptyText(binding.textVoteCount, "Vote Count", "No Vote Count Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteCount, "Vote Count", response.body().getVoteCount());
                    }

                    if(response.body().getVoteAverage().isEmpty()){
                        setHtmlEmptyText(binding.textVoteAverage, "Vote Average", "No Vote Average Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteAverage, "Vote Average", response.body().getVoteAverage());
                    }

                    if(response.body().getHomepage().isEmpty()){
                        setHtmlEmptyText(binding.textHomePage, "Homepage", "No Website Homepage Yet!!!");
                    } else{
                        setHtmlLinkText(binding.textHomePage, "Homepage", response.body().getHomepage(), response.body().getHomepage());
                    }

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
                    Objects.requireNonNull(getSupportActionBar()).setTitle(response.body().getName());
                    binding.loadingDetails.setVisibility(View.GONE);
                    binding.scrollView.setVisibility(View.VISIBLE);
                    binding.imagePosterBack.setVisibility(View.VISIBLE);

                    ImageAdapter.setPosterURL(binding.imagePosterBack, response.body().getPosterPath());
                    ImageAdapter.setBackdropURL(binding.imageBackdrop, response.body().getBackdropPath());
                    ImageAdapter.setPosterURL(binding.imagePoster, response.body().getPosterPath());
                    setHtmlText(binding.textTitle, "Name", response.body().getName());
                    setHtmlText(binding.textRunTime, "Episode Runtime",  Arrays.toString(response.body().getEpisodeRuntime()) + "Episodes");
                    setHtmlText(binding.textReleaseDate, "From", response.body().getFirstAirDate() + " - " + response.body().getLastAirDate());
                    setHtmlText(binding.textOverview,"Overview", response.body().getOverview());
                    setHtmlText(binding.textLanguage, "Language", response.body().getOriginalLanguage());
                    setHtmlText(binding.textStatus, "Status", response.body().getStatus());
                    setHtmlText(binding.textBudgetOrSeasons, "Number of Seasons", String.valueOf(response.body().getNumberOfSeasons()));
                    setHtmlText(binding.textRevenueOrEpisodes, "Number of Episodes", String.valueOf(response.body().getNumberOfEpisodes()));

                    setHtmlText(binding.textPopularity, "Popularity", response.body().getPopularity());

                    if(response.body().getTagline().isEmpty()){
                        setHtmlEmptyText(binding.textTagline, "Tagline", "No Tagline Yet!!!");
                    } else{
                        setHtmlText(binding.textTagline, "Tagline", response.body().getTagline());
                    }

                    if(response.body().getVoteCount().isEmpty()){
                        setHtmlEmptyText(binding.textVoteCount, "Vote Count", "No Vote Count Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteCount, "Vote Count", response.body().getVoteCount());
                    }

                    if(response.body().getVoteAverage().isEmpty()){
                        setHtmlEmptyText(binding.textVoteAverage, "Vote Average", "No Vote Average Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteAverage, "Vote Average", response.body().getVoteAverage());
                    }

                    if(response.body().getHomepage().isEmpty()){
                        setHtmlEmptyText(binding.textHomePage, "Homepage", "No Website Homepage Yet!!!");
                    } else{
                        setHtmlLinkText(binding.textHomePage, "Homepage", response.body().getHomepage(), response.body().getHomepage());
                    }

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
    private void setHtmlText(TextView tv, String textColored, String textValue){
        tv.setText(HtmlCompat.fromHtml("<font color='#059142'>" + textColored + "</font> : " +
                "<b>" + textValue + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setHtmlEmptyText(TextView tv, String textColored, String textValue){
        tv.setText(HtmlCompat.fromHtml("<font color='#059142'>" + textColored + "</font> : " +
                "<b> <font color='#FF0000'>" + textValue + "</font> </b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setHtmlLinkText(TextView tv, String textColored, String textLink, String textValue){
        tv.setText(HtmlCompat.fromHtml("<font color='#059142'>" + textColored + "</font> : " +
                "<b> <a href='" + textLink + "'>" + textValue +"</a> </b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId() == R.id.nav_favorite) {
            item.setIcon(R.drawable.ic_favorite);
            Toast.makeText(DetailActivity.this, "Favorite Added...", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}