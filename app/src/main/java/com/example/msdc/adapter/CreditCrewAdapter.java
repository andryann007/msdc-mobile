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
        private final TextView textPersonName, textPersonGender, textPersonDepartement;

        CreditViewHolder(@NonNull View itemView){
            super(itemView);

            creditPersonImage = itemView.findViewById(R.id.creditProfilePath);
            textPersonName = itemView.findViewById(R.id.textCastOrCrewName);
            textPersonGender = itemView.findViewById(R.id.textGender);
            textPersonDepartement = itemView.findViewById(R.id.textDepartement);
        }

        public void bindItem(CreditCrewResult creditCrewResult, Context context) {
            if(!TextUtils.isEmpty(creditCrewResult.getProfilePath())){
                ImageAdapter.setProfileLogoURL(creditPersonImage, creditCrewResult.getProfilePath());
            } else {
                creditPersonImage.setImageResource(R.drawable.ic_android);
            }

            textPersonName.setText(creditCrewResult.getName());

            int genderCode = creditCrewResult.getGender();
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

            textPersonDepartement.setText(creditCrewResult.getJobDesk());
        }
    }
}
