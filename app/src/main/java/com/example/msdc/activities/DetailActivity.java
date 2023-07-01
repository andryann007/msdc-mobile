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

    private String searchType = null;
    private String filterType = null;
    private String genre = null;
    private String year = null;
    private String region = null;
    private String sortBy = null;

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
            tv_id = getIntent().getIntExtra("tv_id", 0);
            setTVDetails();
        }

        binding.toolbar.setOnClickListener(v-> onBackPressed());
    }

    private void setMovieDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<MovieDetails> call = apiService.getMovieDetails(String.valueOf(movie_id), MainActivity.MY_API_KEY);
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

                    binding.textGenreList.setVisibility(View.GONE);
                    binding.textKeywordList.setVisibility(View.GONE);

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
                break;

            case "tv" :
                i.putExtra("type", "tv");
                i.putExtra("genre", genre);
                i.putExtra("year", year);
                i.putExtra("region", region);
                i.putExtra("sortBy", sortBy);
                break;
        }
    }


    private void setGenresMovie(){
        genreAdapter = new GenreAdapter(movieGenre, this);

        getGenresMovie();
        binding.rvGenreList.setAdapter(genreAdapter);
    }

    private void getGenresMovie(){
        Call<MovieDetails> call = apiService.getMovieDetails(String.valueOf(movie_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {
                assert response.body() != null;
                if(response.body().getGenres()!=null){
                    binding.textGenreList.setVisibility(View.VISIBLE);
                    int oldCount = movieGenre.size();
                    movieGenre.addAll(response.body().getGenres());
                    genreAdapter.notifyItemChanged(oldCount, movieGenre.size());
                } else if(movieGenre.isEmpty()){
                    TextView tvGenresResult = findViewById(R.id.textGenreList);
                    tvGenresResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Genres List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setKeywordsMovie(){
        keywordAdapter = new KeywordAdapter(movieKeyword, this);

        getKeywordsMovie();
        binding.rvKeywordList.setAdapter(keywordAdapter);
    }

    private void getKeywordsMovie(){
        Call<KeywordResponse> call = apiService.getMovieKeywords(String.valueOf(movie_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<KeywordResponse>() {
            @Override
            public void onResponse(@NonNull Call<KeywordResponse> call, @NonNull Response<KeywordResponse> response) {
                assert response.body() != null;
                if(response.body().getKeywords()!=null){
                    binding.textKeywordList.setVisibility(View.VISIBLE);
                    int oldCount = movieKeyword.size();
                    movieKeyword.addAll(response.body().getKeywords());
                    keywordAdapter.notifyItemChanged(oldCount, movieKeyword.size());
                } else if(movieKeyword.isEmpty()){
                    TextView tvKeywordsResult = findViewById(R.id.textKeywordList);
                    tvKeywordsResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<KeywordResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Keywords List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImagesMovie(){
        movieImagesAdapter = new ImageListAdapter(movieImagesList, this);

        getImagesMovie();
        binding.rvImagesList.setAdapter(movieImagesAdapter);
    }

    private void getImagesMovie(){
        Call<ImageResponse> call = apiService.getMovieImages(String.valueOf(movie_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                assert response.body() != null;
                if(response.body().getResults()!=null){
                    int oldCount = movieImagesList.size();
                    movieImagesList.addAll(response.body().getResults());
                    movieImagesAdapter.notifyItemChanged(oldCount, movieImagesList.size());
                } else if(movieImagesList.isEmpty()){
                    TextView tvImagesListResult = findViewById(R.id.textImageList);
                    tvImagesListResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Images List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMovieCast(){
        creditCastAdapter = new CreditCastAdapter(movieCreditCastResult, this);

        getMovieCast();
        binding.rvCreditCast.setAdapter(creditCastAdapter);
    }

    private void getMovieCast(){
        Call<CreditResponse> call = apiService.getMovieCredit(String.valueOf(movie_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<CreditResponse>() {

            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(response.body().getCast()!=null){
                    int oldCount = movieCreditCastResult.size();
                    movieCreditCastResult.addAll(response.body().getCast());
                    creditCastAdapter.notifyItemChanged(oldCount, movieCreditCastResult.size());
                } else if(movieCreditCastResult.isEmpty()){
                    TextView tvCreditCastResult = findViewById(R.id.textMovieCreditCast);
                    tvCreditCastResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Credit Cast List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMovieCrew(){
        creditCrewAdapter = new CreditCrewAdapter(movieCreditCrewResult, this);

        getMovieCrew();
        binding.rvCreditCrew.setAdapter(creditCrewAdapter);
    }

    private void getMovieCrew(){
        Call<CreditResponse> call = apiService.getMovieCredit(String.valueOf(movie_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<CreditResponse>() {

            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(response.body().getCrew()!=null){
                    int oldCount = movieCreditCrewResult.size();
                    movieCreditCrewResult.addAll(response.body().getCrew());
                    creditCrewAdapter.notifyItemChanged(oldCount, movieCreditCrewResult.size());
                } else if(movieCreditCrewResult.isEmpty()){
                    TextView tvCreditCrewResult = findViewById(R.id.textMovieCreditCrew);
                    tvCreditCrewResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Credit Crew List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecommendationsMovie(){
        movieRecommendationsAdapter = new MovieAdapter(movieRecommendationsResult, this);

        getRecommendationsMovie();
        binding.rvMovieRecommendations.setAdapter(movieRecommendationsAdapter);
    }

    private void getRecommendationsMovie(){
        Call<MovieResponse> call = apiService.getMovieRecommendations(String.valueOf(movie_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                assert response.body() != null;
                if(response.body().getResult()!=null){
                    int oldCount = movieRecommendationsResult.size();
                    movieRecommendationsResult.addAll(response.body().getResult());
                    movieRecommendationsAdapter.notifyItemChanged(oldCount, movieRecommendationsResult.size());
                } else if(movieRecommendationsResult.isEmpty()){
                    TextView tvRecommendationResult = findViewById(R.id.textMovieRecommendations);
                    tvRecommendationResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Recommendations Movies List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSimilarMovie(){
        movieSimilarAdapter = new MovieAdapter(movieSimilarResult, this);

        getSimilarMovie();
        binding.rvMovieSimilar.setAdapter(movieSimilarAdapter);
    }

    private void getSimilarMovie(){
        Call<MovieResponse> call = apiService.getMovieSimilar(String.valueOf(movie_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                assert response.body() != null;
                if(response.body().getResult()!=null){
                    int oldCount = movieSimilarResult.size();
                    movieSimilarResult.addAll(response.body().getResult());
                    movieSimilarAdapter.notifyItemChanged(oldCount, movieSimilarResult.size());
                } else if(movieSimilarResult.isEmpty()){
                    TextView tvSimilarResult = findViewById(R.id.textMovieSimilar);
                    tvSimilarResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Similar Movies List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTVDetails(){
        binding.loadingDetails.setVisibility(View.VISIBLE);
        Call<TVDetails> call = apiService.getTvDetails(String.valueOf(tv_id), MainActivity.MY_API_KEY);
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

                    binding.textGenreList.setVisibility(View.GONE);
                    binding.textKeywordList.setVisibility(View.GONE);
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
                Toast.makeText(getApplicationContext(), "Fail to Fetch Detail Data !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGenresTV(){
        genreAdapter = new GenreAdapter(tvGenre, this);

        getGenresTV();
        binding.rvGenreList.setAdapter(genreAdapter);
    }

    private void getGenresTV(){
        Call<TVDetails> call = apiService.getTvDetails(String.valueOf(tv_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<TVDetails>() {
            @Override
            public void onResponse(@NonNull Call<TVDetails> call, @NonNull Response<TVDetails> response) {
                assert response.body() != null;
                if(response.body().getGenres()!=null){
                    binding.textGenreList.setVisibility(View.VISIBLE);
                    int oldCount = tvGenre.size();
                    tvGenre.addAll(response.body().getGenres());
                    genreAdapter.notifyItemChanged(oldCount, tvGenre.size());
                } else if(tvGenre.isEmpty()){
                    TextView tvGenres = findViewById(R.id.textGenreList);
                    tvGenres.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVDetails> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Genres List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setKeywordsTV(){
        keywordAdapter = new KeywordAdapter(tvKeyword, this);

        getKeywordsTV();
        binding.rvKeywordList.setAdapter(keywordAdapter);
    }

    private void getKeywordsTV(){
        Call<KeywordResponse> call = apiService.getTvKeywords(String.valueOf(tv_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<KeywordResponse>() {
            @Override
            public void onResponse(@NonNull Call<KeywordResponse> call, @NonNull Response<KeywordResponse> response) {
                assert response.body() != null;
                if(response.body().getKeywords()!=null){
                    binding.textKeywordList.setVisibility(View.VISIBLE);
                    int oldCount = tvKeyword.size();
                    tvKeyword.addAll(response.body().getKeywords());
                    keywordAdapter.notifyItemChanged(oldCount, tvKeyword.size());
                } else if(tvKeyword.isEmpty()){
                    TextView tvKeywords = findViewById(R.id.textKeywordList);
                    tvKeywords.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<KeywordResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Keywords List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImagesTV(){
        tvImagesAdapter = new ImageListAdapter(tvImagesList, this);

        getImagesTV();
        binding.rvImagesList.setAdapter(tvImagesAdapter);
    }

    private void getImagesTV(){
        Call<ImageResponse> call = apiService.getTvImages(String.valueOf(tv_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                assert response.body() != null;
                if(response.body().getResults()!=null){
                    int oldCount = tvImagesList.size();
                    tvImagesList.addAll(response.body().getResults());
                    tvImagesAdapter.notifyItemChanged(oldCount, tvImagesList.size());
                } else if(tvImagesList.isEmpty()){
                    TextView tvImageList = findViewById(R.id.textImageList);
                    tvImageList.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Images List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTvCast(){
        creditCastAdapter = new CreditCastAdapter(tvCreditCastResult, this);

        getTvCast();
        binding.rvCreditCast.setAdapter(creditCastAdapter);
    }

    private void getTvCast(){
        Call<CreditResponse> call = apiService.getTvCredit(String.valueOf(tv_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(response.body().getCast()!=null){
                    int oldCount = tvCreditCastResult.size();
                    tvCreditCastResult.addAll(response.body().getCast());
                    creditCastAdapter.notifyItemChanged(oldCount, tvCreditCastResult.size());
                } else if(tvCreditCastResult.isEmpty()){
                    TextView tvCreditCast = findViewById(R.id.textMovieCreditCast);
                    tvCreditCast.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Credit Cast List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTvCrew(){
        creditCrewAdapter = new CreditCrewAdapter(tvCreditCrewResult, this);

        getTvCrew();
        binding.rvCreditCrew.setAdapter(creditCrewAdapter);
    }

    private void getTvCrew(){
        Call<CreditResponse> call = apiService.getTvCredit(String.valueOf(tv_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditResponse> call, @NonNull Response<CreditResponse> response) {
                assert response.body() != null;
                if(response.body().getCrew()!=null){
                    int oldCount = tvCreditCrewResult.size();
                    tvCreditCrewResult.addAll(response.body().getCrew());
                    creditCrewAdapter.notifyItemChanged(oldCount, tvCreditCrewResult.size());
                } else if(tvCreditCrewResult.isEmpty()){
                    TextView tvCreditCrewResult = findViewById(R.id.textMovieCreditCrew);
                    tvCreditCrewResult.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<CreditResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Credit Crew List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecommendationsTV(){
        tvRecommendationsAdapter = new TVAdapter(tvRecommendationsResult, this);

        getRecommendationsTV();
        binding.rvMovieRecommendations.setAdapter(tvRecommendationsAdapter);
    }

    private void getRecommendationsTV(){
        Call<TVResponse> call = apiService.getTVRecommendations(String.valueOf(tv_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                assert response.body() != null;
                if(response.body().getResult()!=null){
                    int oldCount = tvRecommendationsResult.size();
                    tvRecommendationsResult.addAll(response.body().getResult());
                    tvRecommendationsAdapter.notifyItemChanged(oldCount, tvRecommendationsResult.size());
                } else if(tvRecommendationsResult.isEmpty()){
                    TextView tvRecommendationResult = findViewById(R.id.textMovieRecommendations);
                    tvRecommendationResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Recommendation TV Shows List !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSimilarTV(){
        tvSimilarAdapter = new TVAdapter(tvSimilarResult, this);

        getSimilarTV();
        binding.rvMovieSimilar.setAdapter(tvSimilarAdapter);
    }

    private void getSimilarTV(){
        Call<TVResponse> call = apiService.getTVSimilar(String.valueOf(tv_id), MainActivity.MY_API_KEY);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                assert response.body() != null;
                if(response.body().getResult()!=null){
                    int oldCount = tvSimilarResult.size();
                    tvSimilarResult.addAll(response.body().getResult());
                    tvSimilarAdapter.notifyItemChanged(oldCount, tvSimilarResult.size());
                } else if(tvSimilarResult.isEmpty()){
                    TextView tvSimilarResult = findViewById(R.id.textMovieSimilar);
                    tvSimilarResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail to Fetch Similar TV Shows List !!!",
                        Toast.LENGTH_SHORT).show();
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

            case("england") :
                region = "GB";
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