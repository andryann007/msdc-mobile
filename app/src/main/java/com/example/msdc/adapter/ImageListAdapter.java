package com.example.msdc.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.api.ImageResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MovieImageViewHolder>{
    private final List<ImageResult> imageResults;

    public ImageListAdapter(List<ImageResult> imageResults){
        this.imageResults = imageResults;
    }

    @NonNull
    @Override
    public ImageListAdapter.MovieImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageListAdapter.MovieImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.container_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListAdapter.MovieImageViewHolder holder, int position) {
        holder.bindItem(imageResults.get(position));
    }

    @Override
    public int getItemCount() {
        return imageResults.size();
    }

    static class MovieImageViewHolder extends RecyclerView.ViewHolder{
        private final ProgressBar loadingImagesList;
        private final RoundedImageView imagePath;

        MovieImageViewHolder(@NonNull View itemView){
            super(itemView);

            loadingImagesList = itemView.findViewById(R.id.loadingImagesList);
            imagePath = itemView.findViewById(R.id.imagesList);
        }

        public void bindItem(ImageResult imageResult) {
            if(!TextUtils.isEmpty(imageResult.getFilePath())){
                loadingImagesList.setVisibility(View.GONE);
                ImageAdapter.setPosterURL(imagePath, imageResult.getFilePath());
            } else {
                loadingImagesList.setVisibility(View.GONE);
                imagePath.setImageResource(R.drawable.ic_no_image);
            }
        }
    }
}
