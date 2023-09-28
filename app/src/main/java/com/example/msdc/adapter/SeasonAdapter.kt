package com.example.msdc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.R
import com.example.msdc.adapter.SeasonAdapter.SeasonViewHolder
import com.example.msdc.api.EpisodeResult
import com.makeramen.roundedimageview.RoundedImageView

class SeasonAdapter(private val episodeResults: ArrayList<EpisodeResult>) :
    RecyclerView.Adapter<SeasonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        return SeasonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_tv_episodes, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        holder.bindItem(episodeResults[position])
    }

    override fun getItemCount(): Int {
        return episodeResults.size
    }

    class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadingImgEpisodePath: ProgressBar
        private val imgEpisodePath: RoundedImageView
        private val textTitle: TextView
        private val textReleaseDate: TextView
        private val textOverview: TextView

        init {
            loadingImgEpisodePath = itemView.findViewById(R.id.loadingTvEpisodePath)
            imgEpisodePath = itemView.findViewById(R.id.tvEpisodePath)
            textTitle = itemView.findViewById(R.id.textTvEpisodeNumberAndName)
            textReleaseDate = itemView.findViewById(R.id.textTvReleaseDate)
            textOverview = itemView.findViewById(R.id.textTvEpisodeOverview)
        }

        fun bindItem(episodeResult: EpisodeResult) {
            if (episodeResult.stillPath != null) {
                loadingImgEpisodePath.visibility = View.GONE
                ImageAdapter.setPosterURL(imgEpisodePath, episodeResult.stillPath)
            } else {
                loadingImgEpisodePath.visibility = View.GONE
                imgEpisodePath.setImageResource(R.drawable.ic_no_image)
                imgEpisodePath.scaleType = ImageView.ScaleType.FIT_CENTER
            }

            val season : Int = episodeResult.seasonNumber!!
            val episode : Int = episodeResult.episodeNumber!!
            val title : String = episodeResult.name!!
            if (season != 0 && episode != 0 && title.isNotEmpty()) {
                setEpisodeTitle(textTitle, season, episode, title)
            }

            val releaseDate : String = episodeResult.airDate!!
            if (releaseDate.isNotEmpty()) {
                setEpisodeReleaseDate(textReleaseDate, releaseDate)
            } else {
                setNoResult(textReleaseDate, "Not Release Yet !!!")
            }

            val overview : String = episodeResult.overview!!
            if (overview.isNotEmpty()) {
                setEpisodeOverview(textOverview, overview)
            } else {
                setNoResult(textOverview, "No Description Yet !!!")
            }
        }
    }

    companion object {
        private fun setEpisodeTitle(tv: TextView, season: Int, episode: Int, title: String? = "-") {
            tv.text = HtmlCompat.fromHtml(
                "<b>Season " + season + " / Episode " + episode
                        + " : " + title + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }

        private fun setEpisodeReleaseDate(tv: TextView, releaseDate: String? = "-") {
            tv.text = HtmlCompat.fromHtml(
                "Release Date : <b>$releaseDate</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }

        private fun setEpisodeOverview(tv: TextView, overview: String? = "-") {
            tv.text = HtmlCompat.fromHtml(
                "Description : <b>$overview</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }

        private fun setNoResult(tv: TextView, note: String) {
            tv.text = HtmlCompat.fromHtml(
                "<font color='#FF2400'><b>$note</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }
}