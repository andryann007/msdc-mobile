package com.example.msdc.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.msdc.R
import com.example.msdc.databinding.ActivityMainBinding
import com.example.msdc.ui.home.HomeFragment
import com.example.msdc.ui.movie.MovieNowPlayingFragment
import com.example.msdc.ui.movie.MoviePopularFragment
import com.example.msdc.ui.movie.MovieTopRatedFragment
import com.example.msdc.ui.movie.MovieUpcomingFragment
import com.example.msdc.ui.tv_shows.TvAiringTodayFragment
import com.example.msdc.ui.tv_shows.TvOnAirFragment
import com.example.msdc.ui.tv_shows.TvPopularFragment
import com.example.msdc.ui.tv_shows.TvTopRatedFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdapterView.OnItemSelectedListener {
    private var searchType: String? = null
    private var filterType: String? = null
    private var genre = 0
    private var year = 0
    private var region: String? = null
    private var sortBy: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var toolbar: Toolbar
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var imgProfile: CircleImageView
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        toolbar.title = "MSDC"
        toolbar.subtitle = "Movie Series Data Center"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        drawerLayout = binding.drawerLayout

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        with(drawerLayout) { this.addDrawerListener(toggle) }
        toggle.syncState()
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            HomeFragment()
        ).commit()

        auth = Firebase.auth

        userProfile()

        onBackPressedDispatcher.addCallback(
            this,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "Activity back pressed invoked")
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        finish()
                    }
                }
            }
        )
    }

    private fun dialogSearch() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val v = inflater.inflate(R.layout.dialog_search, null)
        val inputSearch = v.findViewById<EditText>(R.id.inputSearch)
        val btnSearch = v.findViewById<Button>(R.id.btnSearch)
        val radioGroup = v.findViewById<RadioGroup>(R.id.radioGroup)
        val radioButtonMovie = v.findViewById<RadioButton>(R.id.radioButtonMovie)
        val radioButtonTV = v.findViewById<RadioButton>(R.id.radioButtonTV)

        builder.setView(v)
        val dialogSearch = builder.create()

        if (dialogSearch.window != null) {
            dialogSearch.window!!.setBackgroundDrawable(ColorDrawable(0))
            radioGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
                searchType = if (checkedId == R.id.radioButtonMovie) {
                    radioButtonMovie.text.toString()
                } else {
                    radioButtonTV.text.toString()
                }
            }
            dialogSearch.show()

            btnSearch.setOnClickListener {
                doSearch(inputSearch.text.toString())
                dialogSearch.hide()
            }

            inputSearch.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    doSearch(inputSearch.text.toString())
                }
                false
            }
        }
    }

    private fun doSearch(query: String) {
        if (query.isEmpty()) {
            Toast.makeText(applicationContext, "No Input !!!", Toast.LENGTH_SHORT).show()
            return
        }
        if (searchType == null) {
            Toast.makeText(applicationContext, "No Search Type !!!", Toast.LENGTH_SHORT).show()
            return
        }

        val i = Intent(this@MainActivity, SearchActivity::class.java)
        i.putExtra("type", searchType)
        i.putExtra("searchFor", query)
        startActivity(i)
    }

    private fun dialogFilter() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val v = inflater.inflate(R.layout.dialog_filter, null)
        val btnFilter = v.findViewById<Button>(R.id.btnFilter)
        val radioGroup = v.findViewById<RadioGroup>(R.id.radioGroup)

        builder.setView(v)
        val dialogFilter = builder.create()

        if (dialogFilter.window != null) {
            dialogFilter.window!!.setBackgroundDrawable(ColorDrawable(0))
            val spinnerFilterGenre = v.findViewById<Spinner>(R.id.spinnerFilterGenre)
            val spinnerFilterYear = v.findViewById<Spinner>(R.id.spinnerFilterYear)
            val spinnerFilterRegion = v.findViewById<Spinner>(R.id.spinnerFilterRegion)
            val spinnerSortBy = v.findViewById<Spinner>(R.id.spinnerSortBy)

            radioGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
                if (checkedId == R.id.radioButtonMovie) {
                    filterType = "movie"
                    val filterMovieGenreAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.movieGenreList)
                    )
                    filterMovieGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerFilterGenre.adapter = filterMovieGenreAdapter

                    val filterYearAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.yearList)
                    )
                    filterYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerFilterYear.adapter = filterYearAdapter

                    val filterRegionAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.regionList)
                    )
                    filterRegionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerFilterRegion.adapter = filterRegionAdapter

                    val sortByAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.sortList)
                    )
                    sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerSortBy.adapter = sortByAdapter
                } else if (checkedId == R.id.radioButtonTV) {
                    filterType = "tv"
                    val filterTvGenreAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.tvGenreList)
                    )
                    filterTvGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerFilterGenre.adapter = filterTvGenreAdapter

                    val filterYearAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.yearList)
                    )
                    filterYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerFilterYear.adapter = filterYearAdapter

                    val filterRegionAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.regionList)
                    )
                    filterRegionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerFilterRegion.adapter = filterRegionAdapter

                    val sortByAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.sortList)
                    )
                    sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerSortBy.adapter = sortByAdapter
                }
            }
            spinnerFilterGenre.onItemSelectedListener = this
            spinnerFilterRegion.onItemSelectedListener = this
            spinnerFilterYear.onItemSelectedListener = this
            spinnerSortBy.onItemSelectedListener = this

            dialogFilter.show()

            btnFilter.setOnClickListener {
                doFilter(filterType, genre, year, region, sortBy)
                dialogFilter.hide()
            }
        }
    }

    private fun doFilter(
        filterType: String?,
        genre: Int,
        year: Int,
        region: String?,
        sortBy: String?
    ) {
        val i = Intent(this@MainActivity, FilterActivity::class.java)
        if (filterType.equals("movie", ignoreCase = true)) {
            if (genre != 0 && year == 0 && region!!.isEmpty()) {
                i.putExtra("type", "filter_movie_genre")
                i.putExtra("genre", genre)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre == 0 && year != 0 && region!!.isEmpty()) {
                i.putExtra("type", "filter_movie_year")
                i.putExtra("year", year)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre == 0 && year == 0 && region!!.isNotEmpty()) {
                i.putExtra("type", "filter_movie_region")
                i.putExtra("region", region)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre != 0 && year != 0 && region!!.isEmpty()) {
                i.putExtra("type", "filter_movie_genre_and_year")
                i.putExtra("genre", genre)
                i.putExtra("year", year)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre != 0 && year == 0 && region!!.isNotEmpty()) {
                i.putExtra("type", "filter_movie_genre_and_region")
                i.putExtra("genre", genre)
                i.putExtra("region", region)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre == 0 && year != 0 && region!!.isNotEmpty()) {
                i.putExtra("type", "filter_movie_year_and_region")
                i.putExtra("year", year)
                i.putExtra("region", region)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre != 0 && year != 0 && region!!.isNotEmpty()) {
                i.putExtra("type", "movie")
                i.putExtra("genre", genre)
                i.putExtra("year", year)
                i.putExtra("region", region)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre == 0 && year == 0 && region!!.isEmpty()) {
                Toast.makeText(this@MainActivity, "No Filter Type !!!", Toast.LENGTH_SHORT).show()
            }
        } else if (filterType.equals("tv", ignoreCase = true)) {
            if (genre != 0 && year == 0 && region!!.isEmpty()) {
                i.putExtra("type", "filter_tv_genre")
                i.putExtra("genre", genre)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre == 0 && year != 0 && region!!.isEmpty()) {
                i.putExtra("type", "filter_tv_year")
                i.putExtra("year", year)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre == 0 && year == 0 && region!!.isNotEmpty()) {
                i.putExtra("type", "filter_tv_region")
                i.putExtra("region", region)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre != 0 && year != 0 && region!!.isEmpty()) {
                i.putExtra("type", "filter_tv_genre_and_year")
                i.putExtra("genre", genre)
                i.putExtra("year", year)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre != 0 && year == 0 && region!!.isNotEmpty()) {
                i.putExtra("type", "filter_tv_genre_and_region")
                i.putExtra("genre", genre)
                i.putExtra("region", region)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre == 0 && year != 0 && region!!.isNotEmpty()) {
                i.putExtra("type", "filter_tv_year_and_region")
                i.putExtra("year", year)
                i.putExtra("region", region)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre != 0 && year != 0 && region!!.isNotEmpty()) {
                i.putExtra("type", "tv")
                i.putExtra("genre", genre)
                i.putExtra("year", year)
                i.putExtra("region", region)
                i.putExtra("sortBy", sortBy)
                startActivity(i)
            }
            if (genre == 0 && year == 0 && region!!.isEmpty()) {
                Toast.makeText(this@MainActivity, "No Filter Type !!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                HomeFragment()
            ).commit()

            R.id.nav_now_playing_movie -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MovieNowPlayingFragment()
            ).commit()

            R.id.nav_upcoming_movie -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MovieUpcomingFragment()
            ).commit()

            R.id.nav_top_rated_movie -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MovieTopRatedFragment()
            ).commit()

            R.id.nav_popular_movie -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MoviePopularFragment()
            ).commit()

            R.id.nav_onair_tv -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                TvOnAirFragment()
            ).commit()

            R.id.nav_popular_tv -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                TvPopularFragment()
            ).commit()

            R.id.nav_airing_today_tv -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                TvAiringTodayFragment()
            ).commit()

            R.id.nav_top_rated_tv -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                TvTopRatedFragment()
            ).commit()

            R.id.nav_logout -> {
                auth.signOut()
                val logoutIntent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(logoutIntent)
                finish()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_search) {
            dialogSearch()
        } else if (item.itemId == R.id.nav_filter) {
            dialogFilter()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun userProfile() {
        val header = navigationView.getHeaderView(0)

        val user = auth.currentUser

        val database = Firebase.database

        user?.let { database.getReference("Users").child(it.uid) }
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val profileImage = snapshot.child("profileImage").value.toString()

                    textName = header.findViewById(R.id.textProfileName)
                    textEmail = header.findViewById(R.id.textProfileEmail)
                    imgProfile = header.findViewById(R.id.imageProfile)

                    textName.text = name
                    textEmail.text = email

                    if(profileImage == "" || profileImage == "2131165363"){
                        imgProfile.setImageResource(R.drawable.ic_account)
                    } else {
                        Picasso.get().load(profileImage).noFade().into(imgProfile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val genreSelected =
            parent.getItemAtPosition(position).toString().lowercase(Locale.getDefault())
        val yearSelected =
            parent.getItemAtPosition(position).toString().lowercase(Locale.getDefault())
        val regionSelected =
            parent.getItemAtPosition(position).toString().lowercase(Locale.getDefault())
        val sortSelected =
            parent.getItemAtPosition(position).toString().lowercase(Locale.getDefault())
        when (genreSelected) {
            "action" -> genre = 28
            "adventure" -> genre = 12
            "animation" -> genre = 16
            "comedy" -> genre = 35
            "crime" -> genre = 80
            "documentary" -> genre = 99
            "drama" -> genre = 18
            "family" -> genre = 10751
            "fantasy" -> genre = 14
            "history" -> genre = 36
            "horror" -> genre = 27
            "music" -> genre = 10402
            "mystery" -> genre = 9648
            "romance" -> genre = 10749
            "science fiction" -> genre = 878
            "tv movie" -> genre = 10770
            "thriller" -> genre = 53
            "war" -> genre = 10752
            "western" -> genre = 37
            "action adventure" -> genre = 10759
            "kids" -> genre = 10762
            "news" -> genre = 10763
            "reality" -> genre = 10764
            "sci-fi and fantasy" -> genre = 10765
            "soap" -> genre = 10766
            "talk" -> genre = 10767
            "war and politics" -> genre = 10768
            "not selected" -> genre = 0
        }
        when (yearSelected) {
            "1980" -> year = 1980
            "1981" -> year = 1981
            "1982" -> year = 1982
            "1983" -> year = 1983
            "1984" -> year = 1984
            "1985" -> year = 1985
            "1986" -> year = 1986
            "1987" -> year = 1987
            "1988" -> year = 1988
            "1989" -> year = 1989
            "1990" -> year = 1990
            "1991" -> year = 1991
            "1992" -> year = 1992
            "1993" -> year = 1993
            "1994" -> year = 1994
            "1995" -> year = 1995
            "1996" -> year = 1996
            "1997" -> year = 1997
            "1998" -> year = 1998
            "1999" -> year = 1999
            "2000" -> year = 2000
            "2001" -> year = 2001
            "2002" -> year = 2002
            "2003" -> year = 2003
            "2004" -> year = 2004
            "2005" -> year = 2005
            "2006" -> year = 2006
            "2007" -> year = 2007
            "2008" -> year = 2008
            "2009" -> year = 2009
            "2010" -> year = 2010
            "2011" -> year = 2011
            "2012" -> year = 2012
            "2013" -> year = 2013
            "2014" -> year = 2014
            "2015" -> year = 2015
            "2016" -> year = 2016
            "2017" -> year = 2017
            "2018" -> year = 2018
            "2019" -> year = 2019
            "2020" -> year = 2020
            "2021" -> year = 2021
            "2022" -> year = 2022
            "2023" -> year = 2023
            "not selected" -> year = 0
        }
        when (regionSelected) {
            "australia" -> region = "AU"
            "china" -> region = "CN"
            "france" -> region = "FR"
            "germany" -> region = "DE"
            "hong kong" -> region = "HK"
            "india" -> region = "IN"
            "indonesia" -> region = "ID"
            "japan" -> region = "JP"
            "russia" -> region = "RU"
            "south korea" -> region = "KR"
            "taiwan" -> region = "TW"
            "thailand" -> region = "TH"
            "united kingdom" -> region = "UK"
            "united states" -> region = "US"
            "not selected" -> region = ""
        }
        when (sortSelected) {
            "popularity (ascending)" -> sortBy = "popularity.asc"
            "popularity (descending)" -> sortBy = "popularity.desc"
            "revenue (ascending)" -> sortBy = "revenue.asc"
            "revenue (descending)" -> sortBy = "revenue.desc"
            "release date (ascending)" -> sortBy = "primary_release_date.asc"
            "release date (descending)" -> sortBy = "primary_release_date.desc"
            "vote average (ascending)" -> sortBy = "vote_average.asc"
            "vote average (descending)" -> sortBy = "vote_average.desc"
            "vote count (ascending)" -> sortBy = "vote_count.asc"
            "vote count (descending)" -> sortBy = "vote_count.desc"
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        const val MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9"
        const val LANGUAGE = "en-US"
    }
}