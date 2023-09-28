package com.example.msdc.activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.adapter.CreditCastAdapter
import com.example.msdc.adapter.CreditCrewAdapter
import com.example.msdc.adapter.GenreAdapter
import com.example.msdc.adapter.ImageAdapter.setBackdropURL
import com.example.msdc.adapter.ImageAdapter.setPosterLogoURL
import com.example.msdc.adapter.ImageAdapter.setPosterURL
import com.example.msdc.adapter.ImageListAdapter
import com.example.msdc.adapter.KeywordAdapter
import com.example.msdc.adapter.MovieAdapter
import com.example.msdc.adapter.SeasonAdapter
import com.example.msdc.adapter.TVAdapter
import com.example.msdc.api.ApiClient.client
import com.example.msdc.api.ApiService
import com.example.msdc.api.CreditCastResult
import com.example.msdc.api.CreditCrewResult
import com.example.msdc.api.CreditResponse
import com.example.msdc.api.EpisodeResult
import com.example.msdc.api.GenreResult
import com.example.msdc.api.ImageResponse
import com.example.msdc.api.ImageResult
import com.example.msdc.api.KeywordResponse
import com.example.msdc.api.KeywordResult
import com.example.msdc.api.MovieDetails
import com.example.msdc.api.MovieResponse
import com.example.msdc.api.MovieResult
import com.example.msdc.api.SeasonResponse
import com.example.msdc.api.TVDetails
import com.example.msdc.api.TVResponse
import com.example.msdc.api.TVResult
import com.example.msdc.databinding.ActivityDetailBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.makeramen.roundedimageview.RoundedImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class DetailActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var apiService: ApiService? = null

    private var movieId = 0
    private var seriesId = 0

    private var movieRecommendationsAdapter: MovieAdapter? = null
    private var movieSimilarAdapter: MovieAdapter? = null

    private var tvRecommendationsAdapter: TVAdapter? = null
    private var tvSimilarAdapter: TVAdapter? = null

    private var movieGenreAdapter: GenreAdapter? = null
    private var tvGenreAdapter: GenreAdapter? = null

    private var movieKeywordAdapter: KeywordAdapter? = null

    private var movieImagesAdapter: ImageListAdapter? = null
    private var tvImagesAdapter: ImageListAdapter? = null

    private var movieCreditCastAdapter: CreditCastAdapter? = null
    private var movieCreditCrewAdapter: CreditCrewAdapter? = null
    private var tvCreditCastAdapter: CreditCastAdapter? = null
    private var tvCreditCrewAdapter: CreditCrewAdapter? = null

    private var tvSeasonAdapter: SeasonAdapter? = null

    private val movieGenre = ArrayList<GenreResult>()
    private val movieKeyword = ArrayList<KeywordResult>()
    private val movieImagesList = ArrayList<ImageResult>()
    private val movieCreditCastResult = ArrayList<CreditCastResult>()
    private val movieCreditCrewResult = ArrayList<CreditCrewResult>()
    private val movieRecommendationsResult: MutableList<MovieResult> = ArrayList()
    private val movieSimilarResult: MutableList<MovieResult> = ArrayList()

    private val tvGenre = ArrayList<GenreResult>()
    private val tvImagesList = ArrayList<ImageResult>()
    private val tvCreditCastResult = ArrayList<CreditCastResult>()
    private val tvCreditCrewResult = ArrayList<CreditCrewResult>()
    private val episodeResults =  ArrayList<EpisodeResult>()
    private val tvRecommendationsResult: MutableList<TVResult> = ArrayList()
    private val tvSimilarResult: MutableList<TVResult> = ArrayList()

    private var genre = 0
    private var year = 0
    private var region: String? = null
    private var sortBy: String? = null
    private var season = 0

    private lateinit var detailToolbar: Toolbar
    private lateinit var loadingDetails: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var imagePoster: RoundedImageView
    private lateinit var imagePosterBack: ImageView
    private lateinit var imageBackdrop: RoundedImageView
    private lateinit var textTitleReleaseDate: TextView
    private lateinit var textRunTime: TextView
    private lateinit var textOverview: TextView
    private lateinit var textLanguage: TextView
    private lateinit var textStatus: TextView
    private lateinit var textBudgetOrSeasons: TextView
    private lateinit var textRevenueOrEpisodes: TextView
    private lateinit var textPopularity: TextView
    private lateinit var textTagline: TextView
    private lateinit var textVoteCount: TextView
    private lateinit var textVoteAverage: TextView
    private lateinit var textHomePage: TextView

    private lateinit var textGenreList: TextView
    private lateinit var rvGenreList: RecyclerView
    private lateinit var textKeywordList: TextView
    private lateinit var rvKeywordList: RecyclerView
    private lateinit var textImageList: TextView
    private lateinit var rvImagesList: RecyclerView
    private lateinit var textMovieCreditCast: TextView
    private lateinit var rvCreditCast: RecyclerView
    private lateinit var textMovieCreditCrew: TextView
    private lateinit var rvCreditCrew: RecyclerView
    private lateinit var textMovieRecommendations: TextView
    private lateinit var rvMovieRecommendations: RecyclerView
    private lateinit var textMovieSimilar: TextView
    private lateinit var rvMovieSimilar: RecyclerView
    private lateinit var textTvSeasonAndEpisodeList: TextView
    private lateinit var rvTvSeasonAndEpisodeList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailToolbar = binding.toolbar
        setSupportActionBar(detailToolbar)

        loadingDetails = binding.loadingDetails
        scrollView = binding.scrollView
        imagePoster = binding.imagePoster
        imagePosterBack = binding.imagePosterBack
        imageBackdrop = binding.imageBackdrop
        textTitleReleaseDate = binding.textTitleReleaseDate
        textRunTime = binding.textRunTime
        textOverview = binding.textOverview
        textLanguage = binding.textLanguage
        textStatus = binding.textStatus
        textBudgetOrSeasons = binding.textBudgetOrSeasons
        textRevenueOrEpisodes = binding.textRevenueOrEpisodes
        textPopularity = binding.textPopularity
        textTagline = binding.textTagline
        textVoteCount = binding.textVoteCount
        textVoteAverage = binding.textVoteAverage
        textHomePage = binding.textHomePage

        textGenreList = binding.textGenreList
        rvGenreList = binding.rvGenreList
        textKeywordList = binding.textKeywordList
        rvKeywordList = binding.rvKeywordList
        textImageList = binding.textImageList
        rvImagesList = binding.rvImagesList
        textMovieCreditCast = binding.textMovieCreditCast
        rvCreditCast = binding.rvCreditCast
        textMovieCreditCrew = binding.textMovieCreditCrew
        rvCreditCrew = binding.rvCreditCrew
        textMovieRecommendations = binding.textMovieRecommendations
        rvMovieRecommendations = binding.rvMovieRecommendations
        textMovieSimilar = binding.textMovieSimilar
        rvMovieSimilar = binding.rvMovieSimilar
        textTvSeasonAndEpisodeList = binding.textTvSeasonAndEpisodeList
        rvTvSeasonAndEpisodeList = binding.rvTvSeasonAndEpisodeList

        val retrofit = client
        apiService = retrofit!!.create(ApiService::class.java)

        type = intent.getStringExtra("type")
        if (type == "movie") {
            movieId = intent.getIntExtra("movie_id", 0)
            setMovieDetails()
        } else if (type == "tv") {
            seriesId = intent.getIntExtra("series_id", 0)
            setTVDetails()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "Activity back pressed invoked")
                    finish()
                }
            }
        )

        detailToolbar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setMovieDetails() {
        loadingDetails.visibility = View.VISIBLE

        val call = apiService?.getMovieDetails(movieId, MainActivity.MY_API_KEY)
        call!!.enqueue(object : Callback<MovieDetails?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<MovieDetails?>, response: Response<MovieDetails?>) {
                if (response.body() != null) {
                    detailToolbar.title = "Movie Detail"
                    detailToolbar.subtitle = response.body()!!.title

                    loadingDetails.visibility = View.GONE
                    scrollView.visibility = View.VISIBLE
                    imagePosterBack.visibility = View.VISIBLE

                    setPosterURL(imagePosterBack, response.body()!!.posterPath)
                    setBackdropURL(imageBackdrop, response.body()!!.backdropPath)
                    setPosterLogoURL(imagePoster, response.body()!!.posterPath)

                    setTitleText(
                        textTitleReleaseDate, response.body()!!.title,
                        response.body()!!.releaseDate
                    )

                    setHtmlText(
                        textRunTime,
                        "Runtime",
                        response.body()!!.runtime + " Minutes"
                    )

                    textOverview.text = response.body()!!.overview
                    setHtmlText(textLanguage, "Language", response.body()!!.language)
                    setHtmlText(textStatus, "Status", response.body()!!.status)

                    val budget = response.body()!!.budget!!.toDouble()
                    val budgetFormatted = NumberFormat.getCurrencyInstance(Locale.US).format(budget)
                    setHtmlText(textBudgetOrSeasons, "Budget", budgetFormatted)

                    val revenue = response.body()!!.revenue!!.toDouble()
                    val revenueFormatted =
                        NumberFormat.getCurrencyInstance(Locale.US).format(revenue)
                    setHtmlText(textRevenueOrEpisodes, "Revenue", revenueFormatted)

                    val popularity = response.body()!!.popularity!!.toFloat()
                    val mPopularity = String.format(Locale.US, "%.2f", popularity)
                    setHtmlText(textPopularity, "Popularity", mPopularity)

                    if (response.body()!!.tagline!!.isEmpty()) {
                        setHtmlEmptyText(textTagline, "Tagline", "No Tagline Yet!!!")
                    } else {
                        setHtmlText(textTagline, "Tagline", response.body()!!.tagline)
                    }

                    val voteCount = response.body()!!.voteCount!!.toInt()
                    val mVoteCount = String.format(Locale.US, "%,d", voteCount).replace(",", ".")
                    if (voteCount == 0) {
                        setHtmlEmptyText(
                            textVoteCount,
                            "Vote Count",
                            "No Vote Count Yet!!!"
                        )
                    } else {
                        setHtmlText(textVoteCount, "Vote Count", mVoteCount)
                    }

                    val voteAverage = response.body()!!.voteAverage!!.toFloat()
                    val mVoteAverage = String.format(Locale.US, "%.2f", voteAverage)
                    if (voteAverage == 0f) {
                        setHtmlEmptyText(
                            textVoteAverage,
                            "Vote Average",
                            "No Vote Average Yet!!!"
                        )
                    } else {
                        setHtmlText(textVoteAverage, "Vote Average", mVoteAverage)
                    }

                    if (response.body()!!.homepage!!.isEmpty()) {
                        setHtmlEmptyText(
                            textHomePage,
                            "Homepage",
                            "No Website Homepage Yet!!!"
                        )
                    } else {
                        setHtmlLinkText(
                            textHomePage,
                            response.body()!!.homepage,
                            response.body()!!.homepage
                        )
                        textHomePage.setOnClickListener {
                            val webView = Intent(this@DetailActivity, WebViewActivity::class.java)
                            webView.putExtra("url", response.body()!!.homepage)
                            startActivity(webView)
                        }
                    }

                    keywordsMovie
                    imagesMovie
                    movieCast
                    movieCrew
                    recommendationsMovie
                    similarMovie

                    val movieGenre = response.body()!!.genres.toString()
                    if (movieGenre.isNotEmpty()) {
                        genresMovie
                    } else {
                        textGenreList.visibility = View.GONE
                        rvGenreList.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<MovieDetails?>, t: Throwable) {
                loadingDetails.visibility = View.GONE
                Toast.makeText(
                    this@DetailActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private val genresMovie: Unit
        get() {
            movieGenreAdapter = GenreAdapter(movieGenre, this)
            val call = apiService?.getMovieDetails(movieId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<MovieDetails?> {
                override fun onResponse(
                    call: Call<MovieDetails?>,
                    response: Response<MovieDetails?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.genres!!.isNotEmpty()) {
                            textGenreList.visibility = View.VISIBLE
                            rvGenreList.visibility = View.VISIBLE
                            rvGenreList.setHasFixedSize(true)

                            val oldCount = movieGenre.size
                            movieGenre.addAll(response.body()!!.genres!!)
                            movieGenreAdapter!!.notifyItemRangeInserted(oldCount, movieGenre.size)
                        } else {
                            textGenreList.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<MovieDetails?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvGenreList.adapter = movieGenreAdapter
        }
    private val keywordsMovie: Unit
        get() {
            movieKeywordAdapter = KeywordAdapter(movieKeyword, this)
            val call = apiService?.getMovieKeywords(movieId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<KeywordResponse?> {
                override fun onResponse(
                    call: Call<KeywordResponse?>,
                    response: Response<KeywordResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.keywords!!.isNotEmpty()) {
                            textKeywordList.visibility = View.VISIBLE
                            rvKeywordList.visibility = View.VISIBLE
                            rvKeywordList.setHasFixedSize(true)

                            val oldCount = movieKeyword.size
                            movieKeyword.addAll(response.body()!!.keywords!!)
                            movieKeywordAdapter!!.notifyItemRangeInserted(oldCount, movieKeyword.size)
                        } else {
                            textKeywordList.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<KeywordResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvKeywordList.adapter = movieKeywordAdapter
        }
    private val imagesMovie: Unit
        get() {
            movieImagesAdapter = ImageListAdapter(movieImagesList)
            val call = apiService?.getMovieImages(movieId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<ImageResponse?> {
                override fun onResponse(
                    call: Call<ImageResponse?>,
                    response: Response<ImageResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.results!!.isNotEmpty()) {
                            textImageList.visibility = View.VISIBLE
                            rvImagesList.visibility = View.VISIBLE
                            rvImagesList.setHasFixedSize(true)

                            val oldCount = movieImagesList.size
                            movieImagesList.addAll(response.body()!!.results!!)
                            movieImagesAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                movieImagesList.size
                            )
                        } else {
                            textImageList.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<ImageResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvImagesList.adapter = movieImagesAdapter
        }
    private val movieCast: Unit
        get() {
            movieCreditCastAdapter = CreditCastAdapter(movieCreditCastResult)
            val call = apiService?.getMovieCredit(movieId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<CreditResponse?> {
                override fun onResponse(
                    call: Call<CreditResponse?>,
                    response: Response<CreditResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.cast!!.isNotEmpty()) {
                            textMovieCreditCast.visibility = View.VISIBLE
                            rvCreditCast.visibility = View.VISIBLE
                            rvCreditCast.setHasFixedSize(true)

                            val oldCount = movieCreditCastResult.size
                            movieCreditCastResult.addAll(response.body()!!.cast!!)
                            movieCreditCastAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                movieCreditCastResult.size
                            )
                        } else {
                            textMovieCreditCast.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<CreditResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvCreditCast.adapter = movieCreditCastAdapter
        }
    private val movieCrew: Unit
        get() {
            movieCreditCrewAdapter = CreditCrewAdapter(movieCreditCrewResult)
            val call = apiService?.getMovieCredit(movieId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<CreditResponse?> {
                override fun onResponse(
                    call: Call<CreditResponse?>,
                    response: Response<CreditResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.crew!!.isNotEmpty()) {
                            textMovieCreditCrew.visibility = View.VISIBLE
                            rvCreditCrew.visibility = View.VISIBLE
                            rvCreditCrew.setHasFixedSize(true)

                            val oldCount = movieCreditCrewResult.size
                            movieCreditCrewResult.addAll(response.body()!!.crew!!)
                            movieCreditCrewAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                movieCreditCrewResult.size
                            )
                        } else {
                            textMovieCreditCrew.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<CreditResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvCreditCrew.adapter = movieCreditCrewAdapter
        }
    private val recommendationsMovie: Unit
        get() {
            movieRecommendationsAdapter = MovieAdapter(movieRecommendationsResult, this)
            val call =
                apiService?.getMovieRecommendations(movieId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<MovieResponse?> {
                override fun onResponse(
                    call: Call<MovieResponse?>,
                    response: Response<MovieResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.result!!.isNotEmpty()) {
                            textMovieRecommendations.visibility = View.VISIBLE
                            rvMovieRecommendations.visibility = View.VISIBLE
                            rvMovieRecommendations.setHasFixedSize(true)

                            val oldCount = movieRecommendationsResult.size
                            movieRecommendationsResult.addAll(response.body()!!.result!!)
                            movieRecommendationsAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                movieRecommendationsResult.size
                            )
                        } else {
                            textMovieRecommendations.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvMovieRecommendations.adapter = movieRecommendationsAdapter
        }
    private val similarMovie: Unit
        get() {
            movieSimilarAdapter = MovieAdapter(movieSimilarResult, this)
            val call = apiService?.getMovieSimilar(movieId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<MovieResponse?> {
                override fun onResponse(
                    call: Call<MovieResponse?>,
                    response: Response<MovieResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.result!!.isNotEmpty()) {
                            textMovieSimilar.visibility = View.VISIBLE
                            rvMovieSimilar.visibility = View.VISIBLE
                            rvMovieSimilar.setHasFixedSize(true)

                            val oldCount = movieSimilarResult.size
                            movieSimilarResult.addAll(response.body()!!.result!!)
                            movieSimilarAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                movieSimilarResult.size
                            )
                        } else {
                            textMovieSimilar.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvMovieSimilar.adapter = movieSimilarAdapter
        }

    private fun setTVDetails() {
        loadingDetails.visibility = View.VISIBLE
        val call = apiService?.getTvDetails(seriesId, MainActivity.MY_API_KEY)
        call!!.enqueue(object : Callback<TVDetails?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<TVDetails?>, response: Response<TVDetails?>) {
                if (response.body() != null) {
                    detailToolbar.title = "TV Series Detail"
                    detailToolbar.subtitle = response.body()!!.name

                    loadingDetails.visibility = View.GONE
                    scrollView.visibility = View.VISIBLE
                    imagePosterBack.visibility = View.VISIBLE

                    setPosterURL(imagePosterBack, response.body()!!.posterPath)
                    setBackdropURL(imageBackdrop, response.body()!!.backdropPath)
                    setPosterLogoURL(imagePoster, response.body()!!.posterPath)

                    setTitleText(
                        textTitleReleaseDate, response.body()!!.name,
                        response.body()!!.firstAirDate + " - " + response.body()!!.lastAirDate
                    )
                    val mEpisodeRunTime =
                        response.body()!!.episodeRuntime.contentToString().replace("[", "")
                            .replace("]", "")
                    setHtmlText(
                        textRunTime,
                        "Episode Runtime",
                        "$mEpisodeRunTime Episodes"
                    )

                    textOverview.text = response.body()!!.overview
                    setHtmlText(
                        textLanguage,
                        "Language",
                        response.body()!!.originalLanguage
                    )

                    setHtmlText(textStatus, "Status", response.body()!!.status)
                    setHtmlText(
                        textBudgetOrSeasons,
                        "Number of Seasons",
                        response.body()!!.numberOfSeasons.toString()
                    )

                    setHtmlText(
                        textRevenueOrEpisodes,
                        "Number of Episodes",
                        response.body()!!.numberOfEpisodes.toString()
                    )

                    val popularity = response.body()!!.popularity!!.toFloat()
                    val mPopularity = String.format(Locale.US, "%.2f", popularity)
                    setHtmlText(textPopularity, "Popularity", mPopularity)
                    if (response.body()!!.tagline!!.isEmpty()) {
                        setHtmlEmptyText(textTagline, "Tagline", "No Tagline Yet!!!")
                    } else {
                        setHtmlText(textTagline, "Tagline", response.body()!!.tagline)
                    }

                    val voteCount = response.body()!!.voteCount!!.toInt()
                    val mVoteCount = String.format(Locale.US, "%,d", voteCount).replace(",", ".")
                    if (voteCount == 0) {
                        setHtmlEmptyText(
                            textVoteCount,
                            "Vote Count",
                            "No Vote Count Yet!!!"
                        )
                    } else {
                        setHtmlText(textVoteCount, "Vote Count", mVoteCount)
                    }

                    val voteAverage = response.body()!!.voteAverage!!.toFloat()
                    val mVoteAverage = String.format(Locale.US, "%.2f", voteAverage)
                    if (voteAverage == 0f) {
                        setHtmlEmptyText(
                            textVoteAverage,
                            "Vote Average",
                            "No Vote Average Yet!!!"
                        )
                    } else {
                        setHtmlText(textVoteAverage, "Vote Average", mVoteAverage)
                    }

                    if (response.body()!!.homepage!!.isEmpty()) {
                        setHtmlEmptyText(
                            textHomePage,
                            "Homepage",
                            "No Website Homepage Yet!!!"
                        )
                    } else {
                        setHtmlLinkText(
                            textHomePage,
                            response.body()!!.homepage,
                            response.body()!!.homepage
                        )
                        textHomePage.setOnClickListener {
                            val webView = Intent(this@DetailActivity, WebViewActivity::class.java)
                            webView.putExtra("url", response.body()!!.homepage)
                            startActivity(webView)
                        }
                    }

                    imagesTV
                    tvCast
                    tvCrew
                    recommendationsTV
                    similarTV

                    val tvGenre = response.body()!!.genres.toString()
                    if (tvGenre.isNotEmpty()) {
                        genresTV
                    } else {
                        textGenreList.visibility = View.GONE
                        rvGenreList.visibility = View.GONE
                    }
                    val numberOfSeasons = response.body()!!.numberOfSeasons!!
                    if (numberOfSeasons == 1) {
                        getTVEpisodes(numberOfSeasons)
                    } else if (numberOfSeasons > 1) {
                        season = 1
                        while (season <= numberOfSeasons) {
                            getTVEpisodes(season)
                            season++
                        }
                    } else {
                        textTvSeasonAndEpisodeList.visibility = View.GONE
                        rvTvSeasonAndEpisodeList.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<TVDetails?>, t: Throwable) {
                loadingDetails.visibility = View.GONE
                Toast.makeText(
                    this@DetailActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private val genresTV: Unit
        get() {
            tvGenreAdapter = GenreAdapter(tvGenre, this)
            val call = apiService?.getTvDetails(seriesId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<TVDetails?> {
                override fun onResponse(call: Call<TVDetails?>, response: Response<TVDetails?>) {
                    if (response.body() != null) {
                        if (response.body()!!.genres!!.isNotEmpty()) {
                            textGenreList.visibility = View.VISIBLE
                            rvGenreList.visibility = View.VISIBLE
                            rvGenreList.setHasFixedSize(true)

                            val oldCount = tvGenre.size
                            tvGenre.addAll(response.body()!!.genres!!)
                            tvGenreAdapter!!.notifyItemRangeInserted(oldCount, tvGenre.size)
                        } else {
                            textGenreList.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<TVDetails?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvGenreList.adapter = tvGenreAdapter
        }

    private val imagesTV: Unit
        get() {
            tvImagesAdapter = ImageListAdapter(tvImagesList)
            val call = apiService?.getTvImages(seriesId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<ImageResponse?> {
                override fun onResponse(
                    call: Call<ImageResponse?>,
                    response: Response<ImageResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.results!!.isNotEmpty()) {
                            textImageList.visibility = View.VISIBLE
                            rvImagesList.visibility = View.VISIBLE
                            rvImagesList.setHasFixedSize(true)

                            val oldCount = tvImagesList.size
                            tvImagesList.addAll(response.body()!!.results!!)
                            tvImagesAdapter!!.notifyItemRangeInserted(oldCount, tvImagesList.size)
                        } else {
                            textImageList.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<ImageResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvImagesList.adapter = tvImagesAdapter
        }
    private val tvCast: Unit
        get() {
            tvCreditCastAdapter = CreditCastAdapter(tvCreditCastResult)
            val call = apiService?.getTvCredit(seriesId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<CreditResponse?> {
                override fun onResponse(
                    call: Call<CreditResponse?>,
                    response: Response<CreditResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.cast!!.isNotEmpty()) {
                            setNoteText(textMovieCreditCast, "TV Shows Credit Cast")
                            textMovieCreditCast.visibility = View.VISIBLE
                            rvCreditCast.visibility = View.VISIBLE
                            rvCreditCast.setHasFixedSize(true)

                            val oldCount = tvCreditCastResult.size
                            tvCreditCastResult.addAll(response.body()!!.cast!!)
                            tvCreditCastAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                tvCreditCastResult.size
                            )
                        } else {
                            textMovieCreditCast.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<CreditResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvCreditCast.adapter = tvCreditCastAdapter
        }
    private val tvCrew: Unit
        get() {
            tvCreditCrewAdapter = CreditCrewAdapter(tvCreditCrewResult)
            val call = apiService?.getTvCredit(seriesId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<CreditResponse?> {
                override fun onResponse(
                    call: Call<CreditResponse?>,
                    response: Response<CreditResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.crew!!.isNotEmpty()) {
                            setNoteText(textMovieCreditCrew, "TV Shows Credit Crew")
                            textMovieCreditCrew.visibility = View.VISIBLE
                            rvCreditCrew.visibility = View.VISIBLE
                            rvCreditCrew.setHasFixedSize(true)

                            val oldCount = tvCreditCrewResult.size
                            tvCreditCrewResult.addAll(response.body()!!.crew!!)
                            tvCreditCrewAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                tvCreditCrewResult.size
                            )
                        } else {
                            textMovieCreditCrew.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<CreditResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvCreditCrew.adapter = tvCreditCrewAdapter
        }
    private val recommendationsTV: Unit
        get() {
            tvRecommendationsAdapter = TVAdapter(tvRecommendationsResult, this)
            val call =
                apiService?.getTVRecommendations(seriesId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<TVResponse?> {
                override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                    if (response.body() != null) {
                        if (response.body()!!.result!!.isNotEmpty()) {
                            setNoteText(
                                textMovieRecommendations,
                                "TV Shows Recommendations"
                            )
                            textMovieRecommendations.visibility = View.VISIBLE
                            rvMovieRecommendations.visibility = View.VISIBLE
                            rvMovieRecommendations.setHasFixedSize(true)

                            val oldCount = tvRecommendationsResult.size
                            tvRecommendationsResult.addAll(response.body()!!.result!!)
                            tvRecommendationsAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                tvRecommendationsResult.size
                            )
                        } else {
                            textMovieRecommendations.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvMovieRecommendations.adapter = tvRecommendationsAdapter
        }
    private val similarTV: Unit
        get() {
            tvSimilarAdapter = TVAdapter(tvSimilarResult, this)
            val call = apiService?.getTVSimilar(seriesId, MainActivity.MY_API_KEY)
            call!!.enqueue(object : Callback<TVResponse?> {
                override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                    if (response.body() != null) {
                        if (response.body()!!.result!!.isNotEmpty()) {
                            setNoteText(textMovieSimilar, "Similar TV Shows")
                            textMovieSimilar.visibility = View.VISIBLE
                            rvMovieSimilar.visibility = View.VISIBLE
                            rvMovieSimilar.setHasFixedSize(true)

                            val oldCount = tvSimilarResult.size
                            tvSimilarResult.addAll(response.body()!!.result!!)
                            tvSimilarAdapter!!.notifyItemRangeInserted(
                                oldCount,
                                tvSimilarResult.size
                            )
                        } else {
                            textMovieSimilar.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@DetailActivity, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
            rvMovieSimilar.adapter = tvSimilarAdapter
        }

    private fun getTVEpisodes(season: Int) {
        tvSeasonAdapter = SeasonAdapter(episodeResults)
        val call =
            apiService?.getTvSeasonAndEpisode(seriesId, season, MainActivity.MY_API_KEY)
        call!!.enqueue(object : Callback<SeasonResponse?> {
            override fun onResponse(
                call: Call<SeasonResponse?>,
                response: Response<SeasonResponse?>
            ) {
                if (response.body() != null) {
                    textTvSeasonAndEpisodeList.visibility = View.VISIBLE
                    rvTvSeasonAndEpisodeList.visibility = View.VISIBLE
                    rvTvSeasonAndEpisodeList.setHasFixedSize(true)

                    val oldCount = episodeResults.size
                    episodeResults.addAll(response.body()!!.episodeResults!!)
                    tvSeasonAdapter!!.notifyItemRangeInserted(oldCount, episodeResults.size)
                } else {
                    textTvSeasonAndEpisodeList.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<SeasonResponse?>, t: Throwable) {
                Toast.makeText(
                    this@DetailActivity, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
        rvTvSeasonAndEpisodeList.adapter = tvSeasonAdapter
    }

    private fun setHtmlText(tv: TextView, textColored: String, textValue: String?) {
        tv.text = HtmlCompat.fromHtml(
            "<font color='#FFFFFF'>" + textColored + "</font> : " +
                    "<b>" + textValue + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setHtmlEmptyText(tv: TextView, textColored: String, textValue: String) {
        tv.text = HtmlCompat.fromHtml(
            "<font color='#FFFFFF'>" + textColored + "</font> : " +
                    "<b> <font color='#FF0000'>" + textValue + "</font> </b>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setHtmlLinkText(tv: TextView, textLink: String?, textValue: String?) {
        tv.text = HtmlCompat.fromHtml(
            "<font color='#FFFFFF'>" + "Homepage" + "</font> : " +
                    "<b> <a href='" + textLink + "'>" + textValue + "</a> </b>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setTitleText(tv: TextView, textTitle: String?, textReleaseData: String?) {
        tv.text = HtmlCompat.fromHtml(
            "<b>" + textTitle + "</b><font color='#FFFFFF'> ("
                    + textReleaseData + ") </font>", HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setNoteText(tv: TextView, note: String) {
        tv.text = HtmlCompat.fromHtml("<b>$note</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
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
        var type: String? = null
    }
}