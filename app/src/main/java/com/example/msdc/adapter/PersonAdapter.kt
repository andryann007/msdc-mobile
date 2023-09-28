package com.example.msdc.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.R
import com.example.msdc.adapter.PersonAdapter.PersonViewHolder
import com.example.msdc.api.PersonResult
import com.makeramen.roundedimageview.RoundedImageView

class PersonAdapter(private val personResults: List<PersonResult>) :
    RecyclerView.Adapter<PersonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_person, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bindItem(personResults[position])
    }

    override fun getItemCount(): Int {
        return personResults.size
    }

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadingPersonImage: ProgressBar
        private val profilePath: RoundedImageView
        private val personName: TextView

        init {
            loadingPersonImage = itemView.findViewById(R.id.loadingPersonImage)
            profilePath = itemView.findViewById(R.id.profilePath)
            personName = itemView.findViewById(R.id.textPersonName)
        }

        fun bindItem(personResult: PersonResult) {
            if (!TextUtils.isEmpty(personResult.posterPath)) {
                loadingPersonImage.visibility = View.GONE
                ImageAdapter.setProfileLogoURL(profilePath, personResult.posterPath)
            } else {
                loadingPersonImage.visibility = View.GONE
                profilePath.setImageResource(R.drawable.ic_no_image)
                profilePath.scaleType = ImageView.ScaleType.FIT_CENTER
            }

            val name : String = personResult.name!!
            personName.text = name

            itemView.setOnClickListener { }
        }
    }
}