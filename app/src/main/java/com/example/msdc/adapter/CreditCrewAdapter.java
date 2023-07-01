package com.example.msdc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.api.CreditCrewResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CreditCrewAdapter extends RecyclerView.Adapter<CreditCrewAdapter.CreditViewHolder>{
    private final List<CreditCrewResult> crewResults;
    private final Context context;

    public CreditCrewAdapter(List<CreditCrewResult> crewResults, Context context){
        this.crewResults = crewResults;
        this.context = context;
    }

    @NonNull
    @Override
    public CreditCrewAdapter.CreditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CreditCrewAdapter.CreditViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.credit_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CreditCrewAdapter.CreditViewHolder holder, int position) {
        holder.bindItem(crewResults.get(position), context);
    }

    @Override
    public int getItemCount() {
        return crewResults.size();
    }

    static class CreditViewHolder extends RecyclerView.ViewHolder{
        private final RoundedImageView creditPersonImage;
        private final TextView textPersonName, textPersonGender, textPersonDepartment;

        CreditViewHolder(@NonNull View itemView){
            super(itemView);

            creditPersonImage = itemView.findViewById(R.id.creditProfilePath);
            textPersonName = itemView.findViewById(R.id.textCastOrCrewName);
            textPersonGender = itemView.findViewById(R.id.textGender);
            textPersonDepartment = itemView.findViewById(R.id.textDepartement);
        }

        public void bindItem(CreditCrewResult creditCrewResult, Context context) {
            if(!TextUtils.isEmpty(creditCrewResult.getProfilePath())){
                ImageAdapter.setProfileLogoURL(creditPersonImage, creditCrewResult.getProfilePath());
            } else {
                creditPersonImage.setImageResource(R.drawable.ic_android);
                creditPersonImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            setNameText(textPersonName, creditCrewResult.getName());

            int genderCode = creditCrewResult.getGender();
            String genderName;

            if(genderCode == 1){
                genderName = "Female";
                setGenderText(textPersonGender, genderName);
            } else if(genderCode == 2){
                genderName = "Male";
                setGenderText(textPersonGender, genderName);
            } else {
                genderName = "-";
                setGenderText(textPersonGender, genderName);
            }

            setRoleText(textPersonDepartment, creditCrewResult.getJobDesk());
        }
    }

    private static void setNameText(TextView tv, String textName){
        tv.setText(HtmlCompat.fromHtml("Name : <b>" + textName + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private static void setGenderText(TextView tv, String textGender){
        tv.setText(HtmlCompat.fromHtml("Gender : <b>" + textGender + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private static  void setRoleText(TextView tv, String textRole){
        tv.setText(HtmlCompat.fromHtml("Role : <b>" + textRole + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    }
}
