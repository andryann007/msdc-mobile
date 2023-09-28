package com.example.msdc.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.R
import com.example.msdc.activities.DetailActivity
import com.example.msdc.api.MovieResult
import com.makeramen.roundedimageview.RoundedImageView

class MovieVerticalAdapter(
    private val movieResults: List<MovieResult>,
    private val context: Context
) : RecyclerView.Adapter<MovieVerticalAdapter.MovieViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_item_vertical, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bindItem(movieResults[position], context)
    }

    override fun getItemCount(): Int {
        return movieResults.size
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadingVerticalItemImage: ProgressBar
        private val imageItemPoster: RoundedImageView
        private val textItemName: TextView

        init {
            loadingVerticalItemImage = itemView.findViewById(R.id.loadingVerticalItemImage)
            imageItemPoster = itemView.findViewById(R.id.imageItemPoster)
            textItemName = itemView.findViewById(R.id.textItemName)
        }

        fun bindItem(movieResult: MovieResult, context: Context) {
            if (!TextUtils.isEmpty(movieResult.posterPath)) {
                loadingVerticalItemImage.visibility = View.GONE
                ImageAdapter.setPosterURL(imageItemPoster, movieResult.posterPath)
            } else {
                loadingVerticalItemImage.visibility = View.GONE
                imageItemPoster.setImageResource(R.drawable.ic_no_image)
                imageItemPoster.scaleType = ImageView.ScaleType.FIT_CENTER
            }

            val title : String = movieResult.title!!
            textItemName.text = title

            itemView.setOnClickListener {
                val i = Intent(context, DetailActivity::class.java)
                i.putExtra("type", "movie")
                i.putExtra("movie_id", movieResult.id)
                context.startActivity(i)
            }
        }
    }
}