package com.example.msdc.ui.movie

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.adapter.MovieVerticalAdapter
import com.example.msdc.api.ApiClient.client
import com.example.msdc.api.ApiService
import com.example.msdc.api.MovieResponse
import com.example.msdc.api.MovieResult
import com.example.msdc.databinding.FragmentMovieNowPlayingBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieNowPlayingFragment : Fragment() {
    private var apiService: ApiService? = null

    private var movieNowPlayingAdapter: MovieVerticalAdapter? = null

    private val movieNowPlayingResults: MutableList<MovieResult> = ArrayList()

    private var page = 1

    private lateinit var loadingMovieNowPlaying: ProgressBar
    private lateinit var rvMovieNowPlaying: RecyclerView
    private lateinit var textNoResult: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val binding = FragmentMovieNowPlayingBinding.inflate(inflater, container, false)

        loadingMovieNowPlaying = binding.loadingMovieNowPlayingList
        rvMovieNowPlaying = binding.rvMovieNowPlayingList
        textNoResult = binding.textNoMovieNowPlayingResult

        val root: View = binding.root
        val retrofit = client

        apiService = retrofit!!.create(ApiService::class.java)
        setNowPlayingMovies()

        return root
    }

    private fun setNowPlayingMovies() {
        movieNowPlayingAdapter = MovieVerticalAdapter(movieNowPlayingResults, requireContext())

        val mPortraitLayoutManager = GridLayoutManager(context, 3)
        val mLandscapeLayoutManager = GridLayoutManager(context, 5)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            rvMovieNowPlaying.layoutManager = mLandscapeLayoutManager
        } else {
            rvMovieNowPlaying.layoutManager = mPortraitLayoutManager
        }
        rvMovieNowPlaying.adapter = movieNowPlayingAdapter
        rvMovieNowPlaying.setHasFixedSize(true)
        rvMovieNowPlaying.setItemViewCacheSize(9)

        getNowPlayingMovies(page)
        rvMovieNowPlaying.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvMovieNowPlaying.canScrollVertically(1)) {
                    page++
                    getNowPlayingMovies(page)
                }
            }
        })
    }

    private fun getNowPlayingMovies(page: Int) {
        val limit = 15
        val call = apiService?.getNowPlayingMovies(MY_API_KEY, LANGUAGE, page, limit)

        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
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
                    } else {
                        loadingMovieNowPlaying.visibility = View.GONE
                        textNoResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                loadingMovieNowPlaying.visibility = View.GONE
                Toast.makeText(
                    context, t.message + " cause : " + t.cause,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    companion object {
        const val MY_API_KEY = "9bfd8a12ca22a52a4787b3fd80269ea9"
        const val LANGUAGE = "en-US"
    }
}