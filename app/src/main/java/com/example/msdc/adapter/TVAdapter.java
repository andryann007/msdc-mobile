package com.example.msdc.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.activities.DetailActivity;
import com.example.msdc.api.TVResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class TVAdapter extends RecyclerView.Adapter<TVAdapter.TvViewHolder>{
    private final List<TVResult> tvResults;
    private final Context context;

    public TVAdapter(List<TVResult> tvResults, Context context){
        this.tvResults = tvResults;
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
                ImageAdapter.setPosterLogoURL(imageItemPoster, tvResult.getPosterPath());
            } else {
                imageItemPoster.setImageResource(R.drawable.ic_android);
                imageItemPoster.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            textItemName.setText(tvResult.getName());

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("type", "tv");
                i.putExtra("tv_id", tvResult.getId());
                context.startActivity(i);
            });
        }
    }
}
