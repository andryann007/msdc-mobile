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
import com.example.msdc.adapter.MovieVerticalAdapter;
import com.example.msdc.adapter.TVVerticalAdapter;
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

    private MovieVerticalAdapter filterMovieAdapter, filterMovieGenreAdapter, filterMovieKeywordAdapter,
            filterMovieYearAdapter, filterMovieRegionAdapter, filterMovieGenreYearAdapter,
            filterMovieGenreRegionAdapter, filterMovieYearRegionAdapter;
    private final List<MovieResult> filterMovieResults = new ArrayList<>();
    private final List<MovieResult> filterMovieGenre = new ArrayList<>();
    private final List<MovieResult> filterMovieKeyword = new ArrayList<>();
    private final List<MovieResult> filterMovieYear = new ArrayList<>();
    private final List<MovieResult> filterMovieRegion = new ArrayList<>();
    private final List<MovieResult> filterMovieGenreAndYear = new ArrayList<>();
    private final List<MovieResult> filterMovieGenreAndRegion = new ArrayList<>();
    private final List<MovieResult> filterMovieYearAndRegion = new ArrayList<>();

    private TVVerticalAdapter filterTVAdapter, filterTvGenreAdapter, filterTvKeywordAdapter,
            filterTvYearAdapter, filterTvRegionAdapter, filterTvGenreYearAdapter,
            filterTvGenreRegionAdapter, filterTvYearRegionAdapter;
    private final List<TVResult> filterTvResults = new ArrayList<>();
    private final List<TVResult> filterTvGenre = new ArrayList<>();
    private final List<TVResult> filterTvKeyword= new ArrayList<>();
    private final List<TVResult> filterTvYear = new ArrayList<>();
    private final List<TVResult> filterTvRegion = new ArrayList<>();
    private final List<TVResult> filterTvGenreAndYear = new ArrayList<>();
    private final List<TVResult> filterTvGenreAndRegion = new ArrayList<>();
    private final List<TVResult> filterTvYearAndRegion = new ArrayList<>();

    private TextView noFilterResult;
    private RecyclerView rvFilter;

    private ProgressBar progressFilter;

    private int page = 1;
    private final int limit = 15;
    private int genre = 0;
    private int keyword = 0;
    private int year = 0;
    private String region = null;
    private String sortType = null;
    private String genreName = null;
    private String regionName = null;
    private String sortName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        String filterType = getIntent().getStringExtra("type");

        Toolbar filterToolbar = findViewById(R.id.filterToolbar);
        TextView filterResult = findViewById(R.id.textFilterResult);
        TextView filterGenre = findViewById(R.id.textFilterByGenre);
        TextView filterRegion = findViewById(R.id.textFilterByRegion);
        TextView filterYear = findViewById(R.id.textFilterByYear);
        TextView sortBy = findViewById(R.id.textSortType);

        noFilterResult = findViewById(R.id.textNoFilterResult);
        rvFilter = findViewById(R.id.rvFilterList);
        progressFilter = findViewById(R.id.loadingFilter);

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);

        rvFilter.setLayoutManager(mLayoutManager);

        if(filterType.equalsIgnoreCase("movie")){
            genre = getIntent().getIntExtra("genre", 0);
            year = getIntent().getIntExtra("year", 0);
            region = getIntent().getStringExtra("region");
            sortType = getIntent().getStringExtra("sortBy");

            setGenreName(genre);
            setRegionName(region);
            setSortName(sortType);

            setFilterText(filterResult, filterType.toUpperCase());
            setRegionText(filterRegion, regionName);
            setReleaseYear(filterYear, year);
            setSortTypeText(sortBy, sortName);

            filterToolbar.setTitle("Filter Movies Results :");
            setSupportActionBar(filterToolbar);

            setMovieGenreText(filterGenre, genreName);

            filterMovieAdapter = new MovieVerticalAdapter(filterMovieResults, this);
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
            genre = getIntent().getIntExtra("genre", 0);
            year = getIntent().getIntExtra("year", 0);
            region = getIntent().getStringExtra("region");
            sortType = getIntent().getStringExtra("sortBy");

            setGenreName(genre);
            setRegionName(region);
            setSortName(sortType);

            setFilterText(filterResult, filterType.toUpperCase());
            setRegionText(filterRegion, regionName);
            setReleaseYear(filterYear, year);
            setSortTypeText(sortBy, sortName);

            filterToolbar.setTitle("Filter TV Shows Results :");
            setSupportActionBar(filterToolbar);

            setTvGenreText(filterGenre, genreName);

            filterTVAdapter = new TVVerticalAdapter(filterTvResults, this);
            rvFilter.setAdapter(filterTVAdapter);
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
        } else if(filterType.equalsIgnoreCase("filter_movie_genre")){
            genre = getIntent().getIntExtra("genre", 0);
            sortType = getIntent().getStringExtra("sortBy");

            setGenreName(genre);
            setSortName(sortType);

            setFilterText(filterResult, "Filter Movie Genre");
            setRegionText(filterRegion, "-");
            setReleaseYear(filterYear, 0);
            setSortTypeText(sortBy, sortName);

            filterToolbar.setTitle("Filter Movie Genre Results :");
            setSupportActionBar(filterToolbar);

            setMovieGenreText(filterGenre, genreName);

            filterMovieGenreAdapter = new MovieVerticalAdapter(filterMovieGenre, this);
            rvFilter.setAdapter(filterMovieGenreAdapter);
            filterMovieGenre(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterMovieGenre(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_tv_genre")){
            genre = getIntent().getIntExtra("genre", 0);
            sortType = getIntent().getStringExtra("sortBy");

            setGenreName(genre);
            setSortName(sortType);

            setFilterText(filterResult, "Filter TV Shows Genre");
            setRegionText(filterRegion, "-");
            setReleaseYear(filterYear, 0);
            setSortTypeText(sortBy, sortName);

            filterToolbar.setTitle("Filter TV Shows Genre Results :");
            setSupportActionBar(filterToolbar);

            setTvGenreText(filterGenre, genreName);

            filterTvGenreAdapter = new TVVerticalAdapter(filterTvGenre, this);
            rvFilter.setAdapter(filterTvGenreAdapter);
            filterTvGenre(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterTvGenre(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_movie_keyword")){
            keyword = getIntent().getIntExtra("keyword", 0);
            String keywordName = getIntent().getStringExtra("keyword_name");
            sortType = getIntent().getStringExtra("sortBy");

            setSortName(sortType);

            setFilterText(filterResult, "Filter Movie Keyword");
            setRegionText(filterRegion, "-");
            setReleaseYear(filterYear, 0);
            setSortTypeText(sortBy, sortName);

            filterToolbar.setTitle("Filter Movie Keyword Results :");
            setSupportActionBar(filterToolbar);

            setMovieKeywordText(filterGenre, keywordName);

            filterMovieKeywordAdapter = new MovieVerticalAdapter(filterMovieKeyword, this);
            rvFilter.setAdapter(filterMovieKeywordAdapter);
            filterMovieKeyword(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterMovieKeyword(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_movie_year")){
            year = getIntent().getIntExtra("year", 0);
            sortType = getIntent().getStringExtra("sortBy");

            setSortName(sortType);

            setFilterText(filterResult, "Filter Movie Year");
            setReleaseYear(filterYear, year);
            setRegionText(filterRegion, "-");
            setSortTypeText(sortBy, sortName);

            setMovieGenreText(filterGenre, "-");

            filterToolbar.setTitle("Filter Movie Year Results :");
            setSupportActionBar(filterToolbar);

            filterMovieYearAdapter = new MovieVerticalAdapter(filterMovieYear, this);
            rvFilter.setAdapter(filterMovieYearAdapter);
            filterMovieYear(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterMovieYear(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_movie_region")){
            region = getIntent().getStringExtra("region");
            sortType = getIntent().getStringExtra("sortBy");

            setRegionName(region);
            setSortName(sortType);

            setFilterText(filterResult, "Filter Movie Region");
            setReleaseYear(filterYear, 0);
            setRegionText(filterRegion, regionName);
            setSortTypeText(sortBy, sortName);

            setMovieGenreText(filterGenre, "-");

            filterToolbar.setTitle("Filter Movie Region Results :");
            setSupportActionBar(filterToolbar);

            filterMovieRegionAdapter = new MovieVerticalAdapter(filterMovieRegion, this);
            rvFilter.setAdapter(filterMovieRegionAdapter);
            filterMovieRegion(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterMovieRegion(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_movie_genre_and_year")){
            year = getIntent().getIntExtra("year", 0);
            genre = getIntent().getIntExtra("genre", 0);
            sortType = getIntent().getStringExtra("sortBy");

            setGenreName(genre);
            setSortName(sortType);

            setFilterText(filterResult, "Filter Movie Genre & Year");
            setReleaseYear(filterYear, year);
            setRegionText(filterRegion, "-");
            setSortTypeText(sortBy, sortName);

            setTvGenreText(filterGenre, genreName);

            filterToolbar.setTitle("Filter Movie Genre & Year Results :");
            setSupportActionBar(filterToolbar);

            filterMovieGenreYearAdapter = new MovieVerticalAdapter(filterMovieGenreAndYear, this);
            rvFilter.setAdapter(filterMovieGenreYearAdapter);
            filterMovieGenreAndYear(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterMovieGenreAndYear(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_movie_genre_and_region")){
            genre = getIntent().getIntExtra("genre", 0);
            region = getIntent().getStringExtra("region");
            sortType = getIntent().getStringExtra("sortBy");

            setGenreName(genre);
            setRegionName(region);
            setSortName(sortType);

            setFilterText(filterResult, "Filter Movie Genre & Region");
            setRegionText(filterRegion, regionName);
            setReleaseYear(filterYear, 0);
            setSortTypeText(sortBy, sortName);

            setTvGenreText(filterGenre, genreName);

            filterToolbar.setTitle("Filter Movie Genre & Region Results :");
            setSupportActionBar(filterToolbar);

            filterMovieGenreRegionAdapter = new MovieVerticalAdapter(filterMovieGenreAndRegion, this);
            rvFilter.setAdapter(filterMovieGenreRegionAdapter);
            filterMovieGenreAndRegion(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterMovieGenreAndRegion(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_movie_year_and_region")){
            year = getIntent().getIntExtra("year", 0);
            region = getIntent().getStringExtra("region");
            sortType = getIntent().getStringExtra("sortBy");

            setRegionName(region);
            setSortName(sortType);

            setFilterText(filterResult, "Filter Movie Year & Region");
            setReleaseYear(filterYear, year);
            setRegionText(filterRegion, regionName);
            setSortTypeText(sortBy, sortName);

            setMovieGenreText(filterGenre, "-");

            filterToolbar.setTitle("Filter Movie Year & Region Results :");
            setSupportActionBar(filterToolbar);

            filterMovieYearRegionAdapter = new MovieVerticalAdapter(filterMovieYearAndRegion, this);
            rvFilter.setAdapter(filterMovieYearRegionAdapter);
            filterMovieYearAndRegion(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterMovieYearAndRegion(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_tv_keyword")){
            keyword = getIntent().getIntExtra("keyword", 0);
            String keywordName = getIntent().getStringExtra("keyword_name");
            sortType = getIntent().getStringExtra("sortBy");

            setSortName(sortType);

            setFilterText(filterResult, "Filter TV Shows Keyword");
            setRegionText(filterRegion, "-");
            setReleaseYear(filterYear, 0);
            setSortTypeText(sortBy, sortName);

            filterToolbar.setTitle("Filter TV Shows Keyword Results :");
            setSupportActionBar(filterToolbar);

            setTvKeywordText(filterGenre, keywordName);

            filterTvKeywordAdapter = new TVVerticalAdapter(filterTvKeyword, this);
            rvFilter.setAdapter(filterTvKeywordAdapter);
            filterTvKeyword(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterTvKeyword(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_tv_year")){
            year = getIntent().getIntExtra("year", 0);
            sortType = getIntent().getStringExtra("sortBy");

            setSortName(sortType);

            setFilterText(filterResult, "Filter TV Shows Year");
            setReleaseYear(filterYear, year);
            setRegionText(filterRegion, "-");
            setSortTypeText(sortBy, sortName);

            setMovieGenreText(filterGenre, "-");

            filterToolbar.setTitle("Filter TV Shows Year Results :");
            setSupportActionBar(filterToolbar);

            filterTvYearAdapter = new TVVerticalAdapter(filterTvYear, this);
            rvFilter.setAdapter(filterTvYearAdapter);
            filterTvYear(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterTvYear(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_tv_region")){
            region = getIntent().getStringExtra("region");
            sortType = getIntent().getStringExtra("sortBy");

            setRegionName(region);
            setSortName(sortType);

            setFilterText(filterResult, "Filter TV Shows Region");
            setReleaseYear(filterYear, 0);
            setRegionText(filterRegion, regionName);
            setSortTypeText(sortBy, sortName);

            setTvGenreText(filterGenre, "-");

            filterToolbar.setTitle("Filter TV Shows Region Results :");
            setSupportActionBar(filterToolbar);

            filterTvRegionAdapter = new TVVerticalAdapter(filterTvRegion, this);
            rvFilter.setAdapter(filterTvRegionAdapter);
            filterTvRegion(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterTvRegion(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_tv_genre_and_year")){
            year = getIntent().getIntExtra("year", 0);
            genre = getIntent().getIntExtra("genre", 0);
            sortType = getIntent().getStringExtra("sortBy");

            setGenreName(genre);
            setSortName(sortType);

            setFilterText(filterResult, "Filter TV Shows Genre & Year");
            setReleaseYear(filterYear, year);
            setRegionText(filterRegion, "-");
            setSortTypeText(sortBy, sortName);

            setTvGenreText(filterGenre, genreName);

            filterToolbar.setTitle("Filter TV Shows Genre & Year Results :");
            setSupportActionBar(filterToolbar);

            filterTvGenreYearAdapter = new TVVerticalAdapter(filterTvGenreAndYear, this);
            rvFilter.setAdapter(filterTvGenreYearAdapter);
            filterTvGenreAndYear(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterTvGenreAndYear(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_tv_genre_and_region")){
            genre = getIntent().getIntExtra("genre", 0);
            region = getIntent().getStringExtra("region");
            sortType = getIntent().getStringExtra("sortBy");

            setGenreName(genre);
            setRegionName(region);
            setSortName(sortType);

            setFilterText(filterResult, "Filter TV Shows Genre & Region");
            setRegionText(filterRegion, regionName);
            setReleaseYear(filterYear, 0);
            setSortTypeText(sortBy, sortName);

            setTvGenreText(filterGenre, genreName);

            filterToolbar.setTitle("Filter TV Shows Genre & Region Results :");
            setSupportActionBar(filterToolbar);

            filterTvGenreRegionAdapter = new TVVerticalAdapter(filterTvGenreAndRegion, this);
            rvFilter.setAdapter(filterTvGenreRegionAdapter);
            filterTvGenreAndRegion(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterTvGenreAndRegion(page);
                    }
                }
            });
        } else if(filterType.equalsIgnoreCase("filter_tv_year_and_region")){
            year = getIntent().getIntExtra("year", 0);
            region = getIntent().getStringExtra("region");
            sortType = getIntent().getStringExtra("sortBy");

            setRegionName(region);
            setSortName(sortType);

            setFilterText(filterResult, "Filter TV Shows Year & Region");
            setReleaseYear(filterYear, year);
            setRegionText(filterRegion, regionName);
            setSortTypeText(sortBy, sortName);

            setTvGenreText(filterGenre, "-");

            filterToolbar.setTitle("Filter TV Shows Year & Region Results :");
            setSupportActionBar(filterToolbar);

            filterTvYearRegionAdapter = new TVVerticalAdapter(filterTvYearAndRegion, this);
            rvFilter.setAdapter(filterTvYearRegionAdapter);
            filterTvYearAndRegion(page);

            rvFilter.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if(!recyclerView.canScrollVertically(1)){
                        page++;
                        filterTvYearAndRegion(page);
                    }
                }
            });
        }
    }

    private void filterMovieData(int page) {
        Call<MovieResponse> call = apiService.filterMovie(MY_API_KEY, LANGUAGE, page, limit, genre, year, region, sortType);
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

    private void filterMovieGenre(int page){
        Call<MovieResponse> call = apiService.filterMovieGenre(MY_API_KEY, LANGUAGE, page, limit, genre, sortType);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterMovieGenre.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterMovieGenre.addAll(response.body().getResult());
                        filterMovieGenreAdapter.notifyItemRangeInserted(oldCount, filterMovieGenre.size());
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

    private void filterMovieKeyword(int page){
        Call<MovieResponse> call = apiService.filterMovieKeyword(MY_API_KEY, LANGUAGE, page, limit, keyword, sortType);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterMovieKeyword.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterMovieKeyword.addAll(response.body().getResult());
                        filterMovieKeywordAdapter.notifyItemRangeInserted(oldCount, filterMovieKeyword.size());
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

    private void filterMovieYear(int page){
        Call<MovieResponse> call = apiService.filterMovieYear(MY_API_KEY, LANGUAGE, page, limit, year, sortType);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterMovieYear.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterMovieYear.addAll(response.body().getResult());
                        filterMovieYearAdapter.notifyItemRangeInserted(oldCount, filterMovieYear.size());
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

    private void filterMovieRegion(int page){
        Call<MovieResponse> call = apiService.filterMovieRegion(MY_API_KEY, LANGUAGE, page, limit, region, sortType);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterMovieRegion.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterMovieRegion.addAll(response.body().getResult());
                        filterMovieRegionAdapter.notifyItemRangeInserted(oldCount, filterMovieRegion.size());
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

    private void filterMovieGenreAndYear(int page){
        Call<MovieResponse> call = apiService.filterMovieGenreAndYear(MY_API_KEY, LANGUAGE, page, limit, genre, year, sortType);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterMovieGenreAndYear.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterMovieGenreAndYear.addAll(response.body().getResult());
                        filterMovieGenreYearAdapter.notifyItemRangeInserted(oldCount, filterMovieGenreAndYear.size());
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

    private void filterMovieGenreAndRegion(int page){
        Call<MovieResponse> call = apiService.filterMovieGenreAndRegion(MY_API_KEY, LANGUAGE, page,
                limit, genre, region, sortType);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterMovieGenreAndRegion.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterMovieGenreAndRegion.addAll(response.body().getResult());
                        filterMovieGenreRegionAdapter.notifyItemRangeInserted(oldCount, filterMovieGenreAndRegion.size());
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

    private void filterMovieYearAndRegion(int page){
        Call<MovieResponse> call = apiService.filterMovieYearAndRegion(MY_API_KEY, LANGUAGE, page,
                limit, year, region, sortType);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterMovieYearAndRegion.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterMovieYearAndRegion.addAll(response.body().getResult());
                        filterMovieYearRegionAdapter.notifyItemRangeInserted(oldCount, filterMovieYearAndRegion.size());
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
        Call<TVResponse> call = apiService.filterTv(MY_API_KEY, LANGUAGE, page, limit, genre, year, region, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvResults.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvResults.addAll(response.body().getResult());
                        filterTVAdapter.notifyItemRangeInserted(oldCount, filterTvResults.size());
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

    private void filterTvGenre(int page){
        Call<TVResponse> call = apiService.filterTvGenre(MY_API_KEY, LANGUAGE, page, limit, genre, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvGenre.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvGenre.addAll(response.body().getResult());
                        filterTvGenreAdapter.notifyItemRangeInserted(oldCount, filterTvGenre.size());
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

    private void filterTvKeyword(int page){
        Call<TVResponse> call = apiService.filterTvKeyword(MY_API_KEY, LANGUAGE, page, limit, keyword, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvKeyword.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvKeyword.addAll(response.body().getResult());
                        filterTvKeywordAdapter.notifyItemRangeInserted(oldCount, filterTvKeyword.size());
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

    private void filterTvYear(int page){
        Call<TVResponse> call = apiService.filterTvYear(MY_API_KEY, LANGUAGE, page, limit, year, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvYear.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvYear.addAll(response.body().getResult());
                        filterTvYearAdapter.notifyItemRangeInserted(oldCount, filterTvYear.size());
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

    private void filterTvRegion(int page){
        Call<TVResponse> call = apiService.filterTvRegion(MY_API_KEY, LANGUAGE, page, limit, region, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvRegion.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvRegion.addAll(response.body().getResult());
                        filterTvRegionAdapter.notifyItemRangeInserted(oldCount, filterTvRegion.size());
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

    private void filterTvGenreAndYear(int page){
        Call<TVResponse> call = apiService.filterTvGenreAndYear(MY_API_KEY, LANGUAGE, page, limit, genre, year, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvGenreAndYear.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvGenreAndYear.addAll(response.body().getResult());
                        filterTvGenreYearAdapter.notifyItemRangeInserted(oldCount, filterTvGenreAndYear.size());
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

    private void filterTvGenreAndRegion(int page){
        Call<TVResponse> call = apiService.filterTvGenreAndRegion(MY_API_KEY, LANGUAGE, page,
                limit, genre, region, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvGenreAndRegion.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvGenreAndRegion.addAll(response.body().getResult());
                        filterTvGenreRegionAdapter.notifyItemRangeInserted(oldCount, filterTvGenreAndRegion.size());
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

    private void filterTvYearAndRegion(int page){
        Call<TVResponse> call = apiService.filterTvYearAndRegion(MY_API_KEY, LANGUAGE, page,
                limit, year, region, sortType);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(!response.body().getResult().isEmpty()){
                        int oldCount = filterTvYearAndRegion.size();
                        progressFilter.setVisibility(View.GONE);
                        rvFilter.setVisibility(View.VISIBLE);

                        filterTvYearAndRegion.addAll(response.body().getResult());
                        filterTvYearRegionAdapter.notifyItemRangeInserted(oldCount, filterTvYearAndRegion.size());
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

    private void setMovieKeywordText(TextView tv, String keyword) {
        tv.setText(HtmlCompat.fromHtml("Movie Keyword : <b>" + keyword + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setTvGenreText(TextView tv, String genre) {
        tv.setText(HtmlCompat.fromHtml("TV Shows Genre : <b>" + genre + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setTvKeywordText(TextView tv, String keyword){
        tv.setText(HtmlCompat.fromHtml("TV Shows Keyword : <b>" + keyword + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
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

    public void setGenreName(int genreId){
        switch(genreId){
            case 28 :
                genreName = "action";
                break;

            case 12 :
                genreName = "adventure";
                break;

            case 16 :
                genreName = "animation";
                break;

            case 35 :
                genreName = "comedy";
                break;

            case 80 :
                genreName = "crime";
                break;

            case 99 :
                genreName = "documentary";
                break;

            case 18 :
                genreName = "drama";
                break;

            case 10751 :
                genreName = "family";
                break;

            case 14 :
                genreName = "fantasy";
                break;

            case 36 :
                genreName = "history";
                break;

            case 27 :
                genreName = "horror";
                break;

            case 10402 :
                genreName = "music";
                break;

            case 9648 :
                genreName = "mystery";
                break;

            case 10749 :
                genreName = "romance";
                break;

            case 878 :
                genreName = "science fiction";
                break;

            case 10770 :
                genreName = "tv movie";
                break;

            case 53 :
                genreName = "thriller";
                break;

            case 10752 :
                genreName = "war";
                break;

            case 37 :
                genreName = "western";
                break;

            case 10759 :
                genreName = "action adventure";
                break;

            case 10762 :
                genreName = "kids";
                break;

            case 10763 :
                genreName = "news";
                break;

            case 10764 :
                genreName = "reality";
                break;

            case 10765 :
                genreName = "sci-fi and fantasy";
                break;

            case 10766 :
                genreName = "soap";
                break;

            case 10767 :
                genreName = "talk";
                break;

            case 10768 :
                genreName = "war and politics";
                break;

            case 0 :
                genreName = "-";
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

            case "" :
                regionName = "-";
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