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
    private final int limit = 15;
    private String genre = null;
    private String region = null;
    private String sortType = null;
    private String genreName = null;
    private String regionName = null;
    private String sortName = null;

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

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);

        rvFilter.setLayoutManager(mLayoutManager);

        setGenreName(genre);
        setRegionName(region);
        setSortName(sortType);

        setFilterText(filterResult, filterType.toUpperCase());
        setRegionText(filterRegion, regionName);
        setReleaseYear(filterYear, mYear);
        setSortTypeText(sortBy, sortName);

        if(filterType.equalsIgnoreCase("movie")){
            filterToolbar.setTitle("Filter Movies Results :");
            setSupportActionBar(filterToolbar);

            setMovieGenreText(filterGenre, genreName);

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
            filterToolbar.setTitle("Filter TV Shows Results :");
            setSupportActionBar(filterToolbar);

            setTvGenreText(filterGenre, genreName);

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
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterMovieResults.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterMovieResults.addAll(response.body().getResult());
                        filterMovieAdapter.notifyItemRangeInserted(oldCount, filterMovieResults.size());
                    } else {
                        progressFilter.setVisibility(View.GONE);
                        noFilterResult.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                progressFilter.setVisibility(View.GONE);
                Toast.makeText(FilterActivity.this, t.getMessage() + " cause : "
                        + t.getCause(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterTvData(int page) {
        Call<TVResponse> call = apiService.filterTv(MY_API_KEY, LANGUAGE, page, limit, genre, mYear, region, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvResults.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvResults.addAll(response.body().getResult());
                        filterTvAdapter.notifyItemRangeInserted(oldCount, filterTvResults.size());
                    } else {
                        progressFilter.setVisibility(View.GONE);
                        noFilterResult.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                progressFilter.setVisibility(View.GONE);
                Toast.makeText(FilterActivity.this, t.getMessage() + " cause : "
                                + t.getCause(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFilterText(TextView tv, String type) {
        tv.setText(HtmlCompat.fromHtml("Filter Results For : <b>" + type + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
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

    public void setGenreName(String genreId){
        switch(genreId){
            case "28" :
                genreName = "action";
                break;

            case "12" :
                genreName = "adventure";
                break;

            case "16" :
                genreName = "animation";
                break;

            case "35" :
                genreName = "comedy";
                break;

            case "80" :
                genreName = "crime";
                break;

            case "99" :
                genreName = "documentary";
                break;

            case "18" :
                genreName = "drama";
                break;

            case "10751" :
                genreName = "family";
                break;

            case "14" :
                genreName = "fantasy";
                break;

            case "36" :
                genreName = "history";
                break;

            case "27" :
                genreName = "horror";
                break;

            case "10402" :
                genreName = "music";
                break;

            case "9648" :
                genreName = "mystery";
                break;

            case "10749" :
                genreName = "romance";
                break;

            case "878" :
                genreName = "science fiction";
                break;

            case "10770" :
                genreName = "tv movie";
                break;

            case "53" :
                genreName = "thriller";
                break;

            case "10752" :
                genreName = "war";
                break;

            case "37" :
                genreName = "western";
                break;
        }
    }

    public void setRegionName(String regionCode){
        switch(regionCode){
            case "AU" :
                regionName = "australia";
                break;

            case "CN" :
                regionName = "china";
                break;

            case "FR" :
                regionName = "france";
                break;

            case "DE" :
                regionName = "germany";
                break;

            case "HK" :
                regionName = "hong kong";
                break;

            case "IN" :
                regionName = "india";
                break;

            case "ID" :
                regionName = "indonesia";
                break;

            case "JP" :
                regionName = "japan";
                break;

            case "RU" :
                regionName = "russia";
                break;

            case "KR" :
                regionName = "south korea";
                break;

            case "TW" :
                regionName = "taiwan";
                break;

            case "TH" :
                regionName = "thailand";
                break;

            case "UK" :
                regionName = "united kingdom";
                break;

            case "US" :
                regionName = "united states";
                break;
        }
    }

    public void setSortName(String sortCode){
        switch(sortCode) {
            case "popularity.asc" :
                sortName = "popularity (ascending)";
                break;

            case "popularity.desc" :
                sortName = "popularity (descending)";
                break;

            case "revenue.asc" :
                sortName = "revenue (ascending)";
                break;

            case "revenue.desc" :
                sortName = "revenue (descending)";
                break;

            case "primary_release_date.asc" :
                sortName = "release date (ascending)";
                break;

            case "primary_release_date.desc" :
                sortName = "release date (descending)";
                break;

            case "vote_average.asc" :
                sortName = "vote average (ascending)";
                break;

            case "vote_average.desc" :
                sortName = "vote average (descending)";
                break;

            case "vote_count.asc" :
                sortName = "vote count (ascending)";
                break;

            case "vote_count.desc" :
                sortName = "vote count (descending)";
                break;
        }
    }
}