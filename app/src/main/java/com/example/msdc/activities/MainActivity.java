package com.example.msdc.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.msdc.R;
import com.example.msdc.databinding.ActivityMainBinding;
import com.example.msdc.ui.account.FavoriteFragment;
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
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MYAPI_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9";

    public static final String LANGUAGE = "en-US";

    private String searchType = null;

    private ActivityMainBinding binding;

    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        /*RoundedImageView image_profile = findViewById(R.id.imageProfile);
        TextView textName, textEmail;
        textName = findViewById(R.id.textProfileName);
        textEmail = findViewById(R.id.textProfileEmail);

        if(currentUser !=null){
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            Picasso.get().load(currentUser.getPhotoUrl()).into(image_profile);
            textName.setText(name);
            textEmail.setText(email);
        }*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

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
            dialogSearch.show();
        }
    }

    private void doSearch(String query) {
        if(query.isEmpty()){
            Toast.makeText(getApplicationContext(),"Tidak ada inputan!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(searchType == null){
            Toast.makeText(getApplicationContext(),"Harap pilih tipe search!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("tipe", searchType);
        i.putExtra("searchFor", query);
        startActivity(i);
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

            case R.id.nav_favorites:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FavoriteFragment()).commit();
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
        inflater.inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.nav_search:
                dialogSearch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}