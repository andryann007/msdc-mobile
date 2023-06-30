package com.example.msdc.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private RecyclerView rvMovieTrending, rvMoviePopular, rvMovieNowPlaying, rvMovieTopRated, rvMovieUpcoming;

    private final List<MovieResult> movieTrendingResults = new ArrayList<>();
    private final List<MovieResult> moviePopularResults = new ArrayList<>();
    private final List<MovieResult> movieNowPlayingResults = new ArrayList<>();
    private final List<MovieResult> movieTopRatedResults = new ArrayList<>();
    private final List<MovieResult> movieUpcomingResults = new ArrayList<>();

    private ProgressBar loadingTvTrending, loadingTvPopular, loadingTvTopRated, loadingTvOnAir, loadingTvAiringToday;
    private TVAdapter tvTrendingAdapter, tvPopularAdapter, tvTopRatedAdapter, tvOnAirAdapter, tvAiringTodayAdapter;
    private RecyclerView rvTvTrending, rvTvPopular, rvTvOnAir, rvTvTopRated, rvTvAiringToday;

    private final List<TVResult> tvTrendingResults = new ArrayList<>();

    private final List<TVResult> tvPopularResults = new ArrayList<>();
    private final List<TVResult> tvTopRatedResults = new ArrayList<>();
    private final List<TVResult> tvOnAirResults = new ArrayList<>();
    private final List<TVResult> tvAiringTodayResults = new ArrayList<>();

    private ProgressBar loadingPersonTrending;
    private PersonAdapter personTrendingAdapter;

    private final List<PersonResult> personTrendingResults = new ArrayList<>();

    private RecyclerView rvPersonTrending;

    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";

    public static final String TRENDING_MOVIE = "movie";
    public static final String TRENDING_TV = "tv";
    public static final String TRENDING_PERSON = "person";

    public static final String TIME_WINDOW = "week";

    private int page = 1;
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
        movieTopRatedAdapter = new MovieAdapter(movieTopRatedResults, getContext());

        rvMovieTopRated = view.findViewById(R.id.rvMovieTopRated);
        loadingMovieTopRated = view.findViewById(R.id.loadingMovieTopRated);

        rvMovieTopRated.setAdapter(movieTopRatedAdapter);
        getTopRatedMovies(page);

        rvMovieTopRated.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvMovieTopRated.canScrollHorizontally(1)){
                    page++;
                    getTopRatedMovies(page);
                }
            }
        });
    }

    private void getTopRatedMovies(int PAGE){
        Call<MovieResponse> call = apiService.getTopRatedMovies(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMovieTopRated.setVisibility(View.GONE);
                        rvMovieTopRated.setVisibility(View.VISIBLE);

                        int oldCount = movieTopRatedResults.size();
                        movieTopRatedResults.addAll(response.body().getResult());
                        movieTopRatedAdapter.notifyItemRangeInserted(oldCount, movieTopRatedResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadingMovieTopRated.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Top Rated Movie !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpcomingMovies(View view) {
        movieUpcomingAdapter = new MovieAdapter(movieUpcomingResults, getContext());

        loadingMovieUpcoming = view.findViewById(R.id.loadingUpcomingMovie);
        rvMovieUpcoming = view.findViewById(R.id.rvUpcomingMovie);

        rvMovieUpcoming.setAdapter(movieUpcomingAdapter);
        getUpcomingMovies(page);

        rvMovieUpcoming.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvMovieUpcoming.canScrollHorizontally(1)){
                    page++;
                    getUpcomingMovies(page);
                }
            }
        });
    }

    private void getUpcomingMovies(int PAGE){
        Call<MovieResponse> call = apiService.getUpcomingMovies(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMovieUpcoming.setVisibility(View.GONE);
                        rvMovieUpcoming.setVisibility(View.VISIBLE);

                        int oldCount = movieUpcomingResults.size();
                        movieUpcomingResults.addAll(response.body().getResult());
                        movieUpcomingAdapter.notifyItemRangeInserted(oldCount, movieUpcomingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadingMovieUpcoming.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Upcoming Movie !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNowPlayingMovies(View view) {
        movieNowPlayingAdapter = new MovieAdapter(movieNowPlayingResults, getContext());

        loadingMovieNowPlaying = view.findViewById(R.id.loadingMovieNowPlaying);
        rvMovieNowPlaying = view.findViewById(R.id.rvMovieNowPlaying);

        rvMovieNowPlaying.setAdapter(movieNowPlayingAdapter);
        getNowPlayingMovies(page);

        rvMovieNowPlaying.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvMovieNowPlaying.canScrollHorizontally(1)){
                    page++;
                    getNowPlayingMovies(page);
                }
            }
        });
    }

    private void getNowPlayingMovies(int PAGE){
        Call<MovieResponse> call = apiService.getNowPlayingMovies(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMovieNowPlaying.setVisibility(View.GONE);
                        rvMovieNowPlaying.setVisibility(View.VISIBLE);

                        int oldCount = movieNowPlayingResults.size();
                        movieNowPlayingResults.addAll(response.body().getResult());
                        movieNowPlayingAdapter.notifyItemRangeInserted(oldCount, movieNowPlayingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadingMovieNowPlaying.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Now Playing Movie !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPopularMovies(View view) {
        moviePopularAdapter = new MovieAdapter(moviePopularResults, getContext());

        loadingMoviePopular = view.findViewById(R.id.loadingMoviePopular);
        rvMoviePopular = view.findViewById(R.id.rvMoviePopular);

        rvMoviePopular.setAdapter(moviePopularAdapter);
        getPopularMovies(page);

        rvMoviePopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(rvMoviePopular.canScrollHorizontally(1)){
                    page++;
                    getPopularMovies(page);
                }
            }
        });
    }

    private void getPopularMovies(int PAGE){
        Call<MovieResponse> call = apiService.getPopularMovies(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingMoviePopular.setVisibility(View.GONE);
                        rvMoviePopular.setVisibility(View.VISIBLE);

                        int oldCount = moviePopularResults.size();
                        moviePopularResults.addAll(response.body().getResult());
                        moviePopularAdapter.notifyItemRangeInserted(oldCount, moviePopularResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadingMoviePopular.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Popular Movie !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnAirTV(View view) {
        tvOnAirAdapter = new TVAdapter(tvOnAirResults, getContext());

        loadingTvOnAir = view.findViewById(R.id.loadingTVOnAir);
        rvTvOnAir = view.findViewById(R.id.rvTVOnAir);

        rvTvOnAir.setAdapter(tvOnAirAdapter);
        getOnAirTV(page);

        rvTvOnAir.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvTvOnAir.canScrollHorizontally(1)){
                    page++;
                    getOnAirTV(page);
                }
            }
        });
    }

    private void getOnAirTV(int PAGE){
        Call<TVResponse> call = apiService.getTvOnAir(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvOnAir.setVisibility(View.GONE);
                        rvTvOnAir.setVisibility(View.VISIBLE);

                        int oldCount = tvOnAirResults.size();
                        tvOnAirResults.addAll(response.body().getResult());
                        tvOnAirAdapter.notifyItemRangeInserted(oldCount, tvOnAirResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                loadingTvOnAir.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch On Air TV Shows !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTopRatedTV(View view) {
        tvTopRatedAdapter = new TVAdapter(tvTopRatedResults, getContext());

        loadingTvTopRated = view.findViewById(R.id.loadingTVTopRated);
        rvTvTopRated = view.findViewById(R.id.rvTVTopRated);

        rvTvTopRated.setAdapter(tvTopRatedAdapter);
        getTopRatedTV(page);

        rvTvTopRated.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvTvTopRated.canScrollHorizontally(1)){
                    page++;
                    getTopRatedTV(page);
                }
            }
        });
    }

    private void getTopRatedTV(int PAGE){
        Call<TVResponse> call = apiService.getTvTopRated(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvTopRated.setVisibility(View.GONE);
                        rvTvTopRated.setVisibility(View.VISIBLE);

                        int oldCount = tvTopRatedResults.size();
                        tvTopRatedResults.addAll(response.body().getResult());
                        tvTopRatedAdapter.notifyItemRangeInserted(oldCount, tvTopRatedResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                loadingTvTopRated.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Top Rated TV Shows !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPopularTV(View view) {
        tvPopularAdapter = new TVAdapter(tvPopularResults, getContext());

        loadingTvPopular = view.findViewById(R.id.loadingTVPopular);
        rvTvPopular = view.findViewById(R.id.rvTVPopular);

        rvTvPopular.setAdapter(tvPopularAdapter);
        getPopularTV(page);

        rvTvPopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvTvPopular.canScrollHorizontally(1)){
                    page++;
                    getPopularTV(page);
                }
            }
        });
    }

    private void getPopularTV(int PAGE){
        Call<TVResponse> call = apiService.getTvPopular(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvPopular.setVisibility(View.GONE);
                        rvTvPopular.setVisibility(View.VISIBLE);

                        int oldCount = tvPopularResults.size();
                        tvPopularResults.addAll(response.body().getResult());
                        tvPopularAdapter.notifyItemRangeInserted(oldCount, tvPopularResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                loadingTvPopular.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Popular TV Shows !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnAiringTV(View view) {
        tvAiringTodayAdapter = new TVAdapter(tvAiringTodayResults, getContext());

        loadingTvAiringToday = view.findViewById(R.id.loadingTVAiring);
        rvTvAiringToday = view.findViewById(R.id.rvTVAiring);

        rvTvAiringToday.setAdapter(tvAiringTodayAdapter);
        getOnAiringTV(page);

        rvTvAiringToday.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvTvAiringToday.canScrollHorizontally(1)){
                    page++;
                    getOnAiringTV(page);
                }
            }
        });
    }

    private void getOnAiringTV(int PAGE){
        Call<TVResponse> call = apiService.getTvAiringToday(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<TVResponse>(){
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvAiringToday.setVisibility(View.GONE);
                        rvTvAiringToday.setVisibility(View.VISIBLE);

                        int oldCount = tvAiringTodayResults.size();
                        tvAiringTodayResults.addAll(response.body().getResult());
                        tvAiringTodayAdapter.notifyItemRangeInserted(oldCount, tvAiringTodayResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                loadingTvAiringToday.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Airing TV Shows !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTrendingMovies(View view) {
        movieTrendingAdapter = new MovieAdapter(movieTrendingResults, getContext());

        loadingMovieTrending = view.findViewById(R.id.loadingMovieTrending);
        rvMovieTrending = view.findViewById(R.id.rvMovieTrending);

        getTrendingMovies();
        rvMovieTrending.setAdapter(movieTrendingAdapter);
    }

    private void getTrendingMovies(){
        Call<MovieResponse> call = apiService.getTrendingMovies(TRENDING_MOVIE, TIME_WINDOW, MY_API_KEY);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult() != null){
                        loadingMovieTrending.setVisibility(View.GONE);
                        rvMovieTrending.setVisibility(View.VISIBLE);

                        int oldCount = movieTrendingResults.size();
                        movieTrendingResults.addAll(response.body().getResult());
                        movieTrendingAdapter.notifyItemChanged(oldCount, movieTrendingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadingMovieTrending.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Trending Movie !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTrendingTV(View view) {
        tvTrendingAdapter = new TVAdapter(tvTrendingResults, getContext());
        loadingTvTrending = view.findViewById(R.id.loadingTVTrending);
        rvTvTrending = view.findViewById(R.id.rvTVTrending);

        getTrendingTV();
        rvTvTrending.setAdapter(tvTrendingAdapter);
    }

    private void getTrendingTV(){
        Call<TVResponse> call = apiService.getTrendingTV(TRENDING_TV, TIME_WINDOW, MY_API_KEY);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvTrending.setVisibility(View.GONE);
                        rvTvTrending.setVisibility(View.VISIBLE);

                        int oldCount = tvTrendingResults.size();
                        tvTrendingResults.addAll(response.body().getResult());
                        tvTrendingAdapter.notifyItemChanged(oldCount, tvTrendingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                loadingTvTrending.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Trending TV Shows !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTrendingPerson(View view) {
        personTrendingAdapter = new PersonAdapter(personTrendingResults, getContext());

        loadingPersonTrending = view.findViewById(R.id.loadingPersonTrending);
        rvPersonTrending = view.findViewById(R.id.rvPersonTrending);

        getTrendingPerson();
        rvPersonTrending.setAdapter(personTrendingAdapter);
    }

    private void getTrendingPerson(){
        Call<PersonResponse> call = apiService.getTrendingPerson(TRENDING_PERSON, TIME_WINDOW, MY_API_KEY);
        call.enqueue(new Callback<PersonResponse>(){

            @Override
            public void onResponse(@NonNull Call<PersonResponse> call, @NonNull Response<PersonResponse> response) {
                if(response.body() != null){
                    if(response.body().getResults()!=null){
                        loadingPersonTrending.setVisibility(View.GONE);
                        rvPersonTrending.setVisibility(View.VISIBLE);

                        int oldCount = personTrendingResults.size();
                        personTrendingResults.addAll(response.body().getResults());
                        personTrendingAdapter.notifyItemChanged(oldCount, personTrendingResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PersonResponse> call, @NonNull Throwable t) {
                loadingPersonTrending.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch Trending Person !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}