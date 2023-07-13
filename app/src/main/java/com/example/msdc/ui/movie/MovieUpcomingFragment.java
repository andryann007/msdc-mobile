package com.example.msdc.ui.movie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.adapter.MovieVerticalAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.databinding.FragmentMovieUpcomingBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieUpcomingFragment extends Fragment {
    private ApiService apiService;
    private MovieVerticalAdapter movieUpcomingAdapter;
    private final List<MovieResult> movieUpcomingResults = new ArrayList<>();

    private RecyclerView rvMovieUpcoming;
    private ProgressBar loadingMovieUpcoming;
    private TextView textNoResult;

    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";
    private int page = 1;

    private FragmentMovieUpcomingBinding binding;

    public MovieUpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentMovieUpcomingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setUpcomingMovies(root);

        return root;
    }

    private void setUpcomingMovies(View view) {
        rvMovieUpcoming = view.findViewById(R.id.rvMovieUpcomingList);
        loadingMovieUpcoming = view.findViewById(R.id.loadingMovieUpcomingList);
        textNoResult = view.findViewById(R.id.textNoMovieUpcomingResult);

        movieUpcomingAdapter = new MovieVerticalAdapter(movieUpcomingResults, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        rvMovieUpcoming.setLayoutManager(gridLayoutManager);
        rvMovieUpcoming.setAdapter(movieUpcomingAdapter);
        rvMovieUpcoming.setHasFixedSize(true);
        rvMovieUpcoming.setItemViewCacheSize(9);
        rvMovieUpcoming.setDrawingCacheEnabled(true);
        rvMovieUpcoming.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        getUpcomingMovies(page);

        rvMovieUpcoming.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvMovieUpcoming.canScrollVertically(1)){
                    page++;
                    getUpcomingMovies(page);
                }
            }
        });
    }

    private void getUpcomingMovies(int page){
        final int limit = 15;

        Call<MovieResponse> call = apiService.getUpcomingMovies(MY_API_KEY, LANGUAGE, page, limit);
        call.enqueue(new Callback<MovieResponse>(){

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        loadingMovieUpcoming.setVisibility(View.GONE);
                        rvMovieUpcoming.setVisibility(View.VISIBLE);

                        int oldCount = movieUpcomingResults.size();
                        movieUpcomingResults.addAll(response.body().getResult());
                        movieUpcomingAdapter.notifyItemRangeInserted(oldCount, movieUpcomingResults.size());
                    } else {
                        loadingMovieUpcoming.setVisibility(View.GONE);
                        textNoResult.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadingMovieUpcoming.setVisibility(View.GONE);
                Toast.makeText(getContext(), t.getMessage() + " cause : " + t.getCause(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}