package com.example.msdc.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.msdc.R
import com.example.msdc.api.CreditCrewResult
import com.makeramen.roundedimageview.RoundedImageView

class CreditCrewAdapter(private val crewResults: ArrayList<CreditCrewResult>) :
    RecyclerView.Adapter<CreditCrewAdapter.CreditViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditViewHolder {
        return CreditViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_credit, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CreditViewHolder, position: Int) {
        holder.bindItem(crewResults[position])
    }

    override fun getItemCount(): Int {
        return crewResults.size
    }

    class CreditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadingCreditCrewImage: ProgressBar
        private val creditPersonImage: RoundedImageView
        private val textPersonName: TextView
        private val textPersonGender: TextView
        private val textPersonDepartment: TextView

        init {
            loadingCreditCrewImage = itemView.findViewById(R.id.loadingCreditCrewOrCastImage)
            creditPersonImage = itemView.findViewById(R.id.creditProfilePath)
            textPersonName = itemView.findViewById(R.id.textCastOrCrewName)
            textPersonGender = itemView.findViewById(R.id.textGender)
            textPersonDepartment = itemView.findViewById(R.id.textDepartement)
        }

        fun bindItem(creditCrewResult: CreditCrewResult) {
            if (!TextUtils.isEmpty(creditCrewResult.profilePath)) {
                loadingCreditCrewImage.visibility = View.GONE
                ImageAdapter.setProfileLogoURL(creditPersonImage, creditCrewResult.profilePath)
            } else {
                loadingCreditCrewImage.visibility = View.GONE
                creditPersonImage.setImageResource(R.drawable.ic_no_image)
                creditPersonImage.scaleType = ImageView.ScaleType.FIT_CENTER
            }

            setNameText(textPersonName, creditCrewResult.name)

            val genderCode = creditCrewResult.gender!!
            val genderName: String

            when (genderCode) {
                1 -> {
                    genderName = "Female"
                    setGenderText(textPersonGender, genderName)
                }
                2 -> {
                    genderName = "Male"
                    setGenderText(textPersonGender, genderName)
                }
                else -> {
                    genderName = "-"
                    setGenderText(textPersonGender, genderName)
                }
            }

            val role = creditCrewResult.jobDesk!!
            setRoleText(textPersonDepartment, role)
        }
    }

    companion object {
        private fun setNameText(tv: TextView, textName: String? = "-") {
            tv.text = HtmlCompat.fromHtml(
                "<b>$textName</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }

        private fun setGenderText(tv: TextView, textGender: String) {
            tv.text = HtmlCompat.fromHtml(
                "Gender : <b>$textGender</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }

        private fun setRoleText(tv: TextView, textRole: String? = "-") {
            tv.text = HtmlCompat.fromHtml(
                "Role : <b>$textRole</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }
}