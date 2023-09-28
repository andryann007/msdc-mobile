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
import com.example.msdc.databinding.FragmentMoviePopularBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviePopularFragment : Fragment() {
    private var apiService: ApiService? = null

    private var moviePopularAdapter: MovieVerticalAdapter? = null

    private val moviePopularResults: MutableList<MovieResult> = ArrayList()

    private var page = 1

    private lateinit var loadingMoviePopular: ProgressBar
    private lateinit var rvMoviePopular: RecyclerView
    private lateinit var textNoResult: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val binding = FragmentMoviePopularBinding.inflate(inflater, container, false)

        loadingMoviePopular = binding.loadingMoviePopularList
        rvMoviePopular = binding.rvMoviePopularList
        textNoResult = binding.textNoMoviePopularResult

        val root: View = binding.root
        val retrofit = client

        apiService = retrofit!!.create(ApiService::class.java)
        setPopularMovies()

        return root
    }

    private fun setPopularMovies() {
        moviePopularAdapter = MovieVerticalAdapter(moviePopularResults, requireContext())

        val mPortraitLayoutManager = GridLayoutManager(context, 3)
        val mLandscapeLayoutManager = GridLayoutManager(context, 5)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            rvMoviePopular.layoutManager = mLandscapeLayoutManager
        } else {
            rvMoviePopular.layoutManager = mPortraitLayoutManager
        }
        rvMoviePopular.adapter = moviePopularAdapter
        rvMoviePopular.setHasFixedSize(true)
        rvMoviePopular.setItemViewCacheSize(9)

        getPopularMovies(page)

        rvMoviePopular.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvMoviePopular.canScrollVertically(1)) {
                    page++
                    getPopularMovies(page)
                }
            }
        })
    }

    private fun getPopularMovies(page: Int) {
        val limit = 15
        val call = apiService?.getPopularMovies(MY_API_KEY, LANGUAGE, page, limit)

        call!!.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
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
                    } else {
                        loadingMoviePopular.visibility = View.GONE
                        textNoResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                loadingMoviePopular.visibility = View.GONE
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