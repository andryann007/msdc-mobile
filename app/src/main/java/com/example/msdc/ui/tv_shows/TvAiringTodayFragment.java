package com.example.msdc.ui.tv_shows;

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
import com.example.msdc.adapter.TVGridAdapter;
import com.example.msdc.api.ApiClient;
import com.example.msdc.api.ApiService;
import com.example.msdc.api.TVResponse;
import com.example.msdc.api.TVResult;
import com.example.msdc.databinding.FragmentTvAiringTodayBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TvAiringTodayFragment extends Fragment {

    private ApiService apiService;
    private ProgressBar loadingTvAiringToday;
    private TVGridAdapter tvAiringTodayAdapter;
    private final List<TVResult> tvAiringTodayResults = new ArrayList<>();

    private RecyclerView rvTvAiringToday;

    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";
    private int page = 1;

    private String searchType = null;

    private FragmentTvAiringTodayBinding binding;

    public TvAiringTodayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentTvAiringTodayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

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

    private void setOnAiringTV(View view) {
        TextView textTitle = view.findViewById(R.id.textTvVertical);
        String title = "Airing Today TV Shows";
        textTitle.setText(title);

        rvTvAiringToday = view.findViewById(R.id.rvTvVertical);
        tvAiringTodayAdapter = new TVGridAdapter(tvAiringTodayResults, getContext());
        loadingTvAiringToday = view.findViewById(R.id.loadingTvVertical);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        rvTvAiringToday.setLayoutManager(gridLayoutManager);
        rvTvAiringToday.setAdapter(tvAiringTodayAdapter);
        getOnAiringTV(page);

        rvTvAiringToday.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvTvAiringToday.canScrollVertically(1)){
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}