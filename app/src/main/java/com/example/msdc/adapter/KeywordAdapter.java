package com.example.msdc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.api.KeywordResult;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder>{
    private final List<KeywordResult> keywordResults;

    public KeywordAdapter(List<KeywordResult> keywordResults){
        this.keywordResults = keywordResults;
    }

    @NonNull
    @Override
    public KeywordAdapter.KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeywordAdapter.KeywordViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.container_keyword, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordAdapter.KeywordViewHolder holder, int position) {
        holder.bindItem(keywordResults.get(position));
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

        public void bindItem(KeywordResult keywordResult){
            textKeywords.setText(keywordResult.getName());
        }
    }
}
