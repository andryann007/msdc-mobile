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
import com.example.msdc.databinding.FragmentTvOnAirBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TvOnAirFragment : Fragment() {
    private var apiService: ApiService? = null

    private var tvOnAirAdapter: TVVerticalAdapter? = null

    private val tvOnAirResults: MutableList<TVResult> = ArrayList()

    private var page = 1

    private lateinit var loadingTvOnAir: ProgressBar
    private lateinit var rvTvOnAir: RecyclerView
    private lateinit var textNoResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val binding = FragmentTvOnAirBinding.inflate(inflater, container, false)

        loadingTvOnAir = binding.loadingTvOnAirList
        rvTvOnAir = binding.rvTvOnAirList
        textNoResult = binding.textNoTvOnAirResult

        val root: View = binding.root
        val retrofit = client

        apiService = retrofit!!.create(ApiService::class.java)
        setOnAirTV()

        return root
    }

    private fun setOnAirTV() {
        tvOnAirAdapter = TVVerticalAdapter(tvOnAirResults, requireContext())

        val mPortraitLayoutManager = GridLayoutManager(context, 3)
        val mLandscapeLayoutManager = GridLayoutManager(context, 5)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            rvTvOnAir.layoutManager = mLandscapeLayoutManager
        } else {
            rvTvOnAir.layoutManager = mPortraitLayoutManager
        }
        rvTvOnAir.adapter = tvOnAirAdapter
        rvTvOnAir.setHasFixedSize(true)
        rvTvOnAir.setItemViewCacheSize(9)

        getOnAirTV(page)

        rvTvOnAir.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvTvOnAir.canScrollVertically(1)) {
                    page++
                    getOnAirTV(page)
                }
            }
        })
    }

    private fun getOnAirTV(page: Int) {
        val limit = 15
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
                    } else {
                        loadingTvOnAir.visibility = View.GONE
                        textNoResult.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TVResponse?>, t: Throwable) {
                loadingTvOnAir.visibility = View.GONE
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