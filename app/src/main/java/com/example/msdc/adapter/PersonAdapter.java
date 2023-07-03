package com.example.msdc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.api.PersonResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{
    private final List<PersonResult> personResults;

    public PersonAdapter(List<PersonResult> personResults) {
        this.personResults = personResults;
    }

    @NonNull
    @Override
    public PersonAdapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PersonAdapter.PersonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.PersonViewHolder holder, int position) {
        holder.bindItem(personResults.get(position));
    }

    @Override
    public int getItemCount() {
        return personResults.size();
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder{
        private final ProgressBar loadingPersonImage;
        private final RoundedImageView profilePath;
        private final TextView personName;

        PersonViewHolder(@NonNull View itemView){
            super(itemView);

            loadingPersonImage = itemView.findViewById(R.id.loadingPersonImage);
            profilePath = itemView.findViewById(R.id.profilePath);
            personName = itemView.findViewById(R.id.textPersonName);
        }

        void bindItem(PersonResult personResult){
            if(!TextUtils.isEmpty(personResult.getPosterPath())){
                loadingPersonImage.setVisibility(View.GONE);
                ImageAdapter.setProfileLogoURL(profilePath, personResult.getPosterPath());
            } else {
                loadingPersonImage.setVisibility(View.GONE);
                profilePath.setImageResource(R.drawable.ic_no_image);
                profilePath.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            personName.setText(personResult.getName());

            itemView.setOnClickListener(v -> {

            });
        }
    }
}
