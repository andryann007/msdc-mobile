package com.example.msdc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.msdc.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private ApiService apiService;
    private RecyclerView rvMoviePopular, rvMovieNowPlaying, rvMovieTopRated, rvMovieUpcoming;
    private ProgressBar loadingMoviePopular, loadingMovieNowPlaying, loadingMovieTopRated, loadingMovieUpcoming;
    private MovieAdapter moviePopularAdapter, movieNowPlayingAdapter, movieTopRatedAdapter, movieUpcomingAdapter;
    private final List<MovieResult> moviePopularResults = new ArrayList<>();
    private final List<MovieResult> movieNowPlayingResults = new ArrayList<>();
    private final List<MovieResult> movieTopRatedResults = new ArrayList<>();
    private final List<MovieResult> movieUpcomingResults = new ArrayList<>();
    private int currentPageMoviePopular = 1, currentPageMovieNowPlaying = 1,
            currentPageMovieTopRated = 1, currentPageUpcomingMovie = 1;
    private int totalPagesMoviePopular = 1, totalPagesMovieNowPlaying = 1,
            totalPagesMovieTopRated = 1, totalPagesUpcomingMovie = 1;

    private RecyclerView rvTvPopular, rvTvTopRated, rvTvOnAir;
    private ProgressBar loadingTvPopular, loadingTvTopRated, loadingTvOnAir;
    private TVAdapter tvPopularAdapter, tvTopRatedAdapter, tvOnAirAdapter;
    private final List<TVResult> tvPopularResults = new ArrayList<>();
    private final List<TVResult> tvTopRatedResults = new ArrayList<>();
    private final List<TVResult> tvOnAirResults = new ArrayList<>();
    private int currentPageTVPopular = 1, currentPageTVTopRated = 1, currentPageTVOnAir = 1;
    private int totalPagesTVPopular = 1, totalPagesTVTopRated = 1, totalPagesTVOnAir = 1;

    public static final String MYAPI_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";

    private String searchType = null;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setPopularMovies();
        setNowPlayingMovies();
        setUpcomingMovies();
        setTopRatedMovies();

        setPopularTV();
        setTopRatedTV();
        setOnAirTV();

        binding.imageSearch.setOnClickListener(v -> dialogSearch());
    }

    private void dialogSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_search, null);
        EditText inputSearch = v.findViewById(R.id.inputSearch);
        ImageView imageDoSearch = v.findViewById(R.id.imageDoSearch);
        RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
        RadioButton radioButtonMovie = v.findViewById(R.id.radioButtonMovie);
        RadioButton radioButtonTV = v.findViewById(R.id.radioButtonTV);

        builder.setView(v);
        AlertDialog dialogSearch = builder.create();
        if(dialogSearch.getWindow() != null){
            dialogSearch.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            radioGroup.setOnCheckedChangeListener((group, checkedid) -> {
                if(checkedid == R.id.radioButtonMovie){
                    searchType = radioButtonMovie.getText().toString();
                } else {
                    searchType = radioButtonTV.getText().toString();
                }
            });
            imageDoSearch.setOnClickListener(view -> doSearch(inputSearch.getText().toString()));

            inputSearch.setOnEditorActionListener((v1, actionid, event) -> {
                if(actionid == EditorInfo.IME_ACTION_GO){
                    doSearch(inputSearch.getText().toString());
                }
                return false;
            });
        }
    }

    private void doSearch(String query) {
        if(query.isEmpty()){
            Toast.makeText(getApplicationContext(),"Tidak ada inputan!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(searchType == null){
            Toast.makeText(getApplicationContext(),"Harap pilih tipe search!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("tipe", searchType);
        i.putExtra("searchFor", query);
        startActivity(i);
    }

    private void setOnAirTV() {
        rvTvOnAir = findViewById(R.id.rvTVOnAir);
        tvOnAirAdapter = new TVAdapter(tvOnAirResults, this);
        loadingTvOnAir = findViewById(R.id.loadingTVOnAir);

        getOnAirTV();
        rvTvOnAir.setAdapter(tvOnAirAdapter);
    }

    private void getOnAirTV(){
        Call<TVRespon> call = apiService.getTvOnAir(MYAPI_KEY, LANGUAGE, currentPageTVOnAir);
        call.enqueue(new Callback<TVRespon>(){

            @Override
            public void onResponse(Call<TVRespon> call, Response<TVRespon> response) {
                if(response.body() != null){
                    totalPagesTVOnAir = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingTvOnAir.setVisibility(View.GONE);
                        int oldCount = tvOnAirResults.size();
                        tvOnAirResults.addAll(response.body().getResult());
                        tvOnAirAdapter.notifyItemChanged(oldCount, tvOnAirResults.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<TVRespon> call, Throwable t) {

            }
        });
    }

    private void setTopRatedTV() {
        rvTvTopRated = findViewById(R.id.rvTVTopRated);
        tvTopRatedAdapter = new TVAdapter(tvTopRatedResults, this);
        loadingTvTopRated = findViewById(R.id.loadingTVTopRated);

        getTopRatedTV();
        rvTvTopRated.setAdapter(tvTopRatedAdapter);
    }

    private void getTopRatedTV(){
        Call<TVRespon> call = apiService.getTvTopRated(MYAPI_KEY, LANGUAGE, currentPageTVTopRated);
        call.enqueue(new Callback<TVRespon>(){

            @Override
            public void onResponse(Call<TVRespon> call, Response<TVRespon> response) {
                if(response.body() != null){
                    totalPagesTVTopRated = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingTvTopRated.setVisibility(View.GONE);
                        int oldCount = tvTopRatedResults.size();
                        tvTopRatedResults.addAll(response.body().getResult());
                        tvTopRatedAdapter.notifyItemChanged(oldCount, tvTopRatedResults.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<TVRespon> call, Throwable t) {

            }
        });
    }

    private void setPopularTV() {
        rvTvPopular = findViewById(R.id.rvTVPopular);
        tvPopularAdapter = new TVAdapter(tvPopularResults, this);
        loadingTvPopular = findViewById(R.id.loadingTVPopular);

        getPopularTV();
        rvTvPopular.setAdapter(tvPopularAdapter);
    }

    private void getPopularTV(){
        Call<TVRespon> call = apiService.getTvPopular(MYAPI_KEY, LANGUAGE, currentPageTVPopular);
        call.enqueue(new Callback<TVRespon>(){

            @Override
            public void onResponse(Call<TVRespon> call, Response<TVRespon> response) {
                if(response.body() != null){
                    totalPagesTVPopular = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingTvPopular.setVisibility(View.GONE);
                        int oldCount = tvPopularResults.size();
                        tvPopularResults.addAll(response.body().getResult());
                        tvPopularAdapter.notifyItemChanged(oldCount, tvPopularResults.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<TVRespon> call, Throwable t) {

            }
        });
    }

    private void setTopRatedMovies() {
        rvMovieTopRated = findViewById(R.id.rvMovieTopRated);
        movieTopRatedAdapter = new MovieAdapter(movieTopRatedResults, this);
        loadingMovieTopRated = findViewById(R.id.loadingMovieTopRated);

        getTopRatedMovies();
        rvMovieTopRated.setAdapter(movieTopRatedAdapter);
    }

    private void getTopRatedMovies(){
        Call<MovieRespon> call = apiService.getTopRatedMovies(MYAPI_KEY, LANGUAGE, currentPageMovieTopRated);
        call.enqueue(new Callback<MovieRespon>(){

            @Override
            public void onResponse(Call<MovieRespon> call, Response<MovieRespon> response) {
                if(response.body() != null){
                    totalPagesMovieTopRated = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingMovieTopRated.setVisibility(View.GONE);
                        int oldCount = movieTopRatedResults.size();
                        movieTopRatedResults.addAll(response.body().getResult());
                        movieTopRatedAdapter.notifyItemChanged(oldCount, movieTopRatedResults.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieRespon> call, Throwable t) {

            }
        });
    }

    private void setUpcomingMovies() {
        rvMovieUpcoming = findViewById(R.id.rvUpcomingMovie);
        movieUpcomingAdapter = new MovieAdapter(movieUpcomingResults, this);
        loadingMovieUpcoming = findViewById(R.id.loadingUpcomingMovie);

        getUpcomingMovies();
        rvMovieUpcoming.setAdapter(movieUpcomingAdapter);
    }

    private void getUpcomingMovies(){
        Call<MovieRespon> call = apiService.getUpcomingMovies(MYAPI_KEY, LANGUAGE, currentPageMoviePopular);
        call.enqueue(new Callback<MovieRespon>(){

            @Override
            public void onResponse(Call<MovieRespon> call, Response<MovieRespon> response) {
                if(response.body() != null){
                    totalPagesUpcomingMovie = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingMovieUpcoming.setVisibility(View.GONE);
                        int oldCount = movieUpcomingResults.size();
                        movieUpcomingResults.addAll(response.body().getResult());
                        movieUpcomingAdapter.notifyItemChanged(oldCount, movieUpcomingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieRespon> call, Throwable t) {

            }
        });
    }

    private void setNowPlayingMovies() {
        rvMovieNowPlaying = findViewById(R.id.rvMovieNowPlaying);
        movieNowPlayingAdapter = new MovieAdapter(movieNowPlayingResults, this);
        loadingMovieNowPlaying = findViewById(R.id.loadingMovieNowPlaying);

        getNowPlayingMovies();
        rvMovieNowPlaying.setAdapter(movieNowPlayingAdapter);
    }

    private void getNowPlayingMovies(){
        Call<MovieRespon> call = apiService.getNowPlayingMovies(MYAPI_KEY, LANGUAGE, currentPageMoviePopular);
        call.enqueue(new Callback<MovieRespon>(){

            @Override
            public void onResponse(Call<MovieRespon> call, Response<MovieRespon> response) {
                if(response.body() != null){
                    totalPagesMovieNowPlaying = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingMovieNowPlaying.setVisibility(View.GONE);
                        int oldCount = movieNowPlayingResults.size();
                        movieNowPlayingResults.addAll(response.body().getResult());
                        movieNowPlayingAdapter.notifyItemChanged(oldCount, movieNowPlayingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieRespon> call, Throwable t) {

            }
        });
    }

    private void setPopularMovies() {
        rvMoviePopular = findViewById(R.id.rvMoviePopular);
        moviePopularAdapter = new MovieAdapter(moviePopularResults, this);
        loadingMoviePopular = findViewById(R.id.loadingMoviePopular);

        getPopularMovies();
        rvMoviePopular.setAdapter(moviePopularAdapter);
    }

    private void getPopularMovies(){
        Call<MovieRespon> call = apiService.getPopularMovies(MYAPI_KEY, LANGUAGE, currentPageMoviePopular);
        call.enqueue(new Callback<MovieRespon>(){

            @Override
            public void onResponse(Call<MovieRespon> call, Response<MovieRespon> response) {
                if(response.body() != null){
                    totalPagesMoviePopular = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingMoviePopular.setVisibility(View.GONE);
                        int oldCount = moviePopularResults.size();
                        moviePopularResults.addAll(response.body().getResult());
                        moviePopularAdapter.notifyItemChanged(oldCount, moviePopularResults.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieRespon> call, Throwable t) {

            }
        });
    }
}