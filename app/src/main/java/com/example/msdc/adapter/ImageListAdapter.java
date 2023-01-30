package com.example.msdc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.api.ImageResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MovieImageViewHolder>{
    private final List<ImageResult> imageResults;
    private final Context context;

    public ImageListAdapter(List<ImageResult> imageResults, Context context){
        this.imageResults = imageResults;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageListAdapter.MovieImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageListAdapter.MovieImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListAdapter.MovieImageViewHolder holder, int position) {
        holder.bindItem(imageResults.get(position), context);
    }

    @Override
    public int getItemCount() {
        return imageResults.size();
    }

    static class MovieImageViewHolder extends RecyclerView.ViewHolder{
        private final RoundedImageView imagePath;

        MovieImageViewHolder(@NonNull View itemView){
            super(itemView);

            imagePath = itemView.findViewById(R.id.imagesList);
        }

        public void bindItem(ImageResult imageResult, Context context) {
            if(!TextUtils.isEmpty(imageResult.getFilePath())){
                ImageAdapter.setBackdropListURL(imagePath, imageResult.getFilePath());
            } else {
                imagePath.setImageResource(R.drawable.ic_android);
            }
        }
    }
}
