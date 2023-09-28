package com.example.msdc.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.R
import com.example.msdc.activities.DetailActivity
import com.example.msdc.activities.FilterActivity
import com.example.msdc.adapter.GenreAdapter.GenreViewHolder
import com.example.msdc.api.GenreResult
import com.google.android.material.chip.Chip

class GenreAdapter(private val genreResults: ArrayList<GenreResult>, private val context: Context) :
    RecyclerView.Adapter<GenreViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        return GenreViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_genre, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bindItem(genreResults[position], context)
    }

    override fun getItemCount(): Int {
        return genreResults.size
    }

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textGenres: Chip

        init {
            textGenres = itemView.findViewById(R.id.textGenres)
        }

        fun bindItem(genreResult: GenreResult, context: Context) {
            val name : String = genreResult.name!!
            textGenres.text = name

            val type = DetailActivity.type

            if (type.equals("movie", ignoreCase = true)) {
                itemView.setOnClickListener {
                    val i = Intent(context, FilterActivity::class.java)
                    i.putExtra("type", "filter_movie_genre")
                    i.putExtra("genre", genreResult.id)
                    i.putExtra("sortBy", "popularity.desc")
                    context.startActivity(i)
                }
            } else if (type.equals("tv", ignoreCase = true)) {
                itemView.setOnClickListener {
                    val i = Intent(context, FilterActivity::class.java)
                    i.putExtra("type", "filter_tv_genre")
                    i.putExtra("genre", genreResult.id)
                    i.putExtra("sortBy", "popularity.desc")
                    context.startActivity(i)
                }
            }
        }
    }
}