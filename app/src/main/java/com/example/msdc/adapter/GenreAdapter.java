package com.example.msdc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.activities.DetailActivity;
import com.example.msdc.activities.DiscoverActivity;
import com.example.msdc.api.GenreResult;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder>{
    private final List<GenreResult> genreResults;
    private final Context context;

    public GenreAdapter(List<GenreResult> genreResults, Context context){
        this.genreResults = genreResults;
        this.context = context;
    }

    @NonNull
    @Override
    public GenreAdapter.GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GenreAdapter.GenreViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.GenreViewHolder holder, int position) {
        holder.bindItem(genreResults.get(position), context);
    }

    @Override
    public int getItemCount() {
        return genreResults.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder{
        private final TextView textGenres;

        GenreViewHolder(@NonNull View itemView){
            super(itemView);

            textGenres = itemView.findViewById(R.id.textGenres);
        }

        public void bindItem(GenreResult genreResult, Context context){
            textGenres.setText(genreResult.getName());

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, DiscoverActivity.class);
                i.putExtra("genre_id", genreResult.getId());
                i.putExtra("genre_name", genreResult.getName());
                context.startActivity(i);
            });
        }
    }
}
