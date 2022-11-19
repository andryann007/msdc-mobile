package com.example.msdc;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class TVAdapter extends RecyclerView.Adapter<TVAdapter.TvViewHolder>{
    private final List<TVResult> TVResults;
    private final Context context;

    public TVAdapter(List<TVResult> TVResults, Context context){
        this.TVResults = TVResults;
        this.context = context;
    }

    @NonNull
    @Override
    public TvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new TvViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TvViewHolder holder, int position){
        holder.bindItem(TVResults.get(position), context);
    }

    @Override
    public int getItemCount(){ return TVResults.size();}

    static class TvViewHolder extends RecyclerView.ViewHolder{
        private final RoundedImageView imageItemPoster;
        private final TextView textItemName;

        TvViewHolder(@NonNull View itemView){
            super(itemView);

            imageItemPoster = itemView.findViewById(R.id.imageItemPoster);
            textItemName = itemView.findViewById(R.id.textItemName);
        }

        void bindItem(TVResult TVResult, Context context){
            if(!TextUtils.isEmpty(TVResult.getPosterPath())){
                ImageAdapter.setPosterURL(imageItemPoster, TVResult.getPosterPath());
            } else {
                imageItemPoster.setImageResource(R.drawable.ic_android);
            }
            textItemName.setText(TVResult.getName());

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("tipe", "tv");
                i.putExtra("id", TVResult.getId());
                context.startActivity(i);
            });
        }
    }
}