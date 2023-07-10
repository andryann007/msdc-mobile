package com.example.msdc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msdc.R;
import com.example.msdc.api.EpisodeResult;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder> {
    private final ArrayList<EpisodeResult> episodeResults;

    public SeasonAdapter(ArrayList<EpisodeResult> episodeResults) {
        this.episodeResults = episodeResults;
    }

    @NonNull
    @Override
    public SeasonAdapter.SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeasonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.container_tv_episodes, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonAdapter.SeasonViewHolder holder, int position) {
        holder.bindItem(episodeResults.get(position));
    }

    @Override
    public int getItemCount() {
        return episodeResults.size();
    }

    public static class SeasonViewHolder extends RecyclerView.ViewHolder{
        private final ProgressBar loadingImgEpisodePath;
        private final RoundedImageView imgEpisodePath;
        private final TextView textTitle, textReleaseDate, textRuntime, textOverview;

        public SeasonViewHolder(@NonNull View itemView) {
            super(itemView);

            loadingImgEpisodePath = itemView.findViewById(R.id.loadingTvEpisodePath);
            imgEpisodePath = itemView.findViewById(R.id.tvEpisodePath);
            textTitle = itemView.findViewById(R.id.textTvEpisodeNumberAndName);
            textReleaseDate = itemView.findViewById(R.id.textTvReleaseDate);
            textRuntime = itemView.findViewById(R.id.textTvEpisodeRuntime);
            textOverview = itemView.findViewById(R.id.textTvEpisodeOverview);
        }

        public void bindItem(EpisodeResult episodeResult) {
            if(episodeResult.getStillPath() != null){
                loadingImgEpisodePath.setVisibility(View.GONE);
                ImageAdapter.setPosterURL(imgEpisodePath, episodeResult.getStillPath());
            } else{
                loadingImgEpisodePath.setVisibility(View.GONE);
                imgEpisodePath.setImageResource(R.drawable.ic_no_image);
                imgEpisodePath.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            int season = episodeResult.getSeasonNumber();
            int episode = episodeResult.getEpisodeNumber();
            String title = episodeResult.getName();

            if(season!=0 && episode!=0 && !title.isEmpty()){
                setEpisodeTitle(textTitle, season, episode, title);
            }

            String releaseDate = episodeResult.getAirDate();
            if(!releaseDate.isEmpty()){
                setEpisodeReleaseDate(textReleaseDate, releaseDate);
            } else {
                setNoResult(textReleaseDate, "Not Release Yet !!!");
            }

            int runTime = episodeResult.getRuntime();
            if(runTime != 0){
                setEpisodeRuntime(textRuntime, runTime);
            } else {
                setNoResult(textRuntime, "No Duration Yet !!!");
            }

            String overview = episodeResult.getOverview();
            if(!overview.isEmpty()){
                setEpisodeOverview(textOverview, overview);
            } else {
                setNoResult(textOverview, "No Description Yet !!!");
            }
        }
    }

    private static void setEpisodeTitle(TextView tv, int season, int episode, String title){
        tv.setText(HtmlCompat.fromHtml("<b>Season " + season + " / Episode " + episode
                + " : " + title + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private static void setEpisodeReleaseDate(TextView tv, String releaseDate){
        tv.setText(HtmlCompat.fromHtml("Release Date : <b>" + releaseDate + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private static void setEpisodeRuntime(TextView tv, int runTime){
        tv.setText(HtmlCompat.fromHtml("Duration : <b>" + runTime + " minutes</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private static void setEpisodeOverview(TextView tv, String overview){
        tv.setText(HtmlCompat.fromHtml("Description : <b>" + overview + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private static void setNoResult(TextView tv, String note){
        tv.setText(HtmlCompat.fromHtml("<font color='#FF2400'><b>" + note + "</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
    }
}
