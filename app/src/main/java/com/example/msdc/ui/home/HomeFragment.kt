package com.example.msdc.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.R
import com.example.msdc.adapter.MovieAdapter
import com.example.msdc.adapter.PersonAdapter
import com.example.msdc.adapter.TVAdapter
import com.example.msdc.api.ApiClient.client
import com.example.msdc.api.ApiService
import com.example.msdc.api.MovieResponse
import com.example.msdc.api.MovieResult
import com.example.msdc.api.PersonResponse
import com.example.msdc.api.PersonResult
import com.example.msdc.api.TVResponse
import com.example.msdc.api.TVResult
import com.example.msdc.databinding.FragmentHomeBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private var apiService: ApiService? = null

    private var movieTrendingAdapter: MovieAdapter? = null
    private var moviePopularAdapter: MovieAdapter? = null
    private var movieNowPlayingAdapter: MovieAdapter? = null
    private var movieTopRatedAdapter: MovieAdapter? = null
    private var movieUpcomingAdapter: MovieAdapter? = null

    private val movieTrendingResults: MutableList<MovieResult> = ArrayList()
    private val moviePopularResults: MutableList<MovieResult> = ArrayList()
    private val movieNowPlayingResults: MutableList<MovieResult> = ArrayList()
    private val movieTopRatedResults: MutableList<MovieResult> = ArrayList()
    private val movieUpcomingResults: MutableList<MovieResult> = ArrayList()

    private var tvTrendingAdapter: TVAdapter? = null
    private var tvPopularAdapter: TVAdapter? = null
    private var tvTopRatedAdapter: TVAdapter? = null
    private var tvOnAirAdapter: TVAdapter? = null
    private var tvAiringTodayAdapter: TVAdapter? = null

    private val tvTrendingResults: MutableList<TVResult> = ArrayList()
    private val tvPopularResults: MutableList<TVResult> = ArrayList()
    private val tvTopRatedResults: MutableList<TVResult> = ArrayList()
    private val tvOnAirResults: MutableList<TVResult> = ArrayList()
    private val tvAiringTodayResults: MutableList<TVResult> = ArrayList()

    private var personTrendingAdapter: PersonAdapter? = null

    private val personTrendingResults: MutableList<PersonResult> = ArrayList()

    private var page = 1
    private val limit = 15

    private lateinit var loadingMovieTrending: ProgressBar
    private lateinit var loadingMoviePopular: ProgressBar
    private lateinit var loadingMovieNowPlaying: ProgressBar
    private lateinit var loadingMovieTopRated: ProgressBar
    private lateinit var loadingMovieUpcoming: ProgressBar
    private lateinit var rvMovieTrending: RecyclerView
    private lateinit var rvMoviePopular: RecyclerView
    private lateinit var rvMovieNowPlaying: RecyclerView
    private lateinit var rvMovieTopRated: RecyclerView
    private lateinit var rvMovieUpcoming: RecyclerView

    private lateinit var loadingTvTrending: ProgressBar
    private lateinit var loadingTvPopular: ProgressBar
    private lateinit var loadingTvTopRated: ProgressBar
    private lateinit var loadingTvOnAir: ProgressBar
    private lateinit var loadingTvAiringToday: ProgressBar
    private lateinit var rvTvTrending: RecyclerView
    private lateinit var rvTvPopular: RecyclerView
    private lateinit var rvTvOnAir: RecyclerView
    private lateinit var rvTvTopRated: RecyclerView
    private lateinit var rvTvAiringToday: RecyclerView

    private lateinit var loadingPersonTrending: ProgressBar
    private lateinit var rvPersonTrending: RecyclerView

    private lateinit var scrollView: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        scrollView = binding.scrollView

        val root: View = binding.root
        val retrofit = client
        apiService = retrofit!!.create(ApiService::class.java)

        setTrendingMovies(root)
        setTrendingTV(root)
        setTrendingPerson(root)
        setPopularMovies(root)
        setNowPlayingMovies(root)
        setUpcomingMovies(root)
        setTopRatedMovies(root)
        setPopularTV(root)
        setTopRatedTV(root)
        setOnAirTV(root)
        setOnAiringTV(root)

        return root
    }

    private fun setTopRatedMovies(view: View) {
        movieTopRatedAdapter = MovieAdapter(movieTopRatedResults, requireContext())
        rvMovieTopRated = view.findViewById(R.id.rvMovieTopRated)
        loadingMovieTopRated = view.findViewById(R.id.loadingMovieTopRated)

        rvMovieTopRated.adapter = movieTopRatedAdapter
        getTopRatedMovies(page)

        rvMovieTopRated.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvMovieTopRated.canScrollHorizontally(1)) {
                    page++
                    getTopRatedMovies(page)
                }
            }
        })
    }

    private fun getTopRatedMovies(page: Int) {
        val call = apiService?.getTopRatedMovies(MY_API_KEY, LANGUAGE, page, limit)

        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingMovieTopRated.visibility = View.GONE
                        rvMovieTopRated.visibility = View.VISIBLE

                        val oldCount = movieTopRatedResults.size
                        movieTopRatedResults.addAll(response.body()!!.result!!)
                        movieTopRatedAdapter?.notifyItemRangeInserted(
                            oldCount,
                            movieTopRatedResults.size
                        )
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                loadingMovieTopRated.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setUpcomingMovies(view: View) {
        movieUpcomingAdapter = MovieAdapter(movieUpcomingResults, requireContext())
        loadingMovieUpcoming = view.findViewById(R.id.loadingUpcomingMovie)

        rvMovieUpcoming = view.findViewById(R.id.rvUpcomingMovie)
        rvMovieUpcoming.adapter = movieUpcomingAdapter
        getUpcomingMovies(page)

        rvMovieUpcoming.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvMovieUpcoming.canScrollHorizontally(1)) {
                    page++
                    getUpcomingMovies(page)
                }
            }
        })
    }

    private fun getUpcomingMovies(page: Int) {
        val call = apiService?.getUpcomingMovies(MY_API_KEY, LANGUAGE, page, limit)
        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(call: Call<MovieResponse?>, response: Response<MovieResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingMovieUpcoming.visibility = View.GONE
                        rvMovieUpcoming.visibility = View.VISIBLE

                        val oldCount = movieUpcomingResults.size
                        movieUpcomingResults.addAll(response.body()!!.result!!)
                        movieUpcomingAdapter?.notifyItemRangeInserted(
                            oldCount,
                            movieUpcomingResults.size
                        )
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                loadingMovieUpcoming.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setNowPlayingMovies(view: View) {
        movieNowPlayingAdapter = MovieAdapter(movieNowPlayingResults, requireContext())
        loadingMovieNowPlaying = view.findViewById(R.id.loadingMovieNowPlaying)

        rvMovieNowPlaying = view.findViewById(R.id.rvMovieNowPlaying)
        rvMovieNowPlaying.adapter = movieNowPlayingAdapter
        getNowPlayingMovies(page)

        rvMovieNowPlaying.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvMovieNowPlaying.canScrollHorizontally(1)) {
                    page++
                    getNowPlayingMovies(page)
                }
            }
        })
    }

    private fun getNowPlayingMovies(page: Int) {
        val call = apiService?.getNowPlayingMovies(MY_API_KEY, LANGUAGE, page, limit)

        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(call: Call<MovieResponse?>, response: Response<MovieResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingMovieNowPlaying.visibility = View.GONE
                        rvMovieNowPlaying.visibility = View.VISIBLE

                        val oldCount = movieNowPlayingResults.size
                        movieNowPlayingResults.addAll(response.body()!!.result!!)
                        movieNowPlayingAdapter?.notifyItemRangeInserted(
                            oldCount,
                            movieNowPlayingResults.size
                        )
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                loadingMovieNowPlaying.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setPopularMovies(view: View) {
        moviePopularAdapter = MovieAdapter(moviePopularResults, requireContext())
        loadingMoviePopular = view.findViewById(R.id.loadingMoviePopular)

        rvMoviePopular = view.findViewById(R.id.rvMoviePopular)
        rvMoviePopular.adapter = moviePopularAdapter
        getPopularMovies(page)

        rvMoviePopular.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (rvMoviePopular.canScrollHorizontally(1)) {
                    page++
                    getPopularMovies(page)
                }
            }
        })
    }

    private fun getPopularMovies(page: Int) {
        val call = apiService?.getPopularMovies(MY_API_KEY, LANGUAGE, page, limit)

        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(call: Call<MovieResponse?>, response: Response<MovieResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingMoviePopular.visibility = View.GONE
                        rvMoviePopular.visibility = View.VISIBLE

                        val oldCount = moviePopularResults.size
                        moviePopularResults.addAll(response.body()!!.result!!)
                        moviePopularAdapter?.notifyItemRangeInserted(
                            oldCount,
                            moviePopularResults.size
                        )
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                loadingMoviePopular.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setOnAirTV(view: View) {
        tvOnAirAdapter = TVAdapter(tvOnAirResults, requireContext())
        loadingTvOnAir = view.findViewById(R.id.loadingTVOnAir)

        rvTvOnAir = view.findViewById(R.id.rvTVOnAir)
        rvTvOnAir.adapter = tvOnAirAdapter
        getOnAirTV(page)

        rvTvOnAir.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvTvOnAir.canScrollHorizontally(1)) {
                    page++
                    getOnAirTV(page)
                }
            }
        })
    }

    private fun getOnAirTV(page: Int) {
        val call = apiService?.getTvOnAir(MY_API_KEY, LANGUAGE, page, limit)

        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingTvOnAir.visibility = View.GONE
                        rvTvOnAir.visibility = View.VISIBLE

                        val oldCount = tvOnAirResults.size
                        tvOnAirResults.addAll(response.body()!!.result!!)
                        tvOnAirAdapter?.notifyItemRangeInserted(oldCount, tvOnAirResults.size)
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                loadingTvOnAir.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setTopRatedTV(view: View) {
        tvTopRatedAdapter = TVAdapter(tvTopRatedResults, requireContext())
        loadingTvTopRated = view.findViewById(R.id.loadingTVTopRated)

        rvTvTopRated = view.findViewById(R.id.rvTVTopRated)
        rvTvTopRated.adapter = tvTopRatedAdapter
        getTopRatedTV(page)

        rvTvTopRated.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvTvTopRated.canScrollHorizontally(1)) {
                    page++
                    getTopRatedTV(page)
                }
            }
        })
    }

    private fun getTopRatedTV(page: Int) {
        val call = apiService?.getTvTopRated(MY_API_KEY, LANGUAGE, page, limit)
        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingTvTopRated.visibility = View.GONE
                        rvTvTopRated.visibility = View.VISIBLE

                        val oldCount = tvTopRatedResults.size
                        tvTopRatedResults.addAll(response.body()!!.result!!)
                        tvTopRatedAdapter?.notifyItemRangeInserted(
                            oldCount,
                            tvTopRatedResults.size
                        )
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                loadingTvTopRated.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setPopularTV(view: View) {
        tvPopularAdapter = TVAdapter(tvPopularResults, requireContext())
        loadingTvPopular = view.findViewById(R.id.loadingTVPopular)

        rvTvPopular = view.findViewById(R.id.rvTVPopular)
        rvTvPopular.adapter = tvPopularAdapter
        getPopularTV(page)

        rvTvPopular.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvTvPopular.canScrollHorizontally(1)) {
                    page++
                    getPopularTV(page)
                }
            }
        })
    }

    private fun getPopularTV(page: Int) {
        val call = apiService?.getTvPopular(MY_API_KEY, LANGUAGE, page, limit)

        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingTvPopular.visibility = View.GONE
                        rvTvPopular.visibility = View.VISIBLE

                        val oldCount = tvPopularResults.size
                        tvPopularResults.addAll(response.body()!!.result!!)
                        tvPopularAdapter?.notifyItemRangeInserted(oldCount, tvPopularResults.size)
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                loadingTvPopular.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setOnAiringTV(view: View) {
        tvAiringTodayAdapter = TVAdapter(tvAiringTodayResults, requireContext())
        loadingTvAiringToday = view.findViewById(R.id.loadingTVAiring)

        rvTvAiringToday = view.findViewById(R.id.rvTVAiring)
        rvTvAiringToday.adapter = tvAiringTodayAdapter
        getOnAiringTV(page)

        rvTvAiringToday.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvTvAiringToday.canScrollHorizontally(1)) {
                    page++
                    getOnAiringTV(page)
                }
            }
        })
    }

    private fun getOnAiringTV(page: Int) {
        val call = apiService?.getTvAiringToday(MY_API_KEY, LANGUAGE, page, limit)

        call!!.enqueue(object : Callback<TVResponse?> {
            override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                if (response.body() != null) {
                    if (response.body()!!.result!!.isNotEmpty()) {
                        loadingTvAiringToday.visibility = View.GONE
                        rvTvAiringToday.visibility = View.VISIBLE

                        val oldCount = tvAiringTodayResults.size
                        tvAiringTodayResults.addAll(response.body()!!.result!!)
                        tvAiringTodayAdapter?.notifyItemRangeInserted(
                            oldCount,
                            tvAiringTodayResults.size
                        )
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                loadingTvAiringToday.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : "
                            + t.cause, Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setTrendingMovies(view: View) {
        movieTrendingAdapter = MovieAdapter(movieTrendingResults, requireContext())
        loadingMovieTrending = view.findViewById(R.id.loadingMovieTrending)

        rvMovieTrending = view.findViewById(R.id.rvMovieTrending)
        trendingMovies

        rvMovieTrending.adapter = movieTrendingAdapter
    }

    private val trendingMovies: Unit
        get() {
            val call = apiService?.getTrendingMovies(TRENDING_MOVIE, TIME_WINDOW, MY_API_KEY)
            call!!.enqueue(object : Callback<MovieResponse?> {
                override fun onResponse(
                    call: Call<MovieResponse?>,
                    response: Response<MovieResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.result!!.isNotEmpty()) {
                            loadingMovieTrending.visibility = View.GONE
                            rvMovieTrending.visibility = View.VISIBLE

                            val oldCount = movieTrendingResults.size
                            movieTrendingResults.addAll(response.body()!!.result!!)
                            movieTrendingAdapter?.notifyItemChanged(
                                oldCount,
                                movieTrendingResults.size
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                    loadingMovieTrending.visibility = View.GONE
                    Toast.makeText(
                        context, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    private fun setTrendingTV(view: View) {
        tvTrendingAdapter = TVAdapter(tvTrendingResults, requireContext())
        loadingTvTrending = view.findViewById(R.id.loadingTVTrending)

        rvTvTrending = view.findViewById(R.id.rvTVTrending)
        trendingTV

        rvTvTrending.adapter = tvTrendingAdapter
    }

    private val trendingTV: Unit
        get() {
            val call = apiService?.getTrendingTV(TRENDING_TV, TIME_WINDOW, MY_API_KEY)
            call!!.enqueue(object : Callback<TVResponse?> {
                override fun onResponse(call: Call<TVResponse?>, response: Response<TVResponse?>) {
                    if (response.body() != null) {
                        if (response.body()!!.result!!.isNotEmpty()) {
                            loadingTvTrending.visibility = View.GONE
                            rvTvTrending.visibility = View.VISIBLE

                            val oldCount = tvTrendingResults.size
                            tvTrendingResults.addAll(response.body()!!.result!!)
                            tvTrendingAdapter?.notifyItemChanged(oldCount, tvTrendingResults.size)
                        }
                    }
                }

                override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                    loadingTvTrending.visibility = View.GONE
                    Toast.makeText(
                        context, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    private fun setTrendingPerson(view: View) {
        personTrendingAdapter = PersonAdapter(personTrendingResults)
        loadingPersonTrending = view.findViewById(R.id.loadingPersonTrending)

        rvPersonTrending = view.findViewById(R.id.rvPersonTrending)
        trendingPerson

        rvPersonTrending.adapter = personTrendingAdapter
    }

    private val trendingPerson: Unit
        get() {
            val call = apiService?.getTrendingPerson(TRENDING_PERSON, TIME_WINDOW, MY_API_KEY)
            call!!.enqueue(object : Callback<PersonResponse?> {
                override fun onResponse(
                    call: Call<PersonResponse?>,
                    response: Response<PersonResponse?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.results!!.isNotEmpty()) {
                            loadingPersonTrending.visibility = View.GONE
                            rvPersonTrending.visibility = View.VISIBLE

                            val oldCount = personTrendingResults.size
                            personTrendingResults.addAll(response.body()!!.results!!)
                            personTrendingAdapter?.notifyItemChanged(
                                oldCount,
                                personTrendingResults.size
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<PersonResponse?>, t: Throwable) {
                    loadingPersonTrending.visibility = View.GONE
                    Toast.makeText(
                        context, t.message + " cause : "
                                + t.cause, Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    companion object {
        const val MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9"
        const val LANGUAGE = "en-US"
        const val TRENDING_MOVIE = "movie"
        const val TRENDING_TV = "tv"
        const val TRENDING_PERSON = "person"
        const val TIME_WINDOW = "week"
    }
}