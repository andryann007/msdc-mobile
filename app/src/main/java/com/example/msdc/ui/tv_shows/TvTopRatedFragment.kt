package com.example.msdc.ui.tv_shows

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
import com.example.msdc.adapter.TVVerticalAdapter
import com.example.msdc.api.ApiClient.client
import com.example.msdc.api.ApiService
import com.example.msdc.api.TVResponse
import com.example.msdc.api.TVResult
import com.example.msdc.databinding.FragmentTvTopRatedBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TvTopRatedFragment : Fragment() {
    private var apiService: ApiService? = null

    private var tvTopRatedAdapter: TVVerticalAdapter? = null

    private val tvTopRatedResults: MutableList<TVResult> = ArrayList()

    private var page = 1

    private lateinit var loadingTvTopRated: ProgressBar
    private lateinit var rvTvTopRated: RecyclerView
    private lateinit var textNoResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val binding = FragmentTvTopRatedBinding.inflate(inflater, container, false)

        loadingTvTopRated = binding.loadingTvTopRatedList
        rvTvTopRated = binding.rvTvTopRatedList
        textNoResult = binding.textNoTvTopRatedResult

        val root: View = binding.root
        val retrofit = client

        apiService = retrofit!!.create(ApiService::class.java)
        setTopRatedTV()

        return root
    }

    private fun setTopRatedTV() {
        tvTopRatedAdapter = TVVerticalAdapter(tvTopRatedResults, requireContext())

        val mPortraitLayoutManager = GridLayoutManager(context, 3)
        val mLandscapeLayoutManager = GridLayoutManager(context, 5)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            rvTvTopRated.layoutManager = mLandscapeLayoutManager
        } else {
            rvTvTopRated.layoutManager = mPortraitLayoutManager
        }
        rvTvTopRated.adapter = tvTopRatedAdapter
        rvTvTopRated.setHasFixedSize(true)
        rvTvTopRated.setItemViewCacheSize(9)

        getTopRatedTV(page)

        rvTvTopRated.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvTvTopRated.canScrollVertically(1)) {
                    page++
                    getTopRatedTV(page)
                }
            }
        })
    }

    private fun getTopRatedTV(page: Int) {
        val limit = 15
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
                    } else {
                        loadingTvTopRated.visibility = View.GONE
                        textNoResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                loadingTvTopRated.visibility = View.GONE
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