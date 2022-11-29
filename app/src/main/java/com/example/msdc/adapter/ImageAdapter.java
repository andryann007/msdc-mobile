package com.example.msdc.adapter;

import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageAdapter {
    public static final String IMAGEBASE_URL = "https://image.tmdb.org/t/p/w220_and_h330_face";
    public static final String BACKDROPIMAGE_BASE_URL = "https://image.tmdb.org/t/p/original";

    public static void setPosterURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(IMAGEBASE_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setBackdropURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(BACKDROPIMAGE_BASE_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }
}
