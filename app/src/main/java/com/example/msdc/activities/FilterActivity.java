package com.example.msdc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.msdc.R;
import com.example.msdc.adapter.MovieAdapter;
import com.example.msdc.adapter.TVAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.api.TVResponse;
import com.example.msdc.api.TVResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FilterActivity extends AppCompatActivity {
    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";
    private ApiService apiService;

    private MovieAdapter filterMovieAdapter;
    private final List<MovieResult> filterMovieResults = new ArrayList<>();

    private TVAdapter filterTvAdapter;
    private final List<TVResult> filterTvResults = new ArrayList<>();

    private TextView noFilterResult;
    private RecyclerView rvFilter;

    private ProgressBar progressFilter;

    private int page = 1;
    private final int limit = 9;
    private String genre = null;
    private String region = null;
    private String sortType = null;

    private int mYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        String filterType = getIntent().getStringExtra("type");
        genre = getIntent().getStringExtra("genre");
        String year = getIntent().getStringExtra("year");
        region = getIntent().getStringExtra("region");
        sortType = getIntent().getStringExtra("sortBy");

        Toolbar filterToolbar = findViewById(R.id.filterToolbar);
        TextView filterResult = findViewById(R.id.textFilterResult);
        TextView filterGenre = findViewById(R.id.textFilterByGenre);
        TextView filterRegion = findViewById(R.id.textFilterByRegion);
        TextView filterYear = findViewById(R.id.textFilterByYear);
        TextView sortBy = findViewById(R.id.textSortType);
        noFilterResult = findViewById(R.id.textNoFilterResult);
        rvFilter = findViewById(R.id.rvFilterList);
        progressFilter = findViewById(R.id.loadingFilter);

        mYear = Integer.parseInt(year);

        filterToolbar.setTitle("Filter " + filterType + " Results :");
        setSupportActionBar(filterToolbar);

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);

        rvFilter.setLayoutManager(mLayoutManager);

        setFilterText(filterResult, filterType.toUpperCase());

        setRegionText(filterRegion, region);

        setReleaseYear(filterYear, mYear);

        setSortTypeText(sortBy, sortType);

        if(filterType.equalsIgnoreCase("movie")){
            setMovieGenreText(filterGenre, genre);

            filterMovieAdapter = new MovieAdapter(filterMovieResults, this);
            rvFilter.setAdapter(filterMovieAdapter);
            filterMovieData(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterMovieData(page);
                    }
                }
            });


        } else if(filterType.equalsIgnoreCase("tv")){
            setTvGenreText(filterGenre, genre);

            filterTvAdapter = new TVAdapter(filterTvResults, this);
            rvFilter.setAdapter(filterTvAdapter);
            filterTvData(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterTvData(page);
                    }
                }
            });
        }
    }

    private void filterMovieData(int page) {
        Call<MovieResponse> call = apiService.filterMovie(MY_API_KEY, LANGUAGE, page, limit, genre, mYear, region, sortType);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    int oldCount = filterMovieResults.size();
                    progressFilter.setVisibility(View.GONE);
                    rvFilter.setVisibility(View.VISIBLE);

                    filterMovieResults.addAll(response.body().getResult());
                    filterMovieAdapter.notifyItemRangeInserted(oldCount, filterMovieResults.size());
                } else if(filterMovieResults.isEmpty()){
                    progressFilter.setVisibility(View.GONE);
                    noFilterResult.setVisibility(View.VISIBLE);
                } else {
                    progressFilter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                progressFilter.setVisibility(View.GONE);
                Toast.makeText(FilterActivity.this, "Fail to Fetch Data !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterTvData(int page) {
        Call<TVResponse> call = apiService.filterTv(MY_API_KEY, LANGUAGE, page, limit, genre, mYear, region, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    int oldCount = filterTvResults.size();
                    progressFilter.setVisibility(View.GONE);
                    rvFilter.setVisibility(View.VISIBLE);

                    filterTvResults.addAll(response.body().getResult());
                    filterTvAdapter.notifyItemRangeInserted(oldCount, filterTvResults.size());
                } else if(filterTvResults.isEmpty()){
                    progressFilter.setVisibility(View.GONE);
                    noFilterResult.setVisibility(View.VISIBLE);
                } else {
                    progressFilter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                progressFilter.setVisibility(View.GONE);
                Toast.makeText(FilterActivity.this, "Fail to Fetch Data !!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFilterText(TextView tv, String type) {
        tv.setText(HtmlCompat.fromHtml("Filter Results For : <b>'" + type + "'</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setMovieGenreText(TextView tv, String genre) {
        tv.setText(HtmlCompat.fromHtml("Movie Genre : <b>" + genre + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setTvGenreText(TextView tv, String genre) {
        tv.setText(HtmlCompat.fromHtml("TV Shows Genre : <b>" + genre + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setReleaseYear(TextView tv, int year) {
        tv.setText(HtmlCompat.fromHtml("Release Year : <b>" + year + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setRegionText(TextView tv, String region) {
        tv.setText(HtmlCompat.fromHtml("Region : <b>" + region + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setSortTypeText(TextView tv, String sortType){
        tv.setText(HtmlCompat.fromHtml("Order By : <b>" + sortType + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }
}