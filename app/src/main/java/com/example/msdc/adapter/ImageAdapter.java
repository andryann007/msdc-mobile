package com.example.msdc.adapter;

import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageAdapter {
	public static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

	public static final String POSTER_IMAGE_LOGO_URL = "https://image.tmdb.org/t/p/w185";
	
	public static final String PROFILE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185";

    public static final String BACKDROP_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/original";
	
	public static final String SEARCH_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w154";


    public static void setPosterURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(POSTER_IMAGE_BASE_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                    Toast.makeText(imageView.getContext(), e.getMessage() + " cause : "
                            + e.getCause(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setPosterLogoURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(POSTER_IMAGE_LOGO_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                    Toast.makeText(imageView.getContext(), e.getMessage() + " cause : "
                            + e.getCause(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setProfileLogoURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(PROFILE_IMAGE_BASE_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                    Toast.makeText(imageView.getContext(), e.getMessage() + " cause : "
                            + e.getCause(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setSearchImageBaseURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(SEARCH_IMAGE_BASE_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                    Toast.makeText(imageView.getContext(), e.getMessage() + " cause : "
                            + e.getCause(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setBackdropURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(BACKDROP_IMAGE_BASE_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                    Toast.makeText(imageView.getContext(), e.getMessage() + " cause : "
                            + e.getCause(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception ignored){
        }
    }
}
