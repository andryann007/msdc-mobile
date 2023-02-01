package com.example.msdc.activities;

import android.annotation.SuppressLint;
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
import com.example.msdc.adapter.CreditCastAdapter;
import com.example.msdc.adapter.CreditCrewAdapter;
import com.example.msdc.adapter.GenreAdapter;
import com.example.msdc.adapter.ImageAdapter;
import com.example.msdc.adapter.ImageListAdapter;
import com.example.msdc.adapter.KeywordAdapter;
import com.example.msdc.adapter.MovieAdapter;
import com.example.msdc.adapter.TVAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.CreditCastResult;
import com.example.msdc.api.CreditCrewResult;
import com.example.msdc.api.CreditResponse;
import com.example.msdc.api.GenreResult;
import com.example.msdc.api.ImageResponse;
import com.example.msdc.api.ImageResult;
import com.example.msdc.api.KeywordResponse;
import com.example.msdc.api.KeywordResult;
import com.example.msdc.api.MovieDetails;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.api.TVDetails;
import com.example.msdc.api.TVResponse;
import com.example.msdc.api.TVResult;
import com.example.msdc.databinding.ActivityDetailBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private GenreAdapter genreAdapter;
    private KeywordAdapter keywordAdapter;
    private ImageListAdapter movieImagesAdapter, tvImagesAdapter;
    private CreditCastAdapter creditCastAdapter;
    private CreditCrewAdapter creditCrewAdapter;

    private final List<GenreResult> movieGenre = new ArrayList<>();
    private final List<KeywordResult> movieKeyword = new ArrayList<>();
    private final List<ImageResult> movieImagesList = new ArrayList<>();
    private final List<MovieResult> movieRecommendationsResult = new ArrayList<>();
    private final List<MovieResult> movieSimilarResult = new ArrayList<>();
    private final List<CreditCastResult> movieCreditCastResult = new ArrayList<>();
    private final List<CreditCrewResult> movieCreditCrewResult = new ArrayList<>();

    private final List<GenreResult> tvGenre = new ArrayList<>();
    private final List<KeywordResult> tvKeyword = new ArrayList<>();
    private final List<ImageResult> tvImagesList = new ArrayList<>();
    private final List<TVResult> tvRecommendationsResult = new ArrayList<>();
    private final List<TVResult> tvSimilarResult = new ArrayList<>();
    private final List<CreditCastResult> tvCreditCastResult = new ArrayList<>();
    private final List<CreditCrewResult> tvCreditCrewResult = new ArrayList<>();

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
                    ImageAdapter.setPosterLogoDetailURL(binding.imagePoster, response.body().getPosterPath());
                    setTitleText(binding.textTitleReleaseDate, response.body().getTitle(),
                            response.body().getReleaseDate());
                    setHtmlText(binding.textRunTime, "Runtime", response.body().getRuntime() + " Minutes");

                    binding.textOverview.setText(response.body().getOverview());

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

                    if(response.body().getVoteAverage() == null){
                        setHtmlEmptyText(binding.textVoteAverage, "Vote Average", "No Vote Average Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteAverage, "Vote Average", response.body().getVoteAverage());
                    }

                    if(response.body().getHomepage().isEmpty()){
                        setHtmlEmptyText(binding.textHomePage, "Homepage", "No Website Homepage Yet!!!");
                    } else{
                        setHtmlLinkText(binding.textHomePage, response.body().getHomepage(), response.body().getHomepage());
                    }

                    setGenresMovie();
                    setKeywordsMovie();
                    setImagesMovie();
                    setMovieCast();
                    setMovieCrew();
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

    private void setGenresMovie(){
        genreAdapter = new GenreAdapter(movieGenre, this);

        getGenresMovie();
        binding.rvGenreList.setAdapter(genreAdapter);
    }

    private void getGenresMovie(){
        Call<MovieDetails> call = apiService.getMovieDetails(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {
                assert response.body() != null;
                if(response.body().getGenres()!=null){
                    int oldCount = movieGenre.size();
                    movieGenre.addAll(response.body().getGenres());
                    genreAdapter.notifyItemChanged(oldCount, movieGenre.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
            }
        });
    }

    private void setKeywordsMovie(){
        keywordAdapter = new KeywordAdapter(movieKeyword, this);

        getKeywordsMovie();
        binding.rvKeywordList.setAdapter(keywordAdapter);
    }

    private void getKeywordsMovie(){
        Call<KeywordResponse> call = apiService.getMovieKeywords(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<KeywordResponse>() {
            @Override
            public void onResponse(@NonNull Call<KeywordResponse> call, @NonNull Response<KeywordResponse> response) {
                assert response.body() != null;
                if(response.body().getKeywords()!=null){
                    int oldCount = movieKeyword.size();
                    movieKeyword.addAll(response.body().getKeywords());
                    keywordAdapter.notifyItemChanged(oldCount, movieKeyword.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<KeywordResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void setImagesMovie(){
        movieImagesAdapter = new ImageListAdapter(movieImagesList, this);

        getImagesMovie();
        binding.rvImagesList.setAdapter(movieImagesAdapter);
    }

    private void getImagesMovie(){
        Call<ImageResponse> call = apiService.getMovieImages(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                assert response.body() != null;
                if(response.body().getResults()!=null){
                    int oldCount = movieImagesList.size();
                    movieImagesList.addAll(response.body().getResults());
                    movieImagesAdapter.notifyItemChanged(oldCount, movieImagesList.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void setMovieCast(){
        creditCastAdapter = new CreditCastAdapter(movieCreditCastResult, this);

        getMovieCast();
        binding.rvCreditCast.setAdapter(creditCastAdapter);
    }

    private void getMovieCast(){
        Call<CreditResponse> call = apiService.getMovieCredit(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<CreditResponse>() {

            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(response.body().getCast()!=null){
                    int oldCount = movieCreditCastResult.size();
                    movieCreditCastResult.addAll(response.body().getCast());
                    creditCastAdapter.notifyItemChanged(oldCount, movieCreditCastResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setMovieCrew(){
        creditCrewAdapter = new CreditCrewAdapter(movieCreditCrewResult, this);

        getMovieCrew();
        binding.rvCreditCrew.setAdapter(creditCrewAdapter);
    }

    private void getMovieCrew(){
        Call<CreditResponse> call = apiService.getMovieCredit(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<CreditResponse>() {

            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(response.body().getCrew()!=null){
                    int oldCount = movieCreditCrewResult.size();
                    movieCreditCrewResult.addAll(response.body().getCrew());
                    creditCrewAdapter.notifyItemChanged(oldCount, movieCreditCrewResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setRecommendationsMovie(){
        movieRecommendationsAdapter = new MovieAdapter(movieRecommendationsResult, this);

        getRecommendationsMovie();
        binding.rvMovieRecommendations.setAdapter(movieRecommendationsAdapter);
    }

    private void getRecommendationsMovie(){
        Call<MovieResponse> call = apiService.getMovieRecommendations(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                assert response.body() != null;
                if(response.body().getResult()!=null){
                    int oldCount = movieRecommendationsResult.size();
                    movieRecommendationsResult.addAll(response.body().getResult());
                    movieRecommendationsAdapter.notifyItemChanged(oldCount, movieRecommendationsResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void setSimilarMovie(){
        movieSimilarAdapter = new MovieAdapter(movieSimilarResult, this);

        getSimilarMovie();
        binding.rvMovieSimilar.setAdapter(movieSimilarAdapter);
    }

    private void getSimilarMovie(){
        Call<MovieResponse> call = apiService.getMovieSimilar(String.valueOf(movie_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                assert response.body() != null;
                if(response.body().getResult()!=null){
                    int oldCount = movieSimilarResult.size();
                    movieSimilarResult.addAll(response.body().getResult());
                    movieSimilarAdapter.notifyItemChanged(oldCount, movieSimilarResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
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
                    setTitleText(binding.textTitleReleaseDate, response.body().getName(),
                            response.body().getFirstAirDate() + " - " + response.body().getLastAirDate());
                    setHtmlText(binding.textRunTime, "Episode Runtime",  Arrays.toString(response.body().getEpisodeRuntime()) + "Episodes");

                    binding.textOverview.setText(response.body().getOverview());

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
                        setHtmlLinkText(binding.textHomePage, response.body().getHomepage(), response.body().getHomepage());
                    }

                    binding.textMovieRecommendations.setText("TV Recommendations");
                    binding.textMovieSimilar.setText("Similar TV");

                    setGenresTV();
                    setKeywordsTV();
                    setImagesTV();
                    setTvCast();
                    setTvCrew();
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

    private void setGenresTV(){
        genreAdapter = new GenreAdapter(tvGenre, this);

        getGenresTV();
        binding.rvGenreList.setAdapter(genreAdapter);
    }

    private void getGenresTV(){
        Call<TVDetails> call = apiService.getTvDetails(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<TVDetails>() {
            @Override
            public void onResponse(@NonNull Call<TVDetails> call, @NonNull Response<TVDetails> response) {
                assert response.body() != null;
                if(response.body().getGenres()!=null){
                    int oldCount = tvGenre.size();
                    tvGenre.addAll(response.body().getGenres());
                    genreAdapter.notifyItemChanged(oldCount, tvGenre.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVDetails> call, @NonNull Throwable t) {
            }
        });
    }

    private void setKeywordsTV(){
        keywordAdapter = new KeywordAdapter(tvKeyword, this);

        getKeywordsTV();
        binding.rvKeywordList.setAdapter(keywordAdapter);
    }

    private void getKeywordsTV(){
        Call<KeywordResponse> call = apiService.getTvKeywords(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<KeywordResponse>() {
            @Override
            public void onResponse(@NonNull Call<KeywordResponse> call, @NonNull Response<KeywordResponse> response) {
                assert response.body() != null;
                if(response.body().getKeywords()!=null){
                    int oldCount = tvKeyword.size();
                    tvKeyword.addAll(response.body().getKeywords());
                    keywordAdapter.notifyItemChanged(oldCount, tvKeyword.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<KeywordResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void setImagesTV(){
        tvImagesAdapter = new ImageListAdapter(tvImagesList, this);

        getImagesTV();
        binding.rvImagesList.setAdapter(tvImagesAdapter);
    }

    private void getImagesTV(){
        Call<ImageResponse> call = apiService.getTvImages(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                assert response.body() != null;
                if(response.body().getResults()!=null){
                    int oldCount = tvImagesList.size();
                    tvImagesList.addAll(response.body().getResults());
                    tvImagesAdapter.notifyItemChanged(oldCount, tvImagesList.size());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void setTvCast(){
        creditCastAdapter = new CreditCastAdapter(tvCreditCastResult, this);

        getTvCast();
        binding.rvCreditCast.setAdapter(creditCastAdapter);
    }

    private void getTvCast(){
        Call<CreditResponse> call = apiService.getTvCredit(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(response.body().getCast()!=null){
                    int oldCount = tvCreditCastResult.size();
                    tvCreditCastResult.addAll(response.body().getCast());
                    creditCastAdapter.notifyItemChanged(oldCount, tvCreditCastResult.size());
                }
            }
            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void setTvCrew(){
        creditCrewAdapter = new CreditCrewAdapter(tvCreditCrewResult, this);

        getTvCrew();
        binding.rvCreditCrew.setAdapter(creditCrewAdapter);
    }

    private void getTvCrew(){
        Call<CreditResponse> call = apiService.getTvCredit(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(response.body().getCrew()!=null){
                    int oldCount = tvCreditCrewResult.size();
                    tvCreditCrewResult.addAll(response.body().getCrew());
                    creditCrewAdapter.notifyItemChanged(oldCount, tvCreditCrewResult.size());
                }
            }
            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setRecommendationsTV(){
        tvRecommendationsAdapter = new TVAdapter(tvRecommendationsResult, this);

        getRecommendationsTV();
        binding.rvMovieRecommendations.setAdapter(tvRecommendationsAdapter);
    }

    private void getRecommendationsTV(){
        Call<TVResponse> call = apiService.getTVRecommendations(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                assert response.body() != null;
                if(response.body().getResult()!=null){
                    int oldCount = tvRecommendationsResult.size();
                    tvRecommendationsResult.addAll(response.body().getResult());
                    tvRecommendationsAdapter.notifyItemChanged(oldCount, tvRecommendationsResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void setSimilarTV(){
        tvSimilarAdapter = new TVAdapter(tvSimilarResult, this);

        getSimilarTV();
        binding.rvMovieSimilar.setAdapter(tvSimilarAdapter);
    }

    private void getSimilarTV(){
        Call<TVResponse> call = apiService.getTVSimilar(String.valueOf(tv_id), MainActivity.MYAPI_KEY);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                assert response.body() != null;
                if(response.body().getResult()!=null){
                    int oldCount = tvSimilarResult.size();
                    tvSimilarResult.addAll(response.body().getResult());
                    tvSimilarAdapter.notifyItemChanged(oldCount, tvSimilarResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
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

    private void setHtmlLinkText(TextView tv, String textLink, String textValue){
        tv.setText(HtmlCompat.fromHtml("<font color='#059142'>" + "Homepage" + "</font> : " +
                "<b> <a href='" + textLink + "'>" + textValue +"</a> </b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setTitleText(TextView tv, String textTitle, String textReleaseData){
        tv.setText(HtmlCompat.fromHtml("<b>" + textTitle + "</b><font color='#808080'> ("
                + textReleaseData + ") </font>", HtmlCompat.FROM_HTML_MODE_LEGACY));
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