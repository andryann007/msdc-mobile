package com.example.msdc.adapter;

import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageAdapter {
	public static final String POSTERIMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342";
	public static final String POSTERIMAGE_LOGO_URL = "https://image.tmdb.org/t/p/w185";
	public static final String POSTERIMAGE_ORIGINAL_URL = "https://image.tmdb.org/t/p/original";
	public static final String POSTERIMAGE_LOGO_DETAIL_URL = "https://image.tmdb.org/t/p/w92";
	
	public static final String PROFILEIMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185";
	public static final String PROFILEIMAGE_ORIGINAL_URL = "https://image.tmdb.org/t/p/original";
	
	public static final String BACKDROPIMAGE_BASE_URL = "https://image.tmdb.org/t/p/original";
	public static final String BACKDROPIMAGE_LIST_URL = "https://image.tmdb.org/t/p/w300";
	
	public static final String SEARCHIMAGE_BASE_URL = "https://image.tmdb.org/t/p/w154";
    public static final String IMAGEBASE_URL = "https://image.tmdb.org/t/p/w220_and_h330_face";
   

    public static void setPosterURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(POSTERIMAGE_BASE_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setPosterLogoURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(POSTERIMAGE_LOGO_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setPosterOriginalURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(POSTERIMAGE_ORIGINAL_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setPosterLogoDetailURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(POSTERIMAGE_LOGO_DETAIL_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }
	
	public static void setBackdropListURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(BACKDROPIMAGE_LIST_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setProfileLogoURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(PROFILEIMAGE_BASE_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setProfileOriginalURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(PROFILEIMAGE_ORIGINAL_URL + path ).noFade().into(imageView, new Callback(){
                @Override
                public void onSuccess(){imageView.animate().setDuration(500).alpha(1f).start();}

                @Override
                public void onError(Exception e){
                }
            });
        } catch(Exception ignored){
        }
    }

    public static void setSearchImageBaseURL(ImageView imageView, String path){
        try{
            imageView.setAlpha(0f);
            Picasso.get().load(SEARCHIMAGE_BASE_URL + path ).noFade().into(imageView, new Callback(){
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
