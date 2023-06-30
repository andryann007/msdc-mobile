package com.example.msdc.ui.movie;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.msdc.R;
import com.example.msdc.activities.SearchActivity;
import com.example.msdc.adapter.MovieGridAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.MovieResponse;
import com.example.msdc.api.MovieResult;
import com.example.msdc.databinding.FragmentMoviePopularBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MoviePopularFragment extends Fragment {

    private ApiService apiService;
    private ProgressBar loadingMoviePopular;
    private MovieGridAdapter moviePopularAdapter;
    private final List<MovieResult> moviePopularResults = new ArrayList<>();

    private RecyclerView rvMoviePopular;
    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";
    private int page = 1;

    private String searchType = null;

    private FragmentMoviePopularBinding binding;

    public MoviePopularFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentMoviePopularBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setPopularMovies(root);
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

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioButtonMovie){
                    searchType = radioButtonMovie.getText().toString();
                } else {
                    searchType = radioButtonTV.getText().toString();
                }
            });
            imageDoSearch.setOnClickListener(view -> doSearch(inputSearch.getText().toString()));

            inputSearch.setOnEditorActionListener((v1, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    doSearch(inputSearch.getText().toString());
                }
                return false;
            });
        }
    }

    private void doSearch(String query) {
        if(query.isEmpty()){
            Toast.makeText(getContext(),"No Input !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(searchType == null){
            Toast.makeText(getContext(),"No Search Type !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(getContext(), SearchActivity.class);
        i.putExtra("type", searchType);
        i.putExtra("searchFor", query);
        startActivity(i);
    }

    private void setPopularMovies(View view) {
        TextView textTitle = view.findViewById(R.id.textMovieVertical);
        String title = "Popular Movies";
        textTitle.setText(title);

        rvMoviePopular = view.findViewById(R.id.rvMovieVertical);
        moviePopularAdapter = new MovieGridAdapter(moviePopularResults, getContext());
        loadingMoviePopular = view.findViewById(R.id.loadingMovieVertical);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        rvMoviePopular.setLayoutManager(gridLayoutManager);
        rvMoviePopular.setAdapter(moviePopularAdapter);
        getPopularMovies(page);

        rvMoviePopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvMoviePopular.canScrollVertically(1)){
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}