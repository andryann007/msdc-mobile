package com.example.msdc.ui.tv_shows;

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
import com.example.msdc.activities.MainActivity;
import com.example.msdc.activities.SearchActivity;
import com.example.msdc.adapter.TVAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.TVRespon;
import com.example.msdc.api.TVResult;
import com.example.msdc.databinding.FragmentMovieBinding;
import com.example.msdc.databinding.FragmentTvBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TvFragment extends Fragment {

    private ApiService apiService;
    private RecyclerView rvTvPopular, rvTvTopRated, rvTvOnAir, rvTvAiringToday;
    private ProgressBar loadingTvPopular, loadingTvTopRated, loadingTvOnAir, loadingTvAiringToday;
    private TVAdapter tvPopularAdapter, tvTopRatedAdapter, tvOnAirAdapter, tvAiringTodayAdapter;
    private final List<TVResult> tvPopularResults = new ArrayList<>();
    private final List<TVResult> tvTopRatedResults = new ArrayList<>();
    private final List<TVResult> tvOnAirResults = new ArrayList<>();
    private final List<TVResult> tvAiringTodayResults = new ArrayList<>();
    private int currentPageTVPopular = 1, currentPageTVTopRated = 1, currentPageTVOnAir = 1, currentPageTVAiringToday = 1;
    private int totalPagesTVPopular = 1, totalPagesTVTopRated = 1, totalPagesTVOnAir = 1, totalPagesTVAiringToday = 1;

    public static final String MYAPI_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";

    private String searchType = null;

    private FragmentTvBinding binding;

    public TvFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentTvBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setPopularTV(root);
        setTopRatedTV(root);
        setOnAirTV(root);
        setOnAiringTV(root);

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

    private void setOnAirTV(View view) {
        rvTvOnAir = view.findViewById(R.id.rvTVOnAir);
        tvOnAirAdapter = new TVAdapter(tvOnAirResults, getContext());
        loadingTvOnAir = view.findViewById(R.id.loadingTVOnAir);

        getOnAirTV();
        rvTvOnAir.setAdapter(tvOnAirAdapter);
    }

    private void getOnAirTV(){
        Call<TVRespon> call = apiService.getTvOnAir(MYAPI_KEY, LANGUAGE, currentPageTVOnAir);
        call.enqueue(new Callback<TVRespon>(){

            @Override
            public void onResponse(@NonNull Call<TVRespon> call, @NonNull Response<TVRespon> response) {
                if(response.body() != null){
                    totalPagesTVOnAir = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingTvOnAir.setVisibility(View.GONE);
                        int oldCount = tvOnAirResults.size();
                        tvOnAirResults.addAll(response.body().getResult());
                        tvOnAirAdapter.notifyItemChanged(oldCount, tvOnAirResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVRespon> call, @NonNull Throwable t) {

            }
        });
    }

    private void setTopRatedTV(View view) {
        rvTvTopRated = view.findViewById(R.id.rvTVTopRated);
        tvTopRatedAdapter = new TVAdapter(tvTopRatedResults, getContext());
        loadingTvTopRated = view.findViewById(R.id.loadingTVTopRated);

        getTopRatedTV();
        rvTvTopRated.setAdapter(tvTopRatedAdapter);
    }

    private void getTopRatedTV(){
        Call<TVRespon> call = apiService.getTvTopRated(MYAPI_KEY, LANGUAGE, currentPageTVTopRated);
        call.enqueue(new Callback<TVRespon>(){

            @Override
            public void onResponse(@NonNull Call<TVRespon> call, @NonNull Response<TVRespon> response) {
                if(response.body() != null){
                    totalPagesTVTopRated = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingTvTopRated.setVisibility(View.GONE);
                        int oldCount = tvTopRatedResults.size();
                        tvTopRatedResults.addAll(response.body().getResult());
                        tvTopRatedAdapter.notifyItemChanged(oldCount, tvTopRatedResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVRespon> call, @NonNull Throwable t) {

            }
        });
    }

    private void setPopularTV(View view) {
        rvTvPopular = view.findViewById(R.id.rvTVPopular);
        tvPopularAdapter = new TVAdapter(tvPopularResults, getContext());
        loadingTvPopular = view.findViewById(R.id.loadingTVPopular);

        getPopularTV();
        rvTvPopular.setAdapter(tvPopularAdapter);
    }

    private void getPopularTV(){
        Call<TVRespon> call = apiService.getTvPopular(MYAPI_KEY, LANGUAGE, currentPageTVPopular);
        call.enqueue(new Callback<TVRespon>(){

            @Override
            public void onResponse(@NonNull Call<TVRespon> call, @NonNull Response<TVRespon> response) {
                if(response.body() != null){
                    totalPagesTVPopular = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingTvPopular.setVisibility(View.GONE);
                        int oldCount = tvPopularResults.size();
                        tvPopularResults.addAll(response.body().getResult());
                        tvPopularAdapter.notifyItemChanged(oldCount, tvPopularResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVRespon> call, @NonNull Throwable t) {

            }
        });
    }

    private void setOnAiringTV(View view) {
        rvTvAiringToday = view.findViewById(R.id.rvTVAiring);
        tvAiringTodayAdapter = new TVAdapter(tvAiringTodayResults, getContext());
        loadingTvAiringToday = view.findViewById(R.id.loadingTVAiring);

        getOnAiringTV();
        rvTvAiringToday.setAdapter(tvAiringTodayAdapter);
    }

    private void getOnAiringTV(){
        Call<TVRespon> call = apiService.getTvAiringToday(MYAPI_KEY, LANGUAGE, currentPageTVPopular);
        call.enqueue(new Callback<TVRespon>(){
            @Override
            public void onResponse(@NonNull Call<TVRespon> call, @NonNull Response<TVRespon> response) {
                if(response.body() != null){
                    totalPagesTVAiringToday = response.body().getTotalPages();
                    if(response.body().getResult()!=null){
                        loadingTvAiringToday.setVisibility(View.GONE);
                        int oldCount = tvAiringTodayResults.size();
                        tvAiringTodayResults.addAll(response.body().getResult());
                        tvAiringTodayAdapter.notifyItemChanged(oldCount, tvAiringTodayResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVRespon> call, @NonNull Throwable t) {
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}