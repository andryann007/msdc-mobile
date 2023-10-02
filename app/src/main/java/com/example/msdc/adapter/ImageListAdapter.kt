package com.example.msdc.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.R
import com.example.msdc.adapter.ImageListAdapter.MovieImageViewHolder
import com.example.msdc.api.ImageResult
import com.makeramen.roundedimageview.RoundedImageView

class ImageListAdapter(private val imageResults: ArrayList<ImageResult>) :
    RecyclerView.Adapter<MovieImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieImageViewHolder {
        return MovieImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_image, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MovieImageViewHolder, position: Int) {
        holder.bindItem(imageResults[position])
    }

    override fun getItemCount(): Int {
        return imageResults.size
    }

    class MovieImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadingImagesList: ProgressBar
        private val imagePath: RoundedImageView

        init {
            loadingImagesList = itemView.findViewById(R.id.loadingImagesList)
            imagePath = itemView.findViewById(R.id.imagesList)
        }

        fun bindItem(imageResult: ImageResult) {
            if (!TextUtils.isEmpty(imageResult.filePath)) {
                loadingImagesList.visibility = View.GONE
                ImageAdapter.setPosterHD(imagePath, imageResult.filePath)
            } else {
                loadingImagesList.visibility = View.GONE
                imagePath.setImageResource(R.drawable.ic_no_image)
            }
        }
    }
}