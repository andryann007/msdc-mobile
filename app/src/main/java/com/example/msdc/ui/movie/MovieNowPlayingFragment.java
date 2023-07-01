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
import com.example.msdc.adapter.MovieGridAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.databinding.FragmentMovieNowPlayingBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieNowPlayingFragment extends Fragment {
    private ApiService apiService;
    private ProgressBar loadingMovieNowPlaying;
    private MovieGridAdapter movieNowPlayingAdapter;
    private final List<MovieResult> movieNowPlayingResults = new ArrayList<>();

    private RecyclerView rvMovieNowPlaying;

    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";
    private int page = 1;

    private FragmentMovieNowPlayingBinding binding;

    public MovieNowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentMovieNowPlayingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setNowPlayingMovies(root);

        return root;
    }

    private void setNowPlayingMovies(View view) {
        TextView textTitle = view.findViewById(R.id.textMovieVertical);
        String title = "Now Playing Movies";
        textTitle.setText(title);

        rvMovieNowPlaying = view.findViewById(R.id.rvMovieVertical);
        movieNowPlayingAdapter = new MovieGridAdapter(movieNowPlayingResults, getContext());
        loadingMovieNowPlaying = view.findViewById(R.id.loadingMovieVertical);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        rvMovieNowPlaying.setLayoutManager(gridLayoutManager);
        rvMovieNowPlaying.setAdapter(movieNowPlayingAdapter);
        getNowPlayingMovies(page);

        rvMovieNowPlaying.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvMovieNowPlaying.canScrollVertically(1)){
                    page++;
                    getNowPlayingMovies(page);
                }
            }
        });
    }

    private void getNowPlayingMovies(int page){
        int limit = 9;
        Call<MovieResponse> call = apiService.getNowPlayingMovies(MY_API_KEY, LANGUAGE, page, limit);
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}