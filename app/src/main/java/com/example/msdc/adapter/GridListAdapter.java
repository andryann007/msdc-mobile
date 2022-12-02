package com.example.msdc.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.activities.DetailActivity;
import com.example.msdc.api.MovieResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class GridListAdapter extends RecyclerView.Adapter<GridListAdapter.GridViewHolder>{
    private final List<MovieResult> movieResultList;
    private final OnGridItemSelectedListener onGridItemSelectedListener;

    public GridListAdapter(List<MovieResult> movieResult, OnGridItemSelectedListener onGridItemSelectedListener) {
        this.movieResultList = movieResult;
        this.onGridItemSelectedListener = onGridItemSelectedListener;
        movieResult = new ArrayList<>();
    }

    public void add(MovieResult item){
        movieResultList.add(item);
        notifyItemInserted(movieResultList.size()-1);
    }

    public MovieResult getItem(int position){
        return movieResultList.get(position);
    }

    @NonNull
    @Override
    public GridListAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single, parent, false);
        final GridViewHolder gridViewHolder = new GridViewHolder(view);
        gridViewHolder.itemView.setOnClickListener(v->{
            int adapterPos = gridViewHolder.getAdapterPosition();
            if(adapterPos!= RecyclerView.NO_POSITION){
                if(onGridItemSelectedListener != null){
                    onGridItemSelectedListener.onGridItemClick(gridViewHolder.itemView, adapterPos);
                }
            }
        });
        return gridViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GridListAdapter.GridViewHolder holder, int position) {
        final MovieResult movieResult = movieResultList.get(position);
        holder.img.setImageResource(Integer.parseInt(movieResult.getPosterPath()));
        holder.tv.setText(movieResult.getTitle());
    }

    @Override
    public int getItemCount() {
        return movieResultList.size();
    }
    public static class GridViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView img;
        public TextView tv;

        public GridViewHolder(View itemView){
            super(itemView);
            img = itemView.findViewById(R.id.singleImageItemPoster);
            tv = itemView.findViewById(R.id.singleTextItemName);
        }
    }
    public interface OnGridItemSelectedListener{
        void onGridItemClick(View v, int position);
    }
}
