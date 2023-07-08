package com.example.msdc.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.example.msdc.adapter.SeasonAdapter;
import com.example.msdc.adapter.TVAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.CreditCastResult;
import com.example.msdc.api.CreditCrewResult;
import com.example.msdc.api.CreditResponse;
import com.example.msdc.api.EpisodeResult;
import com.example.msdc.api.GenreResult;
import com.example.msdc.api.ImageResponse;
import com.example.msdc.api.ImageResult;
import com.example.msdc.api.KeywordResponse;
import com.example.msdc.api.KeywordResult;
import com.example.msdc.api.MovieDetails;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.api.SeasonResponse;
import com.example.msdc.api.TVDetails;
import com.example.msdc.api.TVResponse;
import com.example.msdc.api.TVResult;
import com.example.msdc.databinding.ActivityDetailBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private ApiService apiService;
    private int movie_id;
    private int series_id;
    private ActivityDetailBinding binding;
    private MovieAdapter movieRecommendationsAdapter, movieSimilarAdapter;
    private TVAdapter tvRecommendationsAdapter, tvSimilarAdapter;
    private GenreAdapter genreAdapter;
    private KeywordAdapter keywordAdapter;
    private ImageListAdapter movieImagesAdapter, tvImagesAdapter;
    private CreditCastAdapter creditCastAdapter;
    private CreditCrewAdapter creditCrewAdapter;
    private SeasonAdapter tvSeasonAdapter;

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
    private final ArrayList<EpisodeResult> episodeResults = new ArrayList<>();

    private String searchType = null;
    private String filterType = null;
    private String genre = null;
    private String year = null;
    private String region = null;
    private String sortBy = null;
    private int season = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        String type = getIntent().getStringExtra("type");
        if(Objects.equals(type, "movie")){
            movie_id = getIntent().getIntExtra("movie_id", 0);
            setMovieDetails();

        } else if(Objects.equals(type, "tv")){
            series_id = getIntent().getIntExtra("series_id", 0);
            setTVDetails();
        }

        binding.toolbar.setOnClickListener(v-> onBackPressed());
    }

    private void setMovieDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<MovieDetails> call = apiService.getMovieDetails(movie_id, MainActivity.MY_API_KEY);
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
                    ImageAdapter.setPosterLogoURL(binding.imagePoster, response.body().getPosterPath());
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

                    float popularity = Float.parseFloat(response.body().getPopularity());
                    String mPopularity = String.format(Locale.US, "%.2f", popularity);
                    setHtmlText(binding.textPopularity, "Popularity", mPopularity);

                    if(response.body().getTagline().isEmpty()){
                        setHtmlEmptyText(binding.textTagline, "Tagline", "No Tagline Yet!!!");
                    } else{
                        setHtmlText(binding.textTagline, "Tagline", response.body().getTagline());
                    }

                    int voteCount = Integer.parseInt(response.body().getVoteCount());
                    String mVoteCount = String.format(Locale.US, "%,d", voteCount).replace(",",".");
                    if(voteCount == 0){
                        setHtmlEmptyText(binding.textVoteCount, "Vote Count", "No Vote Count Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteCount, "Vote Count", mVoteCount);
                    }

                    float voteAverage = Float.parseFloat(response.body().getVoteAverage());
                    String mVoteAverage = String.format(Locale.US, "%.2f", voteAverage);
                    if(voteAverage == 0){
                        setHtmlEmptyText(binding.textVoteAverage, "Vote Average", "No Vote Average Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteAverage, "Vote Average", mVoteAverage);
                    }

                    if(response.body().getHomepage().isEmpty()){
                        setHtmlEmptyText(binding.textHomePage, "Homepage", "No Website Homepage Yet!!!");
                    } else{
                        setHtmlLinkText(binding.textHomePage, response.body().getHomepage(), response.body().getHomepage());
                    }
                    getKeywordsMovie();
                    getImagesMovie();
                    getMovieCast();
                    getMovieCrew();
                    getRecommendationsMovie();
                    getSimilarMovie();

                    String movieGenre = String.valueOf(response.body().getGenres());
                    if(!movieGenre.isEmpty()){
                        getGenresMovie();
                    } else {
                        binding.textGenreList.setVisibility(View.GONE);
                        binding.rvGenreList.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
                binding.loadingDetails.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Fail to Fetch Detail Data !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dialogSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_search, null);
        EditText inputSearch = v.findViewById(R.id.inputSearch);
        Button btnSearch = v.findViewById(R.id.btnSearch);
        RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
        RadioButton radioButtonMovie = v.findViewById(R.id.radioButtonMovie);
        RadioButton radioButtonTV = v.findViewById(R.id.radioButtonTV);

        builder.setView(v);
        AlertDialog dialogSearch = builder.create();
        if(dialogSearch.getWindow() != null){
            dialogSearch.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioButtonMovie){
                    searchType = radioButtonMovie.getText().toString();
                } else {
                    searchType = radioButtonTV.getText().toString();
                }
            });
            btnSearch.setOnClickListener(view -> doSearch(inputSearch.getText().toString()));

            inputSearch.setOnEditorActionListener((v1, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    doSearch(inputSearch.getText().toString());
                }
                return false;
            });
            dialogSearch.show();
        }
    }

    private void doSearch(String query) {
        if(query.isEmpty()){
            Toast.makeText(getApplicationContext(),"No Input !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(searchType == null){
            Toast.makeText(getApplicationContext(),"No Search Type !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(DetailActivity.this, SearchActivity.class);
        i.putExtra("type", searchType);
        i.putExtra("searchFor", query);
        startActivity(i);
    }

    private void dialogFilter(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_filter, null);

        Button btnFilter = v.findViewById(R.id.btnFilter);
        RadioGroup radioGroup = v.findViewById(R.id.radioGroup);

        builder.setView(v);
        AlertDialog dialogFilter = builder.create();
        if(dialogFilter.getWindow() != null){
            dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            Spinner spinnerFilterGenre = v.findViewById(R.id.spinnerFilterGenre);

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioButtonMovie){
                    filterType = "movie";
                    ArrayAdapter<String> filterMovieGenreAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.movieGenreList));

                    filterMovieGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFilterGenre.setAdapter(filterMovieGenreAdapter);
                } else if(checkedId == R.id.radioButtonTV){
                    filterType = "tv";
                    ArrayAdapter<String> filterTvGenreAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.tvGenreList));

                    filterTvGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFilterGenre.setAdapter(filterTvGenreAdapter);
                } else {
                    filterType = "movie";
                    ArrayAdapter<String> filterMovieGenreAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.movieGenreList));

                    filterMovieGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFilterGenre.setAdapter(filterMovieGenreAdapter);
                }
            });

            Spinner spinnerFilterYear = v.findViewById(R.id.spinnerFilterYear);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            List<String> years = new ArrayList<>();
            for(int i=1980; i<=currentYear; i++){
                years.add(Integer.toString(i));
            }
            ArrayAdapter<String> filterYearAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, years);
            filterYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFilterYear.setAdapter(filterYearAdapter);

            Spinner spinnerFilterRegion = v.findViewById(R.id.spinnerFilterRegion);
            ArrayAdapter<String> filterRegionAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.regionList));
            filterRegionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFilterRegion.setAdapter(filterRegionAdapter);

            Spinner spinnerSortBy = v.findViewById(R.id.spinnerSortBy);
            ArrayAdapter<String> sortByAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sortList));
            sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSortBy.setAdapter(sortByAdapter);

            spinnerFilterGenre.setOnItemSelectedListener(this);
            spinnerFilterRegion.setOnItemSelectedListener(this);
            spinnerFilterYear.setOnItemSelectedListener(this);
            spinnerSortBy.setOnItemSelectedListener(this);

            btnFilter.setOnClickListener(view -> doFilter(filterType, genre, year, region, sortBy));
            dialogFilter.show();
        }
    }

    private void doFilter(String filterType, String genre, String year, String region, String sortBy) {
        Intent i = new Intent(DetailActivity.this, FilterActivity.class);

        switch(filterType){
            case "movie" :
                i.putExtra("type", "movie");
                i.putExtra("genre", genre);
                i.putExtra("year", year);
                i.putExtra("region", region);
                i.putExtra("sortBy", sortBy);
                startActivity(i);
                break;

            case "tv" :
                i.putExtra("type", "tv");
                i.putExtra("genre", genre);
                i.putExtra("year", year);
                i.putExtra("region", region);
                i.putExtra("sortBy", sortBy);
                startActivity(i);
                break;
        }
    }


    private void getGenresMovie(){
        genreAdapter = new GenreAdapter(movieGenre);

        Call<MovieDetails> call = apiService.getMovieDetails(movie_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {
                assert response.body() != null;
                if(!response.body().getGenres().isEmpty()){
                    binding.textGenreList.setVisibility(View.VISIBLE);
                    binding.rvGenreList.setVisibility(View.VISIBLE);

                    int oldCount = movieGenre.size();
                    movieGenre.addAll(response.body().getGenres());
                    genreAdapter.notifyItemRangeInserted(oldCount, movieGenre.size());
                } else {
                    setNoText(binding.textGenreList, "No Genres Yet !!!");
                    binding.textGenreList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Genres List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvGenreList.setAdapter(genreAdapter);
    }

    private void getKeywordsMovie(){
        keywordAdapter = new KeywordAdapter(movieKeyword);

        Call<KeywordResponse> call = apiService.getMovieKeywords(movie_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<KeywordResponse>() {
            @Override
            public void onResponse(@NonNull Call<KeywordResponse> call, @NonNull Response<KeywordResponse> response) {
                assert response.body() != null;
                if(!response.body().getKeywords().isEmpty()){
                    binding.textKeywordList.setVisibility(View.VISIBLE);
                    binding.rvKeywordList.setVisibility(View.VISIBLE);

                    int oldCount = movieKeyword.size();
                    movieKeyword.addAll(response.body().getKeywords());
                    keywordAdapter.notifyItemRangeInserted(oldCount, movieKeyword.size());
                } else {
                    setNoText(binding.textKeywordList, "No Keywords Yet !!!");
                    binding.textKeywordList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<KeywordResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Keywords List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvKeywordList.setAdapter(keywordAdapter);
    }

    private void getImagesMovie(){
        movieImagesAdapter = new ImageListAdapter(movieImagesList);

        Call<ImageResponse> call = apiService.getMovieImages(movie_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                assert response.body() != null;
                if(!response.body().getResults().isEmpty()){
                    binding.textImageList.setVisibility(View.VISIBLE);
                    binding.rvImagesList.setVisibility(View.VISIBLE);

                    int oldCount = movieImagesList.size();
                    movieImagesList.addAll(response.body().getResults());
                    movieImagesAdapter.notifyItemRangeInserted(oldCount, movieImagesList.size());
                } else {
                    setNoText(binding.textImageList, "No Movie Images Yet !!!");
                    binding.textImageList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Images List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvImagesList.setAdapter(movieImagesAdapter);
    }

    private void getMovieCast(){
        creditCastAdapter = new CreditCastAdapter(movieCreditCastResult);

        Call<CreditResponse> call = apiService.getMovieCredit(movie_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<CreditResponse>() {

            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(!response.body().getCast().isEmpty()){
                    binding.textMovieCreditCast.setVisibility(View.VISIBLE);
                    binding.rvCreditCast.setVisibility(View.VISIBLE);

                    int oldCount = movieCreditCastResult.size();
                    movieCreditCastResult.addAll(response.body().getCast());
                    creditCastAdapter.notifyItemRangeInserted(oldCount, movieCreditCastResult.size());
                } else {
                    setNoText(binding.textMovieCreditCast, "No Movie Credit Cast Yet !!!");
                    binding.textMovieCreditCast.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Credit Cast List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvCreditCast.setAdapter(creditCastAdapter);
    }

    private void getMovieCrew(){
        creditCrewAdapter = new CreditCrewAdapter(movieCreditCrewResult);

        Call<CreditResponse> call = apiService.getMovieCredit(movie_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<CreditResponse>() {

            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(!response.body().getCrew().isEmpty()){
                    binding.textMovieCreditCrew.setVisibility(View.VISIBLE);
                    binding.rvCreditCrew.setVisibility(View.VISIBLE);

                    int oldCount = movieCreditCrewResult.size();
                    movieCreditCrewResult.addAll(response.body().getCrew());
                    creditCrewAdapter.notifyItemRangeInserted(oldCount, movieCreditCrewResult.size());
                } else {
                    setNoText(binding.textMovieCreditCrew, "No Movie Credit Crew Yet !!!");
                    binding.textMovieCreditCrew.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Credit Crew List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvCreditCrew.setAdapter(creditCrewAdapter);
    }

    private void getRecommendationsMovie(){
        movieRecommendationsAdapter = new MovieAdapter(movieRecommendationsResult, this);

        Call<MovieResponse> call = apiService.getMovieRecommendations(movie_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                assert response.body() != null;
                if(!response.body().getResult().isEmpty()){
                    binding.textMovieRecommendations.setVisibility(View.VISIBLE);
                    binding.rvMovieRecommendations.setVisibility(View.VISIBLE);

                    int oldCount = movieRecommendationsResult.size();
                    movieRecommendationsResult.addAll(response.body().getResult());
                    movieRecommendationsAdapter.notifyItemRangeInserted(oldCount, movieRecommendationsResult.size());
                } else {
                    setNoText(binding.textMovieRecommendations, "No Movie Recommendations Yet !!!");
                    binding.textMovieRecommendations.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Recommendations Movies List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvMovieRecommendations.setAdapter(movieRecommendationsAdapter);
    }

    private void getSimilarMovie(){
        movieSimilarAdapter = new MovieAdapter(movieSimilarResult, this);

        Call<MovieResponse> call = apiService.getMovieSimilar(movie_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                assert response.body() != null;
                if(!response.body().getResult().isEmpty()){
                    binding.textMovieSimilar.setVisibility(View.VISIBLE);
                    binding.rvMovieSimilar.setVisibility(View.VISIBLE);

                    int oldCount = movieSimilarResult.size();
                    movieSimilarResult.addAll(response.body().getResult());
                    movieSimilarAdapter.notifyItemRangeInserted(oldCount, movieSimilarResult.size());
                } else {
                    setNoText(binding.textMovieSimilar, "No Similar Movie Yet !!");
                    binding.textMovieSimilar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Similar Movies List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvMovieSimilar.setAdapter(movieSimilarAdapter);
    }

    private void setTVDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<TVDetails> call = apiService.getTvDetails(series_id, MainActivity.MY_API_KEY);
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
                    ImageAdapter.setPosterLogoURL(binding.imagePoster, response.body().getPosterPath());
                    setTitleText(binding.textTitleReleaseDate, response.body().getName(),
                            response.body().getFirstAirDate() + " - " + response.body().getLastAirDate());

                    String mEpisodeRunTime = Arrays.toString(response.body().getEpisodeRuntime()).replace("[","").replace("]","");
                    setHtmlText(binding.textRunTime, "Episode Runtime", mEpisodeRunTime + " Episodes");

                    binding.textOverview.setText(response.body().getOverview());

                    setHtmlText(binding.textLanguage, "Language", response.body().getOriginalLanguage());
                    setHtmlText(binding.textStatus, "Status", response.body().getStatus());
                    setHtmlText(binding.textBudgetOrSeasons, "Number of Seasons", String.valueOf(response.body().getNumberOfSeasons()));
                    setHtmlText(binding.textRevenueOrEpisodes, "Number of Episodes", String.valueOf(response.body().getNumberOfEpisodes()));

                    float popularity = Float.parseFloat(response.body().getPopularity());
                    String mPopularity = String.format(Locale.US, "%.2f", popularity);
                    setHtmlText(binding.textPopularity, "Popularity", mPopularity);

                    if(response.body().getTagline().isEmpty()){
                        setHtmlEmptyText(binding.textTagline, "Tagline", "No Tagline Yet!!!");
                    } else{
                        setHtmlText(binding.textTagline, "Tagline", response.body().getTagline());
                    }

                    int voteCount = Integer.parseInt(response.body().getVoteCount());
                    String mVoteCount = String.format(Locale.US, "%,d", voteCount).replace(",",".");
                    if(voteCount == 0){
                        setHtmlEmptyText(binding.textVoteCount, "Vote Count", "No Vote Count Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteCount, "Vote Count", mVoteCount);
                    }

                    float voteAverage = Float.parseFloat(response.body().getVoteAverage());
                    String mVoteAverage = String.format(Locale.US, "%.2f", voteAverage);
                    if(voteAverage == 0){
                        setHtmlEmptyText(binding.textVoteAverage, "Vote Average", "No Vote Average Yet!!!");
                    } else{
                        setHtmlText(binding.textVoteAverage, "Vote Average", mVoteAverage);
                    }

                    if(response.body().getHomepage().isEmpty()){
                        setHtmlEmptyText(binding.textHomePage, "Homepage", "No Website Homepage Yet!!!");
                    } else{
                        setHtmlLinkText(binding.textHomePage, response.body().getHomepage(), response.body().getHomepage());
                    }
                    getKeywordsTV();
                    getImagesTV();
                    getTvCast();
                    getTvCrew();
                    getRecommendationsTV();
                    getSimilarTV();

                    String tvGenre = String.valueOf(response.body().getGenres());
                    if(!tvGenre.isEmpty()){
                        getGenresTV();
                    } else {
                        binding.textGenreList.setVisibility(View.GONE);
                        binding.rvGenreList.setVisibility(View.GONE);
                    }

                    int numberOfSeasons = response.body().getNumberOfSeasons();
                    if(numberOfSeasons == 1){
                        getTVEpisodes(numberOfSeasons);
                    } else if(numberOfSeasons > 1){
                        for(season = 1; season <= numberOfSeasons; season++){
                            getTVEpisodes(season);
                        }
                    } else {
                        binding.textTvSeasonAndEpisodeList.setVisibility(View.GONE);
                        binding.rvTvSeasonAndEpisodeList.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVDetails> call, @NonNull Throwable t) {
                binding.loadingDetails.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Fail to Fetch Detail Data !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGenresTV(){
        genreAdapter = new GenreAdapter(tvGenre);

        Call<TVDetails> call = apiService.getTvDetails(series_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<TVDetails>() {
            @Override
            public void onResponse(@NonNull Call<TVDetails> call, @NonNull Response<TVDetails> response) {
                assert response.body() != null;
                if(!response.body().getGenres().isEmpty()){
                    binding.textGenreList.setVisibility(View.VISIBLE);
                    binding.rvGenreList.setVisibility(View.VISIBLE);

                    int oldCount = tvGenre.size();
                    tvGenre.addAll(response.body().getGenres());
                    genreAdapter.notifyItemRangeInserted(oldCount, tvGenre.size());
                } else {
                    setNoText(binding.textGenreList, "No Genres Yet !!!");
                    binding.textGenreList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVDetails> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Genres List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvGenreList.setAdapter(genreAdapter);
    }

    private void getKeywordsTV(){
        keywordAdapter = new KeywordAdapter(tvKeyword);

        Call<KeywordResponse> call = apiService.getTvKeywords(series_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<KeywordResponse>() {
            @Override
            public void onResponse(@NonNull Call<KeywordResponse> call, @NonNull Response<KeywordResponse> response) {
                assert response.body() != null;
                if(!response.body().getKeywords().isEmpty()){
                    binding.textKeywordList.setVisibility(View.VISIBLE);
                    binding.rvKeywordList.setVisibility(View.VISIBLE);

                    int oldCount = tvKeyword.size();
                    tvKeyword.addAll(response.body().getKeywords());
                    keywordAdapter.notifyItemRangeInserted(oldCount, tvKeyword.size());
                } else {
                    setNoText(binding.textKeywordList, "No Keywords Yet !!!");
                    binding.textKeywordList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<KeywordResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Keywords List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvKeywordList.setAdapter(keywordAdapter);
    }

    private void getImagesTV(){
        tvImagesAdapter = new ImageListAdapter(tvImagesList);

        Call<ImageResponse> call = apiService.getTvImages(series_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                assert response.body() != null;
                if(!response.body().getResults().isEmpty()){
                    binding.textImageList.setVisibility(View.VISIBLE);
                    binding.rvImagesList.setVisibility(View.VISIBLE);

                    int oldCount = tvImagesList.size();
                    tvImagesList.addAll(response.body().getResults());
                    tvImagesAdapter.notifyItemRangeInserted(oldCount, tvImagesList.size());
                } else {
                    setNoText(binding.textImageList, "No TV Shows Images Yet !!!");
                    binding.textImageList.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Images List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvImagesList.setAdapter(tvImagesAdapter);
    }

    private void getTvCast(){
        creditCastAdapter = new CreditCastAdapter(tvCreditCastResult);

        Call<CreditResponse> call = apiService.getTvCredit(series_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(!response.body().getCast().isEmpty()){
                    setNoteText(binding.textMovieCreditCast, "TV Shows Credit Cast");
                    binding.textMovieCreditCast.setVisibility(View.VISIBLE);
                    binding.rvCreditCast.setVisibility(View.VISIBLE);

                    int oldCount = tvCreditCastResult.size();
                    tvCreditCastResult.addAll(response.body().getCast());
                    creditCastAdapter.notifyItemRangeInserted(oldCount, tvCreditCastResult.size());
                } else {
                    setNoText(binding.textMovieCreditCast, "No TV Shows Credit Cast Yet !!!");
                    binding.textMovieCreditCast.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Credit Cast List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvCreditCast.setAdapter(creditCastAdapter);
    }

    private void getTvCrew(){
        creditCrewAdapter = new CreditCrewAdapter(tvCreditCrewResult);

        Call<CreditResponse> call = apiService.getTvCredit(series_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(!response.body().getCrew().isEmpty()){
                    setNoteText(binding.textMovieCreditCrew, "TV Shows Credit Crew");
                    binding.textMovieCreditCrew.setVisibility(View.VISIBLE);
                    binding.rvCreditCrew.setVisibility(View.VISIBLE);

                    int oldCount = tvCreditCrewResult.size();
                    tvCreditCrewResult.addAll(response.body().getCrew());
                    creditCrewAdapter.notifyItemRangeInserted(oldCount, tvCreditCrewResult.size());
                } else {
                    setNoText(binding.textMovieCreditCrew, "No TV Shows Credit Crew Yet !!!");
                    binding.textMovieCreditCrew.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Credit Crew List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvCreditCrew.setAdapter(creditCrewAdapter);
    }

    private void getRecommendationsTV(){
        tvRecommendationsAdapter = new TVAdapter(tvRecommendationsResult, this);

        Call<TVResponse> call = apiService.getTVRecommendations(series_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                assert response.body() != null;
                if(!response.body().getResult().isEmpty()){
                    setNoteText(binding.textMovieRecommendations, "TV Shows Recommendations");
                    binding.textMovieRecommendations.setVisibility(View.VISIBLE);
                    binding.rvMovieRecommendations.setVisibility(View.VISIBLE);

                    int oldCount = tvRecommendationsResult.size();
                    tvRecommendationsResult.addAll(response.body().getResult());
                    tvRecommendationsAdapter.notifyItemRangeInserted(oldCount, tvRecommendationsResult.size());
                } else {
                    setNoText(binding.textMovieRecommendations, "No Recommendations TV Shows Yet !!!");
                    binding.textMovieRecommendations.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Recommendation TV Shows List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvMovieRecommendations.setAdapter(tvRecommendationsAdapter);
    }

    private void getSimilarTV(){
        tvSimilarAdapter = new TVAdapter(tvSimilarResult, this);

        Call<TVResponse> call = apiService.getTVSimilar(series_id, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                assert response.body() != null;
                if(!response.body().getResult().isEmpty()){
                    setNoteText(binding.textMovieSimilar, "Similar TV Shows");
                    binding.textMovieSimilar.setVisibility(View.VISIBLE);
                    binding.rvMovieSimilar.setVisibility(View.VISIBLE);

                    int oldCount = tvSimilarResult.size();
                    tvSimilarResult.addAll(response.body().getResult());
                    tvSimilarAdapter.notifyItemRangeInserted(oldCount, tvSimilarResult.size());
                } else {
                    setNoText(binding.textMovieSimilar, "No Similar TV Shows Yet !!!");
                    binding.textMovieSimilar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Similar TV Shows List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvMovieSimilar.setAdapter(tvSimilarAdapter);
    }

    private void getTVEpisodes(int season){
        tvSeasonAdapter = new SeasonAdapter(episodeResults);

        Call<SeasonResponse> call = apiService.getTvSeasonAndEpisode(series_id, season, MainActivity.MY_API_KEY);
        call.enqueue(new Callback<SeasonResponse>() {
            @Override
            public void onResponse(@NonNull Call<SeasonResponse> call, @NonNull Response<SeasonResponse> response) {
                if(response.body()!=null) {
                    binding.textTvSeasonAndEpisodeList.setVisibility(View.VISIBLE);
                    binding.rvTvSeasonAndEpisodeList.setVisibility(View.VISIBLE);

                    int oldCount = episodeResults.size();
                    episodeResults.addAll(response.body().getEpisodeResults());
                    tvSeasonAdapter.notifyItemRangeInserted(oldCount, episodeResults.size());
                } else {
                    setNoText(binding.textTvSeasonAndEpisodeList, "No Episodes List Yet !!!");
                    binding.textTvSeasonAndEpisodeList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SeasonResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Episodes List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvTvSeasonAndEpisodeList.setAdapter(tvSeasonAdapter);
    }

    private void setHtmlText(TextView tv, String textColored, String textValue){
        tv.setText(HtmlCompat.fromHtml("<font color='#FFFFFF'>" + textColored + "</font> : " +
                "<b>" + textValue + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setHtmlEmptyText(TextView tv, String textColored, String textValue){
        tv.setText(HtmlCompat.fromHtml("<font color='#FFFFFF'>" + textColored + "</font> : " +
                "<b> <font color='#FF0000'>" + textValue + "</font> </b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setHtmlLinkText(TextView tv, String textLink, String textValue){
        tv.setText(HtmlCompat.fromHtml("<font color='#FFFFFF'>" + "Homepage" + "</font> : " +
                "<b> <a href='" + textLink + "'>" + textValue +"</a> </b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setTitleText(TextView tv, String textTitle, String textReleaseData){
        tv.setText(HtmlCompat.fromHtml("<b>" + textTitle + "</b><font color='#FFFFFF'> ("
                + textReleaseData + ") </font>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setNoText(TextView tv, String note){
        tv.setText(HtmlCompat.fromHtml("<font color='#FF2400'><b>" + note + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setNoteText(TextView tv, String note){
        tv.setText(HtmlCompat.fromHtml("<b>" + note + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.nav_search) {
            dialogSearch();
        } else if(item.getItemId() == R.id.nav_filter){
            dialogFilter();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String genreSelected = parent.getItemAtPosition(position).toString().toLowerCase();
        String yearSelected = parent.getItemAtPosition(position).toString();
        String regionSelected = parent.getItemAtPosition(position).toString().toLowerCase();
        String sortSelected = parent.getItemAtPosition(position).toString().toLowerCase();

        switch(genreSelected){
            case "action" :
                genre = "28";
                break;

            case "adventure" :
                genre = "12";
                break;

            case "animation" :
                genre = "16";
                break;

            case "comedy" :
                genre = "35";
                break;

            case "crime" :
                genre = "80";
                break;

            case "documentary" :
                genre = "99";
                break;

            case "drama" :
                genre = "18";
                break;

            case "family" :
                genre = "10751";
                break;

            case "fantasy" :
                genre = "14";
                break;

            case "history" :
                genre = "36";
                break;

            case "horror" :
                genre = "27";
                break;

            case "music" :
                genre = "10402";
                break;

            case "mystery" :
                genre = "9648";
                break;

            case "romance" :
                genre = "10749";
                break;

            case "science fiction" :
                genre = "878";
                break;

            case "tv movie" :
                genre = "10770";
                break;

            case "thriller" :
                genre = "53";
                break;

            case "war" :
                genre = "10752";
                break;

            case "western" :
                genre = "37";
                break;

            case "action adventure" :
                genre = "10759";
                break;

            case "kids" :
                genre = "10762";
                break;

            case "news" :
                genre = "10763";
                break;

            case "reality" :
                genre = "10764";
                break;

            case "sci-fi and fantasy" :
                genre = "10765";
                break;

            case "soap" :
                genre = "10766";
                break;

            case "talk" :
                genre = "10767";
                break;

            case "war and politics" :
                genre = "10768";
                break;
        }

        switch(yearSelected){
            case "1980" :
                year = "1980";
                break;

            case "1981" :
                year = "1981";
                break;

            case "1982" :
                year = "1982";
                break;

            case "1983" :
                year = "1983";
                break;

            case "1984" :
                year = "1984";
                break;

            case "1985" :
                year = "1985";
                break;

            case "1986" :
                year = "1986";
                break;

            case "1987" :
                year = "1987";
                break;

            case "1988" :
                year = "1988";
                break;

            case "1989" :
                year = "1989";
                break;

            case "1990" :
                year = "1990";
                break;

            case "1991" :
                year = "1991";
                break;

            case "1992" :
                year = "1992";
                break;

            case "1993" :
                year = "1993";
                break;

            case "1994" :
                year = "1994";
                break;

            case "1995" :
                year = "1995";
                break;

            case "1996" :
                year = "1996";
                break;

            case "1997" :
                year = "1997";
                break;

            case "1998" :
                year = "1998";
                break;

            case "1999" :
                year = "1999";
                break;

            case "2000" :
                year = "2000";
                break;

            case "2001" :
                year = "2001";
                break;

            case "2002" :
                year = "2002";
                break;

            case "2003" :
                year = "2003";
                break;

            case "2004" :
                year = "2004";
                break;

            case "2005" :
                year = "2005";
                break;

            case "2006" :
                year = "2006";
                break;

            case "2007" :
                year = "2007";
                break;

            case "2008" :
                year = "2008";
                break;

            case "2009" :
                year = "2009";
                break;

            case "2010" :
                year = "2010";
                break;

            case "2011" :
                year = "2011";
                break;

            case "2012" :
                year = "2012";
                break;

            case "2013" :
                year = "2013";
                break;

            case "2014" :
                year = "2014";
                break;

            case "2015" :
                year = "2015";
                break;

            case "2016" :
                year = "2016";
                break;

            case "2017" :
                year = "2017";
                break;

            case "2018" :
                year = "2018";
                break;

            case "2019" :
                year = "2019";
                break;

            case "2020" :
                year = "2020";
                break;

            case "2021" :
                year = "2021";
                break;

            case "2022" :
                year = "2022";
                break;

            case "2023" :
                year = "2023";
                break;

            case "2024" :
                year = "2024";
                break;

            case "2025" :
                year = "2025";
                break;
        }

        switch(regionSelected){
            case("australia") :
                region = "AU";
                break;

            case("china") :
                region = "CN";
                break;

            case("france") :
                region = "FR";
                break;

            case("germany") :
                region = "DE";
                break;

            case("hong kong") :
                region = "HK";
                break;

            case("india") :
                region = "IN";
                break;

            case("indonesia") :
                region = "ID";
                break;

            case("japan") :
                region = "JP";
                break;

            case("russia") :
                region = "RU";
                break;

            case("south korea") :
                region = "KR";
                break;

            case("taiwan") :
                region = "TW";
                break;

            case("thailand") :
                region = "TH";
                break;

            case("united kingdom") :
                region = "UK";
                break;

            case("united states") :
                region = "US";
                break;
        }

        switch(sortSelected){
            case "popularity (ascending)" :
                sortBy = "popularity.asc";
                break;

            case "popularity (descending)" :
                sortBy = "popularity.desc";
                break;

            case "revenue (ascending)" :
                sortBy = "revenue.asc";
                break;

            case "revenue (descending)" :
                sortBy = "revenue.desc";
                break;

            case "release date (ascending)" :
                sortBy = "primary_release_date.asc";
                break;

            case "release date (descending)" :
                sortBy = "primary_release_date.desc";
                break;

            case "vote average (ascending)" :
                sortBy = "vote_average.asc";
                break;

            case "vote average (descending)" :
                sortBy = "vote_average.desc";
                break;

            case "vote count (ascending)" :
                sortBy = "vote_count.asc";
                break;

            case "vote count (descending)" :
                sortBy = "vote_count.desc";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}