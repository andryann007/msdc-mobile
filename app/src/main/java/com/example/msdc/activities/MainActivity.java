package com.example.msdc.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.msdc.R;
import com.example.msdc.databinding.ActivityMainBinding;
import com.example.msdc.ui.home.HomeFragment;
import com.example.msdc.ui.movie.MovieNowPlayingFragment;
import com.example.msdc.ui.movie.MoviePopularFragment;
import com.example.msdc.ui.movie.MovieTopRatedFragment;
import com.example.msdc.ui.movie.MovieUpcomingFragment;
import com.example.msdc.ui.tv_shows.TvAiringTodayFragment;
import com.example.msdc.ui.tv_shows.TvOnAirFragment;
import com.example.msdc.ui.tv_shows.TvPopularFragment;
import com.example.msdc.ui.tv_shows.TvTopRatedFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener{
    public static final String MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";

    private String searchType = null;

    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;

    TextView textName, textEmail;

    private String filterType = null;
    private String genre = null;
    private String year = null;
    private String region = null;
    private String sortBy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.msdc.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loadUserInfo();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void dialogSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_search, null);
        EditText inputSearch = v.findViewById(R.id.inputSearch);
        Button btnSearch = v.findViewById(R.id.btnSearch);
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
            btnSearch.setOnClickListener(view -> doSearch(inputSearch.getText().toString()));

            inputSearch.setOnEditorActionListener((v1, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    doSearch(inputSearch.getText().toString());
                }
                return false;
            });
            dialogSearch.show();
        }
    }

    private void doSearch(String query) {
        if(query.isEmpty()){
            Toast.makeText(getApplicationContext(),"No Input !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(searchType == null){
            Toast.makeText(getApplicationContext(),"No Search Type !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("type", searchType);
        i.putExtra("searchFor", query);
        startActivity(i);
    }

    private void dialogFilter(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_filter, null);

        Button btnFilter = v.findViewById(R.id.btnFilter);
        RadioGroup radioGroup = v.findViewById(R.id.radioGroup);

        builder.setView(v);
        AlertDialog dialogFilter = builder.create();
        if(dialogFilter.getWindow() != null){
            dialogFilter.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            Spinner spinnerFilterGenre = v.findViewById(R.id.spinnerFilterGenre);

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioButtonMovie){
                    filterType = "movie";
                    ArrayAdapter<String> filterMovieGenreAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.movieGenreList));

                    filterMovieGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFilterGenre.setAdapter(filterMovieGenreAdapter);
                } else if(checkedId == R.id.radioButtonTV){
                    filterType = "tv";
                    ArrayAdapter<String> filterTvGenreAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.tvGenreList));

                    filterTvGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFilterGenre.setAdapter(filterTvGenreAdapter);
                } else {
                    filterType = "movie";
                    ArrayAdapter<String> filterMovieGenreAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.movieGenreList));

                    filterMovieGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFilterGenre.setAdapter(filterMovieGenreAdapter);
                }
            });

            Spinner spinnerFilterYear = v.findViewById(R.id.spinnerFilterYear);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            List<String> years = new ArrayList<>();
            for(int i=1980; i<=currentYear; i++){
                years.add(Integer.toString(i));
            }
            ArrayAdapter<String> filterYearAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, years);
            filterYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFilterYear.setAdapter(filterYearAdapter);

            Spinner spinnerFilterRegion = v.findViewById(R.id.spinnerFilterRegion);
            ArrayAdapter<String> filterRegionAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.regionList));
            filterRegionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFilterRegion.setAdapter(filterRegionAdapter);

            Spinner spinnerSortBy = v.findViewById(R.id.spinnerSortBy);
            ArrayAdapter<String> sortByAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sortList));
            sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSortBy.setAdapter(sortByAdapter);

            spinnerFilterGenre.setOnItemSelectedListener(this);
            spinnerFilterRegion.setOnItemSelectedListener(this);
            spinnerFilterYear.setOnItemSelectedListener(this);
            spinnerSortBy.setOnItemSelectedListener(this);

            btnFilter.setOnClickListener(view -> doFilter(filterType, genre, year, region, sortBy));
            dialogFilter.show();
        }
    }

    private void doFilter(String filterType, String genre, String year, String region, String sortBy) {
        Intent i = new Intent(MainActivity.this, FilterActivity.class);

        switch(filterType){
            case "movie" :
                i.putExtra("type", "movie");
                i.putExtra("genre", genre);
                i.putExtra("year", year);
                i.putExtra("region", region);
                i.putExtra("sortBy", sortBy);
                break;

            case "tv" :
                i.putExtra("type", "tv");
                i.putExtra("genre", genre);
                i.putExtra("year", year);
                i.putExtra("region", region);
                i.putExtra("sortBy", sortBy);
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;

            case R.id.nav_now_playing_movie:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MovieNowPlayingFragment()).commit();
                break;

            case R.id.nav_upcoming_movie:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MovieUpcomingFragment()).commit();
                break;

            case R.id.nav_top_rated_movie:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MovieTopRatedFragment()).commit();
                break;

            case R.id.nav_popular_movie:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MoviePopularFragment()).commit();
                break;

            case R.id.nav_onair_tv:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TvOnAirFragment()).commit();
                break;

            case R.id.nav_popular_tv:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TvPopularFragment()).commit();
                break;

            case R.id.nav_airing_today_tv:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TvAiringTodayFragment()).commit();
                break;

            case R.id.nav_top_rated_tv:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TvTopRatedFragment()).commit();
                break;

            case R.id.nav_logout:
                firebaseAuth.signOut();
                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.nav_search) {
            dialogSearch();
        } else if(item.getItemId() == R.id.nav_filter){
            dialogFilter();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user "+firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info of user here from snapshot
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        //set data to ui
                        CircleImageView image_profile = findViewById(R.id.imageProfile);
                        textName = findViewById(R.id.textProfileName);
                        textEmail = findViewById(R.id.textProfileEmail);

                        textName.setText(name);
                        textEmail.setText(email);
                        if(profileImage.equals("") || profileImage.equals("2131165363")){
                            image_profile.setImageResource(R.drawable.ic_account);
                        } else {
                            Uri photo_url = Uri.parse(profileImage);
                            Picasso.get().load(photo_url).into(image_profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String genreSelected = parent.getItemAtPosition(position).toString().toLowerCase();
        String yearSelected = parent.getItemAtPosition(position).toString();
        String regionSelected = parent.getItemAtPosition(position).toString().toLowerCase();
        String sortSelected = parent.getItemAtPosition(position).toString().toLowerCase();

        switch(genreSelected){
            case "action" :
                genre = "28";
                break;

            case "adventure" :
                genre = "12";
                break;

            case "animation" :
                genre = "16";
                break;

            case "comedy" :
                genre = "35";
                break;

            case "crime" :
                genre = "80";
                break;

            case "documentary" :
                genre = "99";
                break;

            case "drama" :
                genre = "18";
                break;

            case "family" :
                genre = "10751";
                break;

            case "fantasy" :
                genre = "14";
                break;

            case "history" :
                genre = "36";
                break;

            case "horror" :
                genre = "27";
                break;

            case "music" :
                genre = "10402";
                break;

            case "mystery" :
                genre = "9648";
                break;

            case "romance" :
                genre = "10749";
                break;

            case "science fiction" :
                genre = "878";
                break;

            case "tv movie" :
                genre = "10770";
                break;

            case "thriller" :
                genre = "53";
                break;

            case "war" :
                genre = "10752";
                break;

            case "western" :
                genre = "37";
                break;

            case "action adventure" :
                genre = "10759";
                break;

            case "kids" :
                genre = "10762";
                break;

            case "news" :
                genre = "10763";
                break;

            case "reality" :
                genre = "10764";
                break;

            case "sci-fi and fantasy" :
                genre = "10765";
                break;

            case "soap" :
                genre = "10766";
                break;

            case "talk" :
                genre = "10767";
                break;

            case "war and politics" :
                genre = "10768";
                break;
        }

        switch(yearSelected){
            case "1980" :
                year = "1980";
                break;

            case "1981" :
                year = "1981";
                break;

            case "1982" :
                year = "1982";
                break;

            case "1983" :
                year = "1983";
                break;

            case "1984" :
                year = "1984";
                break;

            case "1985" :
                year = "1985";
                break;

            case "1986" :
                year = "1986";
                break;

            case "1987" :
                year = "1987";
                break;

            case "1988" :
                year = "1988";
                break;

            case "1989" :
                year = "1989";
                break;

            case "1990" :
                year = "1990";
                break;

            case "1991" :
                year = "1991";
                break;

            case "1992" :
                year = "1992";
                break;

            case "1993" :
                year = "1993";
                break;

            case "1994" :
                year = "1994";
                break;

            case "1995" :
                year = "1995";
                break;

            case "1996" :
                year = "1996";
                break;

            case "1997" :
                year = "1997";
                break;

            case "1998" :
                year = "1998";
                break;

            case "1999" :
                year = "1999";
                break;

            case "2000" :
                year = "2000";
                break;

            case "2001" :
                year = "2001";
                break;

            case "2002" :
                year = "2002";
                break;

            case "2003" :
                year = "2003";
                break;

            case "2004" :
                year = "2004";
                break;

            case "2005" :
                year = "2005";
                break;

            case "2006" :
                year = "2006";
                break;

            case "2007" :
                year = "2007";
                break;

            case "2008" :
                year = "2008";
                break;

            case "2009" :
                year = "2009";
                break;

            case "2010" :
                year = "2010";
                break;

            case "2011" :
                year = "2011";
                break;

            case "2012" :
                year = "2012";
                break;

            case "2013" :
                year = "2013";
                break;

            case "2014" :
                year = "2014";
                break;

            case "2015" :
                year = "2015";
                break;

            case "2016" :
                year = "2016";
                break;

            case "2017" :
                year = "2017";
                break;

            case "2018" :
                year = "2018";
                break;

            case "2019" :
                year = "2019";
                break;

            case "2020" :
                year = "2020";
                break;

            case "2021" :
                year = "2021";
                break;

            case "2022" :
                year = "2022";
                break;

            case "2023" :
                year = "2023";
                break;

            case "2024" :
                year = "2024";
                break;

            case "2025" :
                year = "2025";
                break;
        }

        switch(regionSelected){
            case("australia") :
                region = "AU";
                break;

            case("china") :
                region = "CN";
                break;

            case("england") :
                region = "GB";
                break;

            case("france") :
                region = "FR";
                break;

            case("germany") :
                region = "DE";
                break;

            case("hong kong") :
                region = "HK";
                break;

            case("india") :
                region = "IN";
                break;

            case("indonesia") :
                region = "ID";
                break;

            case("japan") :
                region = "JP";
                break;

            case("russia") :
                region = "RU";
                break;

            case("south korea") :
                region = "KR";
                break;

            case("taiwan") :
                region = "TW";
                break;

            case("thailand") :
                region = "TH";
                break;

            case("united kingdom") :
                region = "UK";
                break;

            case("united states") :
                region = "US";
                break;
        }

        switch(sortSelected){
            case "popularity (ascending)" :
                sortBy = "popularity.asc";
                break;

            case "popularity (descending)" :
                sortBy = "popularity.desc";
                break;

            case "revenue (ascending)" :
                sortBy = "revenue.asc";
                break;

            case "revenue (descending)" :
                sortBy = "revenue.desc";
                break;

            case "release date (ascending)" :
                sortBy = "primary_release_date.asc";
                break;

            case "release date (descending)" :
                sortBy = "primary_release_date.desc";
                break;

            case "vote average (ascending)" :
                sortBy = "vote_average.asc";
                break;

            case "vote average (descending)" :
                sortBy = "vote_average.desc";
                break;

            case "vote count (ascending)" :
                sortBy = "vote_count.asc";
                break;

            case "vote count (descending)" :
                sortBy = "vote_count.desc";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}