package com.example.msdc.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.adapter.MovieAdapter;
import com.example.msdc.adapter.PersonAdapter;
import com.example.msdc.adapter.TVAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.api.PersonResponse;
import com.example.msdc.api.PersonResult;
import com.example.msdc.api.TVResponse;
import com.example.msdc.api.TVResult;
import com.example.msdc.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    private ApiService apiService;
    private ProgressBar loadingMovieTrending, loadingMoviePopular, loadingMovieNowPlaying, loadingMovieTopRated, loadingMovieUpcoming;
    private MovieAdapter movieTrendingAdapter, moviePopularAdapter, movieNowPlayingAdapter, movieTopRatedAdapter, movieUpcomingAdapter;

    private final List<MovieResult> movieTrendingResults = new ArrayList<>();
    private final List<MovieResult> moviePopularResults = new ArrayList<>();
    private final List<MovieResult> movieNowPlayingResults = new ArrayList<>();
    private final List<MovieResult> movieTopRatedResults = new ArrayList<>();
    private final List<MovieResult> movieUpcomingResults = new ArrayList<>();

    private ProgressBar loadingTvTrending, loadingTvPopular, loadingTvTopRated, loadingTvOnAir, loadingTvAiringToday;
    private TVAdapter tvTrendingAdapter, tvPopularAdapter, tvTopRatedAdapter, tvOnAirAdapter, tvAiringTodayAdapter;

    private final List<TVResult> tvTrendingResults = new ArrayList<>();

    private final List<TVResult> tvPopularResults = new ArrayList<>();
    private final List<TVResult> tvTopRatedResults = new ArrayList<>();
    private final List<TVResult> tvOnAirResults = new ArrayList<>();
    private final List<TVResult> tvAiringTodayResults = new ArrayList<>();

    private ProgressBar loadingPersonTrending;
    private PersonAdapter personTrendingAdapter;

    private final List<PersonResult> personTrendingResults = new ArrayList<>();

    public static final String MYAPI_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";

    public static final String TRENDING_MOVIE = "movie";
    public static final String TRENDING_TV = "tv";
    public static final String TRENDING_PERSON = "person";

    public static final String TIME_WINDOW = "week";
    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setTrendingMovies(root);
        setTrendingTV(root);
        setTrendingPerson(root);

        setPopularMovies(root);
        setNowPlayingMovies(root);
        setUpcomingMovies(root);
        setTopRatedMovies(root);

        setPopularTV(root);
        setTopRatedTV(root);
        setOnAirTV(root);
        setOnAiringTV(root);

        return root;
    }

    private void setTopRatedMovies(View view) {
        RecyclerView rvMovieTopRated = view.findViewById(R.id.rvMovieTopRated);
        movieTopRatedAdapter = new MovieAdapter(movieTopRatedResults, getContext());
        loadingMovieTopRated = view.findViewById(R.id.loadingMovieTopRated);

        getTopRatedMovies();
        rvMovieTopRated.setAdapter(movieTopRatedAdapter);
    }

    private void getTopRatedMovies(){
        int currentPageMovieTopRated = 1;
        Call<MovieResponse> call = apiService.getTopRatedMovies(MYAPI_KEY, LANGUAGE, currentPageMovieTopRated);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMovieTopRated.setVisibility(View.GONE);
                        int oldCount = movieTopRatedResults.size();
                        movieTopRatedResults.addAll(response.body().getResult());
                        movieTopRatedAdapter.notifyItemChanged(oldCount, movieTopRatedResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setUpcomingMovies(View view) {
        RecyclerView rvMovieUpcoming = view.findViewById(R.id.rvUpcomingMovie);
        movieUpcomingAdapter = new MovieAdapter(movieUpcomingResults, getContext());
        loadingMovieUpcoming = view.findViewById(R.id.loadingUpcomingMovie);

        getUpcomingMovies();
        rvMovieUpcoming.setAdapter(movieUpcomingAdapter);
    }

    private void getUpcomingMovies(){
        int currentPageUpcomingMovie = 1;
        Call<MovieResponse> call = apiService.getUpcomingMovies(MYAPI_KEY, LANGUAGE, currentPageUpcomingMovie);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMovieUpcoming.setVisibility(View.GONE);
                        int oldCount = movieUpcomingResults.size();
                        movieUpcomingResults.addAll(response.body().getResult());
                        movieUpcomingAdapter.notifyItemChanged(oldCount, movieUpcomingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setNowPlayingMovies(View view) {
        RecyclerView rvMovieNowPlaying = view.findViewById(R.id.rvMovieNowPlaying);
        movieNowPlayingAdapter = new MovieAdapter(movieNowPlayingResults, getContext());
        loadingMovieNowPlaying = view.findViewById(R.id.loadingMovieNowPlaying);

        getNowPlayingMovies();
        rvMovieNowPlaying.setAdapter(movieNowPlayingAdapter);
    }

    private void getNowPlayingMovies(){
        int currentPageMovieNowPlaying = 1;
        Call<MovieResponse> call = apiService.getNowPlayingMovies(MYAPI_KEY, LANGUAGE, currentPageMovieNowPlaying);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMovieNowPlaying.setVisibility(View.GONE);
                        int oldCount = movieNowPlayingResults.size();
                        movieNowPlayingResults.addAll(response.body().getResult());
                        movieNowPlayingAdapter.notifyItemChanged(oldCount, movieNowPlayingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setPopularMovies(View view) {
        RecyclerView rvMoviePopular = view.findViewById(R.id.rvMoviePopular);
        moviePopularAdapter = new MovieAdapter(moviePopularResults, getContext());
        loadingMoviePopular = view.findViewById(R.id.loadingMoviePopular);

        getPopularMovies();
        rvMoviePopular.setAdapter(moviePopularAdapter);
    }

    private void getPopularMovies(){
        int currentPageMoviePopular = 1;
        Call<MovieResponse> call = apiService.getPopularMovies(MYAPI_KEY, LANGUAGE, currentPageMoviePopular);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMoviePopular.setVisibility(View.GONE);
                        int oldCount = moviePopularResults.size();
                        moviePopularResults.addAll(response.body().getResult());
                        moviePopularAdapter.notifyItemChanged(oldCount, moviePopularResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setOnAirTV(View view) {
        RecyclerView rvTvOnAir = view.findViewById(R.id.rvTVOnAir);
        tvOnAirAdapter = new TVAdapter(tvOnAirResults, getContext());
        loadingTvOnAir = view.findViewById(R.id.loadingTVOnAir);

        getOnAirTV();
        rvTvOnAir.setAdapter(tvOnAirAdapter);
    }

    private void getOnAirTV(){
        int currentPageTVOnAir = 1;
        Call<TVResponse> call = apiService.getTvOnAir(MYAPI_KEY, LANGUAGE, currentPageTVOnAir);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvOnAir.setVisibility(View.GONE);
                        int oldCount = tvOnAirResults.size();
                        tvOnAirResults.addAll(response.body().getResult());
                        tvOnAirAdapter.notifyItemChanged(oldCount, tvOnAirResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setTopRatedTV(View view) {
        RecyclerView rvTvTopRated = view.findViewById(R.id.rvTVTopRated);
        tvTopRatedAdapter = new TVAdapter(tvTopRatedResults, getContext());
        loadingTvTopRated = view.findViewById(R.id.loadingTVTopRated);

        getTopRatedTV();
        rvTvTopRated.setAdapter(tvTopRatedAdapter);
    }

    private void getTopRatedTV(){
        int currentPageTVTopRated = 1;
        Call<TVResponse> call = apiService.getTvTopRated(MYAPI_KEY, LANGUAGE, currentPageTVTopRated);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvTopRated.setVisibility(View.GONE);
                        int oldCount = tvTopRatedResults.size();
                        tvTopRatedResults.addAll(response.body().getResult());
                        tvTopRatedAdapter.notifyItemChanged(oldCount, tvTopRatedResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setPopularTV(View view) {
        RecyclerView rvTvPopular = view.findViewById(R.id.rvTVPopular);
        tvPopularAdapter = new TVAdapter(tvPopularResults, getContext());
        loadingTvPopular = view.findViewById(R.id.loadingTVPopular);

        getPopularTV();
        rvTvPopular.setAdapter(tvPopularAdapter);
    }

    private void getPopularTV(){
        int currentPageTVPopular = 1;
        Call<TVResponse> call = apiService.getTvPopular(MYAPI_KEY, LANGUAGE, currentPageTVPopular);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvPopular.setVisibility(View.GONE);
                        int oldCount = tvPopularResults.size();
                        tvPopularResults.addAll(response.body().getResult());
                        tvPopularAdapter.notifyItemChanged(oldCount, tvPopularResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setOnAiringTV(View view) {
        RecyclerView rvTvAiringToday = view.findViewById(R.id.rvTVAiring);
        tvAiringTodayAdapter = new TVAdapter(tvAiringTodayResults, getContext());
        loadingTvAiringToday = view.findViewById(R.id.loadingTVAiring);

        getOnAiringTV();
        rvTvAiringToday.setAdapter(tvAiringTodayAdapter);
    }

    private void getOnAiringTV(){
        int currentPageTVAiringToday = 1;
        Call<TVResponse> call = apiService.getTvAiringToday(MYAPI_KEY, LANGUAGE, currentPageTVAiringToday);
        call.enqueue(new Callback<TVResponse>(){
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvAiringToday.setVisibility(View.GONE);
                        int oldCount = tvAiringTodayResults.size();
                        tvAiringTodayResults.addAll(response.body().getResult());
                        tvAiringTodayAdapter.notifyItemChanged(oldCount, tvAiringTodayResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void setTrendingMovies(View view) {
        RecyclerView rvMovieTrending = view.findViewById(R.id.rvMovieTrending);
        movieTrendingAdapter = new MovieAdapter(movieTrendingResults, getContext());
        loadingMovieTrending = view.findViewById(R.id.loadingMovieTrending);

        getTrendingMovies();
        rvMovieTrending.setAdapter(movieTrendingAdapter);
    }

    private void getTrendingMovies(){
        Call<MovieResponse> call = apiService.getTrendingMovies(TRENDING_MOVIE, TIME_WINDOW, MYAPI_KEY);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMovieTrending.setVisibility(View.GONE);
                        int oldCount = movieTrendingResults.size();
                        movieTrendingResults.addAll(response.body().getResult());
                        movieTrendingAdapter.notifyItemChanged(oldCount, movieTrendingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setTrendingTV(View view) {
        RecyclerView rvTvTrending = view.findViewById(R.id.rvTVTrending);
        tvTrendingAdapter = new TVAdapter(tvTrendingResults, getContext());
        loadingTvTrending = view.findViewById(R.id.loadingTVTrending);

        getTrendingTV();
        rvTvTrending.setAdapter(tvTrendingAdapter);
    }

    private void getTrendingTV(){
        Call<TVResponse> call = apiService.getTrendingTV(TRENDING_TV, TIME_WINDOW, MYAPI_KEY);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvTrending.setVisibility(View.GONE);
                        int oldCount = tvTrendingResults.size();
                        tvTrendingResults.addAll(response.body().getResult());
                        tvTrendingAdapter.notifyItemChanged(oldCount, tvTrendingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setTrendingPerson(View view) {
        RecyclerView rvPersonTrending = view.findViewById(R.id.rvPersonTrending);
        personTrendingAdapter = new PersonAdapter(personTrendingResults, getContext());
        loadingPersonTrending = view.findViewById(R.id.loadingPersonTrending);

        getTrendingPerson();
        rvPersonTrending.setAdapter(personTrendingAdapter);
    }

    private void getTrendingPerson(){
        Call<PersonResponse> call = apiService.getTrendingPerson(TRENDING_PERSON, TIME_WINDOW, MYAPI_KEY);
        call.enqueue(new Callback<PersonResponse>(){

            @Override
            public void onResponse(@NonNull Call<PersonResponse> call, @NonNull Response<PersonResponse> response) {
                if(response.body() != null){
                    if(response.body().getResults()!=null){
                        loadingPersonTrending.setVisibility(View.GONE);
                        int oldCount = personTrendingResults.size();
                        personTrendingResults.addAll(response.body().getResults());
                        personTrendingAdapter.notifyItemChanged(oldCount, personTrendingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PersonResponse> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}