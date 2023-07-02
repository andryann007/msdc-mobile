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

import com.example.msdc.api.MovieResult;
import com.example.msdc.R;
import com.example.msdc.activities.DetailActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.MovieViewHolder> {
    private final List<MovieResult> movieResults;
    private final Context context;

    public MovieSearchAdapter(List<MovieResult> movieResults, Context context){
        this.movieResults = movieResults;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieSearchAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieSearchAdapter.MovieViewHolder holder, int position) {
        holder.bindItem(movieResults.get(position), context);
    }

    @Override
    public int getItemCount() {
        return movieResults.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder{
        private final RoundedImageView imageItemPoster;
        private final TextView textItemName;

        MovieViewHolder(@NonNull View itemView){
            super(itemView);

            imageItemPoster = itemView.findViewById(R.id.imageItemPoster);
            textItemName = itemView.findViewById(R.id.textItemName);
        }

        void bindItem(MovieResult movieResult, Context context){
            if(!TextUtils.isEmpty(movieResult.getPosterPath())){
                ImageAdapter.setSearchImageBaseURL(imageItemPoster, movieResult.getPosterPath());
            } else {
                imageItemPoster.setImageResource(R.drawable.ic_no_image);
                imageItemPoster.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            textItemName.setText(movieResult.getTitle());

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("type", "movie");
                i.putExtra("movie_id", movieResult.getId());
                context.startActivity(i);
            });
        }
    }
}
