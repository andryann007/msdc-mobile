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
import com.example.msdc.api.TVResult
import com.makeramen.roundedimageview.RoundedImageView

class TVAdapter(private val tvResults: List<TVResult>, private val context: Context) :
    RecyclerView.Adapter<TVAdapter.TvViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvViewHolder {
        return TvViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TvViewHolder, position: Int) {
        holder.bindItem(tvResults[position], context)
    }

    override fun getItemCount(): Int {
        return tvResults.size
    }

    class TvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadingItemImage: ProgressBar
        private val imageItemPoster: RoundedImageView
        private val textItemName: TextView

        init {
            loadingItemImage = itemView.findViewById(R.id.loadingItemImage)
            imageItemPoster = itemView.findViewById(R.id.imageItemPoster)
            textItemName = itemView.findViewById(R.id.textItemName)
        }

        fun bindItem(tvResult: TVResult, context: Context) {
            if (!TextUtils.isEmpty(tvResult.posterPath)) {
                loadingItemImage.visibility = View.GONE
                ImageAdapter.setPosterLogoURL(imageItemPoster, tvResult.posterPath)
            } else {
                loadingItemImage.visibility = View.GONE
                imageItemPoster.setImageResource(R.drawable.ic_no_image)
                imageItemPoster.scaleType = ImageView.ScaleType.FIT_CENTER
            }

            val name : String = tvResult.name!!
            textItemName.text = name

            itemView.setOnClickListener {
                val i = Intent(context, DetailActivity::class.java)
                i.putExtra("type", "tv")
                i.putExtra("series_id", tvResult.id)
                context.startActivity(i)
            }
        }
    }
}