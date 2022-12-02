package com.example.msdc.ui.movie;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.msdc.R;
import com.example.msdc.activities.SearchActivity;
import com.example.msdc.adapter.MovieAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.MovieRespon;
import com.example.msdc.api.MovieResult;
import com.example.msdc.databinding.FragmentMovieBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieFragment extends Fragment {

    private ApiService apiService;
    private RecyclerView rvMoviePopular, rvMovieNowPlaying, rvMovieTopRated, rvMovieUpcoming;
    private ProgressBar loadingMoviePopular, loadingMovieNowPlaying, loadingMovieTopRated, loadingMovieUpcoming;
    private MovieAdapter moviePopularAdapter, movieNowPlayingAdapter, movieTopRatedAdapter, movieUpcomingAdapter;
    private final List<MovieResult> moviePopularResults = new ArrayList<>();
    private final List<MovieResult> movieNowPlayingResults = new ArrayList<>();
    private final List<MovieResult> movieTopRatedResults = new ArrayList<>();
    private final List<MovieResult> movieUpcomingResults = new ArrayList<>();
    private int currentPageMoviePopular = 1;
    private int currentPageMovieNowPlaying = 1;
    private int currentPageMovieTopRated = 1;
    private int currentPageUpcomingMovie = 1;

    private int totalPagesMoviePopular = 1;
    private int totalPagesMovieNowPlaying = 1;
    private int totalPagesMovieTopRated = 1;
    private int totalPagesUpcomingMovie = 1;

    public static final String MYAPI_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";

    private String searchType = null;
    private FragmentMovieBinding binding;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentMovieBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setPopularMovies(root);
        setNowPlayingMovies(root);
        setUpcomingMovies(root);
        setTopRatedMovies(root);

        return root;
    }

    private void dialogSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
            Toast.makeText(getContext(),"Tidak ada inputan!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(searchType == null){
            Toast.makeText(getContext(),"Harap pilih tipe search!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(getContext(), SearchActivity.class);
        i.putExtra("tipe", searchType);
        i.putExtra("searchFor", query);
        startActivity(i);
    }

    private void setTopRatedMovies(View view) {
        rvMovieTopRated = view.findViewById(R.id.rvMovieTopRated);
        movieTopRatedAdapter = new MovieAdapter(movieTopRatedResults, getContext());
        loadingMovieTopRated = view.findViewById(R.id.loadingMovieTopRated);

        getTopRatedMovies();
        rvMovieTopRated.setAdapter(movieTopRatedAdapter);
    }

    private void getTopRatedMovies(){
        Call<MovieRespon> call = apiService.getTopRatedMovies(MYAPI_KEY, LANGUAGE, currentPageMovieTopRated);
        call.enqueue(new Callback<MovieRespon>(){

            @Override
            public void onResponse(@NonNull Call<MovieRespon> call, @NonNull Response<MovieRespon> response) {
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
            public void onFailure(@NonNull Call<MovieRespon> call, @NonNull Throwable t) {

            }
        });
    }

    private void setUpcomingMovies(View view) {
        rvMovieUpcoming = view.findViewById(R.id.rvUpcomingMovie);
        movieUpcomingAdapter = new MovieAdapter(movieUpcomingResults, getContext());
        loadingMovieUpcoming = view.findViewById(R.id.loadingUpcomingMovie);

        getUpcomingMovies();
        rvMovieUpcoming.setAdapter(movieUpcomingAdapter);
    }

    private void getUpcomingMovies(){
        Call<MovieRespon> call = apiService.getUpcomingMovies(MYAPI_KEY, LANGUAGE, currentPageMoviePopular);
        call.enqueue(new Callback<MovieRespon>(){

            @Override
            public void onResponse(@NonNull Call<MovieRespon> call, @NonNull Response<MovieRespon> response) {
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
            public void onFailure(@NonNull Call<MovieRespon> call, @NonNull Throwable t) {

            }
        });
    }

    private void setNowPlayingMovies(View view) {
        rvMovieNowPlaying = view.findViewById(R.id.rvMovieNowPlaying);
        movieNowPlayingAdapter = new MovieAdapter(movieNowPlayingResults, getContext());
        loadingMovieNowPlaying = view.findViewById(R.id.loadingMovieNowPlaying);

        getNowPlayingMovies();
        rvMovieNowPlaying.setAdapter(movieNowPlayingAdapter);
    }

    private void getNowPlayingMovies(){
        Call<MovieRespon> call = apiService.getNowPlayingMovies(MYAPI_KEY, LANGUAGE, currentPageMoviePopular);
        call.enqueue(new Callback<MovieRespon>(){

            @Override
            public void onResponse(@NonNull Call<MovieRespon> call, @NonNull Response<MovieRespon> response) {
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
            public void onFailure(@NonNull Call<MovieRespon> call, @NonNull Throwable t) {

            }
        });
    }

    private void setPopularMovies(View view) {
        rvMoviePopular = view.findViewById(R.id.rvMoviePopular);
        moviePopularAdapter = new MovieAdapter(moviePopularResults, getContext());
        loadingMoviePopular = view.findViewById(R.id.loadingMoviePopular);

        getPopularMovies();
        rvMoviePopular.setAdapter(moviePopularAdapter);
    }

    private void getPopularMovies(){
        Call<MovieRespon> call = apiService.getPopularMovies(MYAPI_KEY, LANGUAGE, currentPageMoviePopular);
        call.enqueue(new Callback<MovieRespon>(){

            @Override
            public void onResponse(@NonNull Call<MovieRespon> call, @NonNull Response<MovieRespon> response) {
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
            public void onFailure(@NonNull Call<MovieRespon> call, @NonNull Throwable t) {

            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}