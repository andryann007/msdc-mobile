package com.example.msdc.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.activities.DetailActivity;
import com.example.msdc.api.TVResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class TVGridAdapter extends RecyclerView.Adapter<TVGridAdapter.TvViewHolder>{
    private final List<TVResult> tvResults;
    private final Context context;

    public TVGridAdapter(List<TVResult> tvResults, Context context){
        this.tvResults = tvResults;
        this.context = context;
    }

    @NonNull
    @Override
    public TVGridAdapter.TvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new TVGridAdapter.TvViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TVGridAdapter.TvViewHolder holder, int position){
        holder.bindItem(tvResults.get(position), context);
    }

    @Override
    public int getItemCount(){ return tvResults.size();}

    static class TvViewHolder extends RecyclerView.ViewHolder{
        private final RoundedImageView imageItemPoster;
        private final TextView textItemName;

        TvViewHolder(@NonNull View itemView){
            super(itemView);

            imageItemPoster = itemView.findViewById(R.id.imageItemPoster);
            textItemName = itemView.findViewById(R.id.textItemName);
        }

        void bindItem(TVResult tvResult, Context context){
            if(!TextUtils.isEmpty(tvResult.getPosterPath())){
                ImageAdapter.setPosterURL(imageItemPoster, tvResult.getPosterPath());
            } else {
                imageItemPoster.setImageResource(R.drawable.ic_android);
            }
            textItemName.setText(tvResult.getName());

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("tipe", "tv");
                i.putExtra("tv_id", tvResult.getId());
                context.startActivity(i);
            });
        }
    }
}
