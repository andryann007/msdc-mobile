package com.example.msdc.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.adapter.MovieSearchAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.GenreResult;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.databinding.ActivityDiscoverBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DiscoverActivity extends AppCompatActivity {

    private ApiService apiService;
    private ActivityDiscoverBinding binding;
    private MovieSearchAdapter movieDiscoverAdapter;
    private final List<MovieResult> discoverMovieResult = new ArrayList<>();

    private int genreId;
    private String genreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiscoverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        genreId = getIntent().getIntExtra("genre_id", 0);
        genreName = getIntent().getStringExtra("genre_name");

        toolbar.setTitle(HtmlCompat.fromHtml("Discover Movies / TV by <b>" + genreName + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));

        TextView tvTitleGenre = findViewById(R.id.titleDiscover);
        tvTitleGenre.setText("Data Sort By " + genreName);

        RecyclerView rvDiscoverMovieByGenre = findViewById(R.id.rvDiscover);
        movieDiscoverAdapter = new MovieSearchAdapter(discoverMovieResult, this);
        rvDiscoverMovieByGenre.setAdapter(movieDiscoverAdapter);
        discoverMovieByGenres();
    }

    private void discoverMovieByGenres(){
        Call<MovieResponse> call = apiService.discoverMovie(MainActivity.MYAPI_KEY, MainActivity.LANGUAGE, String.valueOf(genreId));
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    int oldCount = discoverMovieResult.size();
                    discoverMovieResult.addAll(response.body().getResult());
                    movieDiscoverAdapter.notifyItemRangeInserted(oldCount, discoverMovieResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}