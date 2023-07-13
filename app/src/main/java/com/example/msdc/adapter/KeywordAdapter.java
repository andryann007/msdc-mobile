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
import com.example.msdc.activities.FilterActivity;
import com.example.msdc.api.KeywordResult;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder>{
    private final List<KeywordResult> keywordResults;
    private final Context context;

    public KeywordAdapter(List<KeywordResult> keywordResults, Context context){
        this.keywordResults = keywordResults;
        this.context = context;
    }

    @NonNull
    @Override
    public KeywordAdapter.KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeywordAdapter.KeywordViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.container_keyword, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordAdapter.KeywordViewHolder holder, int position) {
        holder.bindItem(keywordResults.get(position), context);
    }

    @Override
    public int getItemCount() {
        return keywordResults.size();
    }

    static class KeywordViewHolder extends RecyclerView.ViewHolder{
        private final TextView textKeywords;

        KeywordViewHolder(@NonNull View itemView){
            super(itemView);

            textKeywords = itemView.findViewById(R.id.textKeywords);
        }

        public void bindItem(KeywordResult keywordResult, Context context){
            textKeywords.setText(keywordResult.getName());

            String type = DetailActivity.type;

            if(type.equalsIgnoreCase("movie")){
                itemView.setOnClickListener(v -> {
                    Intent i = new Intent(context, FilterActivity.class);
                    i.putExtra("type", "filter_movie_keyword");
                    i.putExtra("keyword", keywordResult.getId());
                    i.putExtra("keyword_name", keywordResult.getName());
                    i.putExtra("sortBy", "popularity.desc");
                    context.startActivity(i);
                });
            } else if(type.equalsIgnoreCase("tv")){
                itemView.setOnClickListener(v -> {
                    Intent i = new Intent(context, FilterActivity.class);
                    i.putExtra("type", "filter_tv_keyword");
                    i.putExtra("keyword", keywordResult.getId());
                    i.putExtra("keyword_name", keywordResult.getName());
                    i.putExtra("sortBy", "popularity.desc");
                    context.startActivity(i);
                });
            }
        }
    }
}
