package com.example.msdc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.api.PersonResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{
    private final List<PersonResult> personResults;
    private final Context context;

    public PersonAdapter(List<PersonResult> personResults, Context context) {
        this.personResults = personResults;
        this.context = context;
    }

    @NonNull
    @Override
    public PersonAdapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PersonAdapter.PersonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.PersonViewHolder holder, int position) {
        holder.bindItem(personResults.get(position), context);
    }

    @Override
    public int getItemCount() {
        return personResults.size();
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder{
        private final RoundedImageView profilePath;
        private final TextView personName;

        PersonViewHolder(@NonNull View itemView){
            super(itemView);

            profilePath = itemView.findViewById(R.id.profilePath);
            personName = itemView.findViewById(R.id.textPersonName);
        }

        void bindItem(PersonResult personResult, Context context){
            if(!TextUtils.isEmpty(personResult.getPosterPath())){
                ImageAdapter.setProfileLogoURL(profilePath, personResult.getPosterPath());
            } else {
                profilePath.setImageResource(R.drawable.ic_android);
                profilePath.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            personName.setText(personResult.getName());

            itemView.setOnClickListener(v -> {

            });
        }
    }
}
