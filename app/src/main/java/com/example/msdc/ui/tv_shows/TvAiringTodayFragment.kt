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
import com.example.msdc.databinding.FragmentTvAiringTodayBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TvAiringTodayFragment : Fragment() {
    private var apiService: ApiService? = null

    private var tvAiringTodayAdapter: TVVerticalAdapter? = null

    private val tvAiringTodayResults: MutableList<TVResult> = ArrayList()

    private var page = 1

    private lateinit var loadingTvAiringToday: ProgressBar
    private lateinit var rvTvAiringToday: RecyclerView
    private lateinit var textNoResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val binding = FragmentTvAiringTodayBinding.inflate(inflater, container, false)

        loadingTvAiringToday = binding.loadingTvAiringTodayList
        rvTvAiringToday = binding.rvTvAiringTodayList
        textNoResult = binding.textNoTvAiringTodayResult

        val root: View = binding.root
        val retrofit = client

        apiService = retrofit!!.create(ApiService::class.java)
        setOnAiringTV()

        return root
    }

    private fun setOnAiringTV() {
        tvAiringTodayAdapter = TVVerticalAdapter(tvAiringTodayResults, requireContext())

        val mPortraitLayoutManager = GridLayoutManager(context, 3)
        val mLandscapeLayoutManager = GridLayoutManager(context, 5)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            rvTvAiringToday.layoutManager = mLandscapeLayoutManager
        } else {
            rvTvAiringToday.layoutManager = mPortraitLayoutManager
        }
        rvTvAiringToday.adapter = tvAiringTodayAdapter
        rvTvAiringToday.setHasFixedSize(true)
        rvTvAiringToday.setItemViewCacheSize(9)

        getOnAiringTV(page)

        rvTvAiringToday.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvTvAiringToday.canScrollVertically(1)) {
                    page++
                    getOnAiringTV(page)
                }
            }
        })
    }

    private fun getOnAiringTV(page: Int) {
        val limit = 15
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
                    } else {
                        loadingTvAiringToday.visibility = View.GONE
                        textNoResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                loadingTvAiringToday.visibility = View.GONE
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