package com.example.msdc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.api.CreditCastResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CreditCastAdapter extends RecyclerView.Adapter<CreditCastAdapter.CreditViewHolder> {
    private final List<CreditCastResult> castResults;
    private final Context context;

    public CreditCastAdapter(List<CreditCastResult> castResults, Context context){
        this.castResults = castResults;
        this.context = context;
    }

    @NonNull
    @Override
    public CreditCastAdapter.CreditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CreditCastAdapter.CreditViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.credit_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CreditCastAdapter.CreditViewHolder holder, int position) {
        holder.bindItem(castResults.get(position), context);
    }

    @Override
    public int getItemCount() {
        return castResults.size();
    }

    static class CreditViewHolder extends RecyclerView.ViewHolder{
        private final RoundedImageView creditPersonImage;
        private final TextView textPersonName, textPersonGender, textPersonDepartement;

        CreditViewHolder(@NonNull View itemView){
            super(itemView);

            creditPersonImage = itemView.findViewById(R.id.creditProfilePath);
            textPersonName = itemView.findViewById(R.id.textCastOrCrewName);
            textPersonGender = itemView.findViewById(R.id.textGender);
            textPersonDepartement = itemView.findViewById(R.id.textDepartement);
        }

        public void bindItem(CreditCastResult creditCastResult, Context context) {
            if(!TextUtils.isEmpty(creditCastResult.getProfilePath())){
                ImageAdapter.setProfileLogoURL(creditPersonImage, creditCastResult.getProfilePath());
            } else {
                creditPersonImage.setImageResource(R.drawable.ic_android);
            }

            textPersonName.setText(creditCastResult.getName());

            int genderCode = creditCastResult.getGender();
            String genderName;

            if(genderCode == 1){
                genderName = "Female";
                textPersonGender.setText(genderName);
            } else if(genderCode == 2){
                genderName = "Male";
                textPersonGender.setText(genderName);
            } else {
                genderName = "Unknown";
                textPersonGender.setText(genderName);
            }

            textPersonDepartement.setText(creditCastResult.getCharacter());
        }
    }
}
