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
import com.example.msdc.databinding.FragmentTvOnAirBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TvOnAirFragment extends Fragment {

    private ApiService apiService;
    private ProgressBar loadingTvOnAir;
    private TVGridAdapter tvOnAirAdapter;
    private final List<TVResult> tvOnAirResults = new ArrayList<>();

    private RecyclerView rvTvOnAir;

    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";
    private int page = 1;

    private String searchType = null;

    private FragmentTvOnAirBinding binding;

    public TvOnAirFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentTvOnAirBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        setOnAirTV(root);

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

    private void setOnAirTV(View view) {
        TextView textTitle = view.findViewById(R.id.textTvVertical);
        String title = "On Air TV Shows";
        textTitle.setText(title);

        rvTvOnAir = view.findViewById(R.id.rvTvVertical);
        tvOnAirAdapter = new TVGridAdapter(tvOnAirResults, getContext());
        loadingTvOnAir = view.findViewById(R.id.loadingTvVertical);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        rvTvOnAir.setLayoutManager(gridLayoutManager);
        rvTvOnAir.setAdapter(tvOnAirAdapter);
        getOnAirTV(page);

        rvTvOnAir.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!rvTvOnAir.canScrollVertically(1)){
                    page++;
                    getOnAirTV(page);
                }
            }
        });
    }

    private void getOnAirTV(int PAGE){
        Call<TVResponse> call = apiService.getTvOnAir(MY_API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(@NonNull Call<TVResponse> call, @NonNull Response<TVResponse> response) {
                if(response.body() != null){
                    if(response.body().getResult()!=null){
                        loadingTvOnAir.setVisibility(View.GONE);
                        rvTvOnAir.setVisibility(View.VISIBLE);

                        int oldCount = tvOnAirResults.size();
                        tvOnAirResults.addAll(response.body().getResult());
                        tvOnAirAdapter.notifyItemRangeInserted(oldCount, tvOnAirResults.size());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVResponse> call, @NonNull Throwable t) {
                loadingTvOnAir.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed To Fetch On Air TV Shows !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}