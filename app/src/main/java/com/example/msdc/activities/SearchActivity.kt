package com.example.msdc.activities

import android.content.ContentValues
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.adapter.MovieVerticalAdapter
import com.example.msdc.adapter.TVVerticalAdapter
import com.example.msdc.api.ApiClient
import com.example.msdc.api.ApiService
import com.example.msdc.api.MovieResponse
import com.example.msdc.api.MovieResult
import com.example.msdc.api.TVResponse
import com.example.msdc.api.TVResult
import com.example.msdc.databinding.ActivitySearchBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private var apiService: ApiService? = null

    private var movieSearchAdapter: MovieVerticalAdapter? = null
    private var tvSearchAdapter: TVVerticalAdapter? = null

    private val movieResults: MutableList<MovieResult> = ArrayList()
    private val tvResults: MutableList<TVResult> = ArrayList()

    private var page = 1
    private val limit = 15
    private var query: String? = null

    private lateinit var searchToolbar: Toolbar
    private lateinit var loadingSearch: ProgressBar
    private lateinit var rvSearch: RecyclerView
    private lateinit var textNoResults: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchToolbar = binding.toolbar
        loadingSearch = binding.loadingSearch
        rvSearch = binding.rvSearch
        textNoResults = binding.textNoResults

        val mPortraitLayoutManager = GridLayoutManager(this, 3)
        val mLandscapeLayoutManager = GridLayoutManager(this, 5)

        if(applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            rvSearch.layoutManager = mLandscapeLayoutManager
        } else {
            rvSearch.layoutManager = mPortraitLayoutManager
        }

        movieSearchAdapter = MovieVerticalAdapter(movieResults, this)
        tvSearchAdapter = TVVerticalAdapter(tvResults, this)
        apiService = ApiClient.client!!.create(ApiService::class.java)
        query = intent.getStringExtra("searchFor")

        searchToolbar.title = "Search Results"
        searchToolbar.subtitle = query

        when (intent.getStringExtra("type")) {
            "Movies" -> {
                searchForMovies(page)
                binding.rvSearch.adapter = movieSearchAdapter
                binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (!binding.rvSearch.canScrollVertically(1)) {
                            page++
                            searchForMovies(page)
                        }
                    }
                })
            }

            "TV Shows" -> {
                searchForTV(page)
                binding.rvSearch.adapter = tvSearchAdapter
                binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (!binding.rvSearch.canScrollVertically(1)) {
                            page++
                            searchForTV(page)
                        }
                    }
                })
            }
        }

        onBackPressedDispatcher.addCallback(
            this,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(ContentValues.TAG, "Activity back pressed invoked")
                    finish()
                }
            }
        )

        searchToolbar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun searchForMovies(page: Int) {
        val call = apiService?.searchMovie(
            MainActivity.MY_API_KEY,
            MainActivity.LANGUAGE,
            query,
            page,
            limit
        )
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingSearch.visibility = View.GONE
                        rvSearch.visibility = View.VISIBLE
                        val oldCount = movieResults.size
                        movieResults.addAll(response.body()!!.result!!)
                        movieSearchAdapter?.notifyItemRangeInserted(oldCount, movieResults.size)
                    } else {
                        loadingSearch.visibility = View.GONE
                        textNoResults.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                loadingSearch.visibility = View.GONE
                Toast.makeText(
                    this@SearchActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun searchForTV(page: Int) {
        val call = apiService?.searchTv(
            MainActivity.MY_API_KEY,
            MainActivity.LANGUAGE,
            query,
            page,
            limit
        )
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingSearch.visibility = View.GONE
                        rvSearch.visibility = View.VISIBLE
                        val oldCount = tvResults.size
                        tvResults.addAll(response.body()!!.result!!)
                        tvSearchAdapter?.notifyItemRangeInserted(oldCount, tvResults.size)
                    } else {
                        loadingSearch.visibility = View.GONE
                        textNoResults.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                loadingSearch.visibility = View.GONE
                Toast.makeText(
                    this@SearchActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}