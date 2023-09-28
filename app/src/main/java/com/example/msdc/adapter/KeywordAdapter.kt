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
import com.example.msdc.adapter.KeywordAdapter.KeywordViewHolder
import com.example.msdc.api.KeywordResult
import com.google.android.material.chip.Chip

class KeywordAdapter(
    private val keywordResults: ArrayList<KeywordResult>,
    private val context: Context
) : RecyclerView.Adapter<KeywordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder {
        return KeywordViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_keyword, parent, false)
        )
    }

    override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
        holder.bindItem(keywordResults[position], context)
    }

    override fun getItemCount(): Int {
        return keywordResults.size
    }

    class KeywordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textKeywords: Chip

        init {
            textKeywords = itemView.findViewById(R.id.textKeywords)
        }

        fun bindItem(keywordResult: KeywordResult, context: Context) {
            val name : String = keywordResult.name!!
            textKeywords.text = name

            val type = DetailActivity.type

            if (type.equals("movie", ignoreCase = true)) {
                itemView.setOnClickListener {
                    val i = Intent(context, FilterActivity::class.java)
                    i.putExtra("type", "filter_movie_keyword")
                    i.putExtra("keyword", keywordResult.id)
                    i.putExtra("keyword_name", keywordResult.name)
                    i.putExtra("sortBy", "popularity.desc")
                    context.startActivity(i)
                }
            } else if (type.equals("tv", ignoreCase = true)) {
                itemView.setOnClickListener {
                    val i = Intent(context, FilterActivity::class.java)
                    i.putExtra("type", "filter_tv_keyword")
                    i.putExtra("keyword", keywordResult.id)
                    i.putExtra("keyword_name", keywordResult.name)
                    i.putExtra("sortBy", "popularity.desc")
                    context.startActivity(i)
                }
            }
        }
    }
}