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
import com.example.msdc.api.CreditCastResult
import com.makeramen.roundedimageview.RoundedImageView

class CreditCastAdapter(private val castResults: ArrayList<CreditCastResult>) :
    RecyclerView.Adapter<CreditCastAdapter.CreditViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditViewHolder {
        return CreditViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.container_credit, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CreditViewHolder, position: Int) {
        holder.bindItem(castResults[position])
    }

    override fun getItemCount(): Int {
        return castResults.size
    }

    class CreditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val loadingCreditCastImage: ProgressBar
        private val creditPersonImage: RoundedImageView
        private val textPersonName: TextView
        private val textPersonGender: TextView
        private val textPersonDepartment: TextView

        init {
            loadingCreditCastImage = itemView.findViewById(R.id.loadingCreditCrewOrCastImage)
            creditPersonImage = itemView.findViewById(R.id.creditProfilePath)
            textPersonName = itemView.findViewById(R.id.textCastOrCrewName)
            textPersonGender = itemView.findViewById(R.id.textGender)
            textPersonDepartment = itemView.findViewById(R.id.textDepartement)
        }

        fun bindItem(creditCastResult: CreditCastResult) {
            if (!TextUtils.isEmpty(creditCastResult.profilePath)) {
                loadingCreditCastImage.visibility = View.GONE
                ImageAdapter.setProfileLogoURL(creditPersonImage, creditCastResult.profilePath)
            } else {
                loadingCreditCastImage.visibility = View.GONE
                creditPersonImage.setImageResource(R.drawable.ic_no_image)
                creditPersonImage.scaleType = ImageView.ScaleType.FIT_CENTER
            }

            val name : String = creditCastResult.name!!
            setNameText(textPersonName, name)

            val genderCode : Int = creditCastResult.gender!!
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

            val role = creditCastResult.character!!
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