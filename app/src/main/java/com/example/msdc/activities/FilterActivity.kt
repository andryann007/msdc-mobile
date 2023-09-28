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
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.adapter.MovieVerticalAdapter
import com.example.msdc.adapter.TVVerticalAdapter
import com.example.msdc.api.ApiClient.client
import com.example.msdc.api.ApiService
import com.example.msdc.api.MovieResponse
import com.example.msdc.api.MovieResult
import com.example.msdc.api.TVResponse
import com.example.msdc.api.TVResult
import com.example.msdc.databinding.ActivityFilterBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class FilterActivity : AppCompatActivity() {
    private var apiService: ApiService? = null

    private var filterMovieAdapter: MovieVerticalAdapter? = null
    private var filterMovieGenreAdapter: MovieVerticalAdapter? = null
    private var filterMovieKeywordAdapter: MovieVerticalAdapter? = null
    private var filterMovieYearAdapter: MovieVerticalAdapter? = null
    private var filterMovieRegionAdapter: MovieVerticalAdapter? = null
    private var filterMovieGenreYearAdapter: MovieVerticalAdapter? = null
    private var filterMovieGenreRegionAdapter: MovieVerticalAdapter? = null
    private var filterMovieYearRegionAdapter: MovieVerticalAdapter? = null

    private val filterMovieResults: MutableList<MovieResult> = ArrayList()
    private val filterMovieGenre: MutableList<MovieResult> = ArrayList()
    private val filterMovieKeyword: MutableList<MovieResult> = ArrayList()
    private val filterMovieYear: MutableList<MovieResult> = ArrayList()
    private val filterMovieRegion: MutableList<MovieResult> = ArrayList()
    private val filterMovieGenreAndYear: MutableList<MovieResult> = ArrayList()
    private val filterMovieGenreAndRegion: MutableList<MovieResult> = ArrayList()
    private val filterMovieYearAndRegion: MutableList<MovieResult> = ArrayList()

    private var filterTVAdapter: TVVerticalAdapter? = null
    private var filterTvGenreAdapter: TVVerticalAdapter? = null
    private var filterTvKeywordAdapter: TVVerticalAdapter? = null
    private var filterTvYearAdapter: TVVerticalAdapter? = null
    private var filterTvRegionAdapter: TVVerticalAdapter? = null
    private var filterTvGenreYearAdapter: TVVerticalAdapter? = null
    private var filterTvGenreRegionAdapter: TVVerticalAdapter? = null
    private var filterTvYearRegionAdapter: TVVerticalAdapter? = null

    private val filterTvResults: MutableList<TVResult> = ArrayList()
    private val filterTvGenre: MutableList<TVResult> = ArrayList()
    private val filterTvKeyword: MutableList<TVResult> = ArrayList()
    private val filterTvYear: MutableList<TVResult> = ArrayList()
    private val filterTvRegion: MutableList<TVResult> = ArrayList()
    private val filterTvGenreAndYear: MutableList<TVResult> = ArrayList()
    private val filterTvGenreAndRegion: MutableList<TVResult> = ArrayList()
    private val filterTvYearAndRegion: MutableList<TVResult> = ArrayList()

    private var page = 1
    private val limit = 15
    private var genre = 0
    private var keyword = 0
    private var year = 0
    private var region: String? = null
    private var sortType: String? = null
    private var genreName: String? = null
    private var regionName: String? = null
    private var sortName: String? = null

    private lateinit var noFilterResult: TextView
    private lateinit var rvFilter: RecyclerView
    private lateinit var progressFilter: ProgressBar
    private lateinit var filterToolbar: Toolbar
    private lateinit var filterResult: TextView
    private lateinit var filterGenre: TextView
    private lateinit var filterRegion: TextView
    private lateinit var filterYear: TextView
    private lateinit var sortBy: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filterType = intent.getStringExtra("type")
        filterToolbar = binding.filterToolbar
        filterResult = binding.textFilterResult
        filterGenre = binding.textFilterByGenre
        filterRegion = binding.textFilterByRegion
        filterYear = binding.textFilterByYear
        sortBy = binding.textSortType

        noFilterResult = binding.textNoFilterResult
        rvFilter = binding.rvFilterList
        progressFilter = binding.loadingFilter

        val retrofit = client
        apiService = retrofit!!.create(ApiService::class.java)

        val mPortraitLayoutManager = GridLayoutManager(this, 3)
        val mLandscapeLayoutManager = GridLayoutManager(this, 5)

        if(applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            rvFilter.layoutManager = mLandscapeLayoutManager
        } else {
            rvFilter.layoutManager = mPortraitLayoutManager
        }

        if (filterType.equals("movie", ignoreCase = true)) {
            genre = intent.getIntExtra("genre", 0)
            year = intent.getIntExtra("year", 0)
            region = intent.getStringExtra("region")
            sortType = intent.getStringExtra("sortBy")

            setGenreName(genre)
            setRegionName(region)
            setSortName(sortType)

            setFilterText(filterResult, filterType!!.uppercase(Locale.getDefault()))

            setRegionText(filterRegion, regionName)
            setReleaseYear(filterYear, year.toString())
            setSortTypeText(sortBy, sortName)

            filterToolbar.title = "Filter Movies Results :"
            setSupportActionBar(filterToolbar)

            setMovieGenreText(filterGenre, genreName)
            filterMovieAdapter = MovieVerticalAdapter(filterMovieResults, this)

            rvFilter.adapter = filterMovieAdapter
            filterMovieData(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterMovieData(page)
                    }
                }
            })
        } else if (filterType.equals("tv", ignoreCase = true)) {
            genre = intent.getIntExtra("genre", 0)
            year = intent.getIntExtra("year", 0)
            region = intent.getStringExtra("region")
            sortType = intent.getStringExtra("sortBy")

            setGenreName(genre)
            setRegionName(region)
            setSortName(sortType)

            setFilterText(filterResult, filterType!!.uppercase(Locale.getDefault()))

            setRegionText(filterRegion, regionName)
            setReleaseYear(filterYear, year.toString())
            setSortTypeText(sortBy, sortName)

            filterToolbar.title = "Filter TV Shows Results :"
            setSupportActionBar(filterToolbar)

            setTvGenreText(filterGenre, genreName)
            filterTVAdapter = TVVerticalAdapter(filterTvResults, this)

            rvFilter.adapter = filterTVAdapter
            filterTvData(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterTvData(page)
                    }
                }
            })
        } else if (filterType.equals("filter_movie_genre", ignoreCase = true)) {
            genre = intent.getIntExtra("genre", 0)
            sortType = intent.getStringExtra("sortBy")

            setGenreName(genre)
            setSortName(sortType)

            setFilterText(filterResult, "Filter Movie Genre")
            setRegionText(filterRegion, "-")
            setReleaseYear(filterYear, "-")
            setSortTypeText(sortBy, sortName)

            filterToolbar.title = "Filter Movie Genre Results :"
            setSupportActionBar(filterToolbar)

            setMovieGenreText(filterGenre, genreName)
            filterMovieGenreAdapter = MovieVerticalAdapter(filterMovieGenre, this)

            rvFilter.adapter = filterMovieGenreAdapter
            filterMovieGenre(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterMovieGenre(page)
                    }
                }
            })
        } else if (filterType.equals("filter_tv_genre", ignoreCase = true)) {
            genre = intent.getIntExtra("genre", 0)
            sortType = intent.getStringExtra("sortBy")

            setGenreName(genre)
            setSortName(sortType)

            setFilterText(filterResult, "Filter TV Shows Genre")
            setRegionText(filterRegion, "-")
            setReleaseYear(filterYear, "-")
            setSortTypeText(sortBy, sortName)

            filterToolbar.title = "Filter TV Shows Genre Results :"
            setSupportActionBar(filterToolbar)

            setTvGenreText(filterGenre, genreName)
            filterTvGenreAdapter = TVVerticalAdapter(filterTvGenre, this)

            rvFilter.adapter = filterTvGenreAdapter
            filterTvGenre(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterTvGenre(page)
                    }
                }
            })
        } else if (filterType.equals("filter_movie_keyword", ignoreCase = true)) {
            keyword = intent.getIntExtra("keyword", 0)
            val keywordName = intent.getStringExtra("keyword_name")

            sortType = intent.getStringExtra("sortBy")
            setSortName(sortType)

            setFilterText(filterResult, "Filter Movie Keyword")
            setRegionText(filterRegion, "-")
            setReleaseYear(filterYear, "-")
            setSortTypeText(sortBy, sortName)

            filterToolbar.title = "Filter Movie Keyword Results :"
            setSupportActionBar(filterToolbar)

            setMovieKeywordText(filterGenre, keywordName)
            filterMovieKeywordAdapter = MovieVerticalAdapter(filterMovieKeyword, this)

            rvFilter.adapter = filterMovieKeywordAdapter
            filterMovieKeyword(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterMovieKeyword(page)
                    }
                }
            })
        } else if (filterType.equals("filter_movie_year", ignoreCase = true)) {
            year = intent.getIntExtra("year", 0)
            sortType = intent.getStringExtra("sortBy")

            setSortName(sortType)
            setFilterText(filterResult, "Filter Movie Year")
            setReleaseYear(filterYear, year.toString())
            setRegionText(filterRegion, "-")
            setSortTypeText(sortBy, sortName)
            setMovieGenreText(filterGenre, "-")

            filterToolbar.title = "Filter Movie Year Results :"
            setSupportActionBar(filterToolbar)

            filterMovieYearAdapter = MovieVerticalAdapter(filterMovieYear, this)
            rvFilter.adapter = filterMovieYearAdapter
            filterMovieYear(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterMovieYear(page)
                    }
                }
            })
        } else if (filterType.equals("filter_movie_region", ignoreCase = true)) {
            region = intent.getStringExtra("region")
            sortType = intent.getStringExtra("sortBy")

            setRegionName(region)
            setSortName(sortType)

            setFilterText(filterResult, "Filter Movie Region")
            setReleaseYear(filterYear, "-")
            setRegionText(filterRegion, regionName)
            setSortTypeText(sortBy, sortName)
            setMovieGenreText(filterGenre, "-")

            filterToolbar.title = "Filter Movie Region Results :"
            setSupportActionBar(filterToolbar)

            filterMovieRegionAdapter = MovieVerticalAdapter(filterMovieRegion, this)
            rvFilter.adapter = filterMovieRegionAdapter
            filterMovieRegion(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterMovieRegion(page)
                    }
                }
            })
        } else if (filterType.equals("filter_movie_genre_and_year", ignoreCase = true)) {
            year = intent.getIntExtra("year", 0)
            genre = intent.getIntExtra("genre", 0)
            sortType = intent.getStringExtra("sortBy")

            setGenreName(genre)
            setSortName(sortType)

            setFilterText(filterResult, "Filter Movie Genre & Year")
            setReleaseYear(filterYear, year.toString())
            setRegionText(filterRegion, "-")
            setSortTypeText(sortBy, sortName)
            setTvGenreText(filterGenre, genreName)

            filterToolbar.title = "Filter Movie Genre & Year Results :"
            setSupportActionBar(filterToolbar)

            filterMovieGenreYearAdapter = MovieVerticalAdapter(filterMovieGenreAndYear, this)
            rvFilter.adapter = filterMovieGenreYearAdapter
            filterMovieGenreAndYear(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterMovieGenreAndYear(page)
                    }
                }
            })
        } else if (filterType.equals("filter_movie_genre_and_region", ignoreCase = true)) {
            genre = intent.getIntExtra("genre", 0)
            region = intent.getStringExtra("region")
            sortType = intent.getStringExtra("sortBy")

            setGenreName(genre)
            setRegionName(region)
            setSortName(sortType)

            setFilterText(filterResult, "Filter Movie Genre & Region")
            setRegionText(filterRegion, regionName)
            setReleaseYear(filterYear, "-")
            setSortTypeText(sortBy, sortName)
            setTvGenreText(filterGenre, genreName)

            filterToolbar.title = "Filter Movie Genre & Region Results :"
            setSupportActionBar(filterToolbar)

            filterMovieGenreRegionAdapter = MovieVerticalAdapter(filterMovieGenreAndRegion, this)
            rvFilter.adapter = filterMovieGenreRegionAdapter
            filterMovieGenreAndRegion(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterMovieGenreAndRegion(page)
                    }
                }
            })
        } else if (filterType.equals("filter_movie_year_and_region", ignoreCase = true)) {
            year = intent.getIntExtra("year", 0)
            region = intent.getStringExtra("region")
            sortType = intent.getStringExtra("sortBy")

            setRegionName(region)
            setSortName(sortType)

            setFilterText(filterResult, "Filter Movie Year & Region")
            setReleaseYear(filterYear, year.toString())
            setRegionText(filterRegion, regionName)
            setSortTypeText(sortBy, sortName)
            setMovieGenreText(filterGenre, "-")

            filterToolbar.title = "Filter Movie Year & Region Results :"
            setSupportActionBar(filterToolbar)

            filterMovieYearRegionAdapter = MovieVerticalAdapter(filterMovieYearAndRegion, this)
            rvFilter.adapter = filterMovieYearRegionAdapter
            filterMovieYearAndRegion(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterMovieYearAndRegion(page)
                    }
                }
            })
        } else if (filterType.equals("filter_tv_keyword", ignoreCase = true)) {
            keyword = intent.getIntExtra("keyword", 0)
            val keywordName = intent.getStringExtra("keyword_name")

            sortType = intent.getStringExtra("sortBy")
            setSortName(sortType)
            setFilterText(filterResult, "Filter TV Shows Keyword")
            setRegionText(filterRegion, "-")
            setReleaseYear(filterYear, "-")
            setSortTypeText(sortBy, sortName)

            filterToolbar.title = "Filter TV Shows Keyword Results :"
            setSupportActionBar(filterToolbar)

            setTvKeywordText(filterGenre, keywordName)
            filterTvKeywordAdapter = TVVerticalAdapter(filterTvKeyword, this)
            rvFilter.adapter = filterTvKeywordAdapter
            filterTvKeyword(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterTvKeyword(page)
                    }
                }
            })
        } else if (filterType.equals("filter_tv_year", ignoreCase = true)) {
            year = intent.getIntExtra("year", 0)
            sortType = intent.getStringExtra("sortBy")

            setSortName(sortType)
            setFilterText(filterResult, "Filter TV Shows Year")
            setReleaseYear(filterYear, year.toString())
            setRegionText(filterRegion, "-")
            setSortTypeText(sortBy, sortName)
            setMovieGenreText(filterGenre, "-")

            filterToolbar.title = "Filter TV Shows Year Results :"
            setSupportActionBar(filterToolbar)

            filterTvYearAdapter = TVVerticalAdapter(filterTvYear, this)
            rvFilter.adapter = filterTvYearAdapter
            filterTvYear(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterTvYear(page)
                    }
                }
            })
        } else if (filterType.equals("filter_tv_region", ignoreCase = true)) {
            region = intent.getStringExtra("region")
            sortType = intent.getStringExtra("sortBy")

            setRegionName(region)
            setSortName(sortType)
            setFilterText(filterResult, "Filter TV Shows Region")
            setReleaseYear(filterYear, "-")
            setRegionText(filterRegion, regionName)
            setSortTypeText(sortBy, sortName)
            setTvGenreText(filterGenre, "-")

            filterToolbar.title = "Filter TV Shows Region Results :"
            setSupportActionBar(filterToolbar)

            filterTvRegionAdapter = TVVerticalAdapter(filterTvRegion, this)
            rvFilter.adapter = filterTvRegionAdapter
            filterTvRegion(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterTvRegion(page)
                    }
                }
            })
        } else if (filterType.equals("filter_tv_genre_and_year", ignoreCase = true)) {
            year = intent.getIntExtra("year", 0)
            genre = intent.getIntExtra("genre", 0)
            sortType = intent.getStringExtra("sortBy")

            setGenreName(genre)
            setSortName(sortType)
            setFilterText(filterResult, "Filter TV Shows Genre & Year")
            setReleaseYear(filterYear, year.toString())
            setRegionText(filterRegion, "-")
            setSortTypeText(sortBy, sortName)
            setTvGenreText(filterGenre, genreName)

            filterToolbar.title = "Filter TV Shows Genre & Year Results :"
            setSupportActionBar(filterToolbar)

            filterTvGenreYearAdapter = TVVerticalAdapter(filterTvGenreAndYear, this)
            rvFilter.adapter = filterTvGenreYearAdapter
            filterTvGenreAndYear(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterTvGenreAndYear(page)
                    }
                }
            })
        } else if (filterType.equals("filter_tv_genre_and_region", ignoreCase = true)) {
            genre = intent.getIntExtra("genre", 0)
            region = intent.getStringExtra("region")
            sortType = intent.getStringExtra("sortBy")

            setGenreName(genre)
            setRegionName(region)
            setSortName(sortType)

            setFilterText(filterResult, "Filter TV Shows Genre & Region")
            setRegionText(filterRegion, regionName)
            setReleaseYear(filterYear, "-")
            setSortTypeText(sortBy, sortName)
            setTvGenreText(filterGenre, genreName)

            filterToolbar.title = "Filter TV Shows Genre & Region Results :"
            setSupportActionBar(filterToolbar)

            filterTvGenreRegionAdapter = TVVerticalAdapter(filterTvGenreAndRegion, this)
            rvFilter.adapter = filterTvGenreRegionAdapter
            filterTvGenreAndRegion(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterTvGenreAndRegion(page)
                    }
                }
            })
        } else if (filterType.equals("filter_tv_year_and_region", ignoreCase = true)) {
            year = intent.getIntExtra("year", 0)
            region = intent.getStringExtra("region")
            sortType = intent.getStringExtra("sortBy")

            setRegionName(region)
            setSortName(sortType)

            setFilterText(filterResult, "Filter TV Shows Year & Region")
            setReleaseYear(filterYear, year.toString())
            setRegionText(filterRegion, regionName)
            setSortTypeText(sortBy, sortName)
            setTvGenreText(filterGenre, "-")

            filterToolbar.title = "Filter TV Shows Year & Region Results :"
            setSupportActionBar(filterToolbar)

            filterTvYearRegionAdapter = TVVerticalAdapter(filterTvYearAndRegion, this)
            rvFilter.adapter = filterTvYearRegionAdapter
            filterTvYearAndRegion(page)

            rvFilter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        filterTvYearAndRegion(page)
                    }
                }
            })
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

        filterToolbar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun filterMovieData(page: Int) {
        val call = apiService?.filterMovie(
            MY_API_KEY,
            LANGUAGE,
            page,
            limit,
            genre,
            year,
            region,
            sortType
        )
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterMovieResults.size

                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE

                        filterMovieResults.addAll(response.body()!!.result!!)
                        filterMovieAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterMovieResults.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterMovieGenre(page: Int) {
        val call = apiService?.filterMovieGenre(MY_API_KEY, LANGUAGE, page, limit, genre, sortType)
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterMovieGenre.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterMovieGenre.addAll(response.body()!!.result!!)
                        filterMovieGenreAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterMovieGenre.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterMovieKeyword(page: Int) {
        val call =
            apiService?.filterMovieKeyword(MY_API_KEY, LANGUAGE, page, limit, keyword, sortType)
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterMovieKeyword.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterMovieKeyword.addAll(response.body()!!.result!!)
                        filterMovieKeywordAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterMovieKeyword.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterMovieYear(page: Int) {
        val call = apiService?.filterMovieYear(MY_API_KEY, LANGUAGE, page, limit, year, sortType)
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterMovieYear.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterMovieYear.addAll(response.body()!!.result!!)
                        filterMovieYearAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterMovieYear.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterMovieRegion(page: Int) {
        val call =
            apiService?.filterMovieRegion(MY_API_KEY, LANGUAGE, page, limit, region, sortType)
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterMovieRegion.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterMovieRegion.addAll(response.body()!!.result!!)
                        filterMovieRegionAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterMovieRegion.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterMovieGenreAndYear(page: Int) {
        val call = apiService?.filterMovieGenreAndYear(
            MY_API_KEY,
            LANGUAGE,
            page,
            limit,
            genre,
            year,
            sortType
        )
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterMovieGenreAndYear.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterMovieGenreAndYear.addAll(response.body()!!.result!!)
                        filterMovieGenreYearAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterMovieGenreAndYear.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterMovieGenreAndRegion(page: Int) {
        val call = apiService?.filterMovieGenreAndRegion(
            MY_API_KEY, LANGUAGE, page,
            limit, genre, region, sortType
        )
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterMovieGenreAndRegion.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterMovieGenreAndRegion.addAll(response.body()!!.result!!)
                        filterMovieGenreRegionAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterMovieGenreAndRegion.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterMovieYearAndRegion(page: Int) {
        val call = apiService?.filterMovieYearAndRegion(
            MY_API_KEY, LANGUAGE, page,
            limit, year, region, sortType
        )
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterMovieYearAndRegion.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterMovieYearAndRegion.addAll(response.body()!!.result!!)
                        filterMovieYearRegionAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterMovieYearAndRegion.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterTvData(page: Int) {
        val call =
            apiService?.filterTv(MY_API_KEY, LANGUAGE, page, limit, genre, year, region, sortType)
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterTvResults.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterTvResults.addAll(response.body()!!.result!!)
                        filterTVAdapter?.notifyItemRangeInserted(oldCount, filterTvResults.size)
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterTvGenre(page: Int) {
        val call = apiService?.filterTvGenre(MY_API_KEY, LANGUAGE, page, limit, genre, sortType)
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterTvGenre.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterTvGenre.addAll(response.body()!!.result!!)
                        filterTvGenreAdapter?.notifyItemRangeInserted(oldCount, filterTvGenre.size)
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterTvKeyword(page: Int) {
        val call =
            apiService?.filterTvKeyword(MY_API_KEY, LANGUAGE, page, limit, keyword, sortType)
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterTvKeyword.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterTvKeyword.addAll(response.body()!!.result!!)
                        filterTvKeywordAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterTvKeyword.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterTvYear(page: Int) {
        val call = apiService?.filterTvYear(MY_API_KEY, LANGUAGE, page, limit, year, sortType)
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterTvYear.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterTvYear.addAll(response.body()!!.result!!)
                        filterTvYearAdapter?.notifyItemRangeInserted(oldCount, filterTvYear.size)
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterTvRegion(page: Int) {
        val call = apiService?.filterTvRegion(MY_API_KEY, LANGUAGE, page, limit, region, sortType)
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterTvRegion.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterTvRegion.addAll(response.body()!!.result!!)
                        filterTvRegionAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterTvRegion.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterTvGenreAndYear(page: Int) {
        val call = apiService?.filterTvGenreAndYear(
            MY_API_KEY,
            LANGUAGE,
            page,
            limit,
            genre,
            year,
            sortType
        )
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterTvGenreAndYear.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterTvGenreAndYear.addAll(response.body()!!.result!!)
                        filterTvGenreYearAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterTvGenreAndYear.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterTvGenreAndRegion(page: Int) {
        val call = apiService?.filterTvGenreAndRegion(
            MY_API_KEY, LANGUAGE, page,
            limit, genre, region, sortType
        )
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterTvGenreAndRegion.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterTvGenreAndRegion.addAll(response.body()!!.result!!)
                        filterTvGenreRegionAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterTvGenreAndRegion.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterTvYearAndRegion(page: Int) {
        val call = apiService?.filterTvYearAndRegion(
            MY_API_KEY, LANGUAGE, page,
            limit, year, region, sortType
        )
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty() && response.body()!!.result!!.size > 0) {
                        val oldCount = filterTvYearAndRegion.size
                        progressFilter.visibility = View.GONE
                        rvFilter.visibility = View.VISIBLE
                        filterTvYearAndRegion.addAll(response.body()!!.result!!)
                        filterTvYearRegionAdapter?.notifyItemRangeInserted(
                            oldCount,
                            filterTvYearAndRegion.size
                        )
                    } else {
                        progressFilter.visibility = View.GONE
                        noFilterResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                progressFilter.visibility = View.GONE
                Toast.makeText(
                    this@FilterActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setFilterText(tv: TextView, type: String) {
        tv.text = HtmlCompat.fromHtml(
            "Filter Results For : <b>$type</b>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setMovieGenreText(tv: TextView, genre: String?) {
        tv.text =
            HtmlCompat.fromHtml("Movie Genre : <b>$genre</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setMovieKeywordText(tv: TextView, keyword: String?) {
        tv.text =
            HtmlCompat.fromHtml("Movie Keyword : <b>$keyword</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setTvGenreText(tv: TextView, genre: String?) {
        tv.text =
            HtmlCompat.fromHtml("TV Shows Genre : <b>$genre</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setTvKeywordText(tv: TextView, keyword: String?) {
        tv.text = HtmlCompat.fromHtml(
            "TV Shows Keyword : <b>$keyword</b>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setReleaseYear(tv: TextView, year: String) {
        tv.text =
            HtmlCompat.fromHtml("Release Year : <b>$year</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setRegionText(tv: TextView, region: String?) {
        tv.text = HtmlCompat.fromHtml("Region : <b>$region</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setSortTypeText(tv: TextView, sortType: String?) {
        tv.text =
            HtmlCompat.fromHtml("Order By : <b>$sortType</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setGenreName(genreId: Int) {
        when (genreId) {
            28 -> genreName = "action"
            12 -> genreName = "adventure"
            16 -> genreName = "animation"
            35 -> genreName = "comedy"
            80 -> genreName = "crime"
            99 -> genreName = "documentary"
            18 -> genreName = "drama"
            10751 -> genreName = "family"
            14 -> genreName = "fantasy"
            36 -> genreName = "history"
            27 -> genreName = "horror"
            10402 -> genreName = "music"
            9648 -> genreName = "mystery"
            10749 -> genreName = "romance"
            878 -> genreName = "science fiction"
            10770 -> genreName = "tv movie"
            53 -> genreName = "thriller"
            10752 -> genreName = "war"
            37 -> genreName = "western"
            10759 -> genreName = "action adventure"
            10762 -> genreName = "kids"
            10763 -> genreName = "news"
            10764 -> genreName = "reality"
            10765 -> genreName = "sci-fi and fantasy"
            10766 -> genreName = "soap"
            10767 -> genreName = "talk"
            10768 -> genreName = "war and politics"
            0 -> genreName = "-"
        }
    }

    private fun setRegionName(regionCode: String?) {
        when (regionCode) {
            "AU" -> regionName = "australia"
            "CN" -> regionName = "china"
            "FR" -> regionName = "france"
            "DE" -> regionName = "germany"
            "HK" -> regionName = "hong kong"
            "IN" -> regionName = "india"
            "ID" -> regionName = "indonesia"
            "JP" -> regionName = "japan"
            "RU" -> regionName = "russia"
            "KR" -> regionName = "south korea"
            "TW" -> regionName = "taiwan"
            "TH" -> regionName = "thailand"
            "UK" -> regionName = "united kingdom"
            "US" -> regionName = "united states"
            "" -> regionName = "-"
        }
    }

    private fun setSortName(sortCode: String?) {
        when (sortCode) {
            "popularity.asc" -> sortName = "popularity (ascending)"
            "popularity.desc" -> sortName = "popularity (descending)"
            "revenue.asc" -> sortName = "revenue (ascending)"
            "revenue.desc" -> sortName = "revenue (descending)"
            "primary_release_date.asc" -> sortName = "release date (ascending)"
            "primary_release_date.desc" -> sortName = "release date (descending)"
            "vote_average.asc" -> sortName = "vote average (ascending)"
            "vote_average.desc" -> sortName = "vote average (descending)"
            "vote_count.asc" -> sortName = "vote count (ascending)"
            "vote_count.desc" -> sortName = "vote count (descending)"
        }
    }

    companion object {
        const val MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9"
        const val LANGUAGE = "en-US"
    }
}
