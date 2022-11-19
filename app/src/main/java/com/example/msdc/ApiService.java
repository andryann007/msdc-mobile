package com.example.msdc;

import android.graphics.Movie;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("movie/popular")
    Call<MovieRespon> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/now_playing")
    Call<MovieRespon> getNowPlayingMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/upcoming")
    Call<MovieRespon> getUpcomingMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieRespon> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("search/movie")
    Call<MovieRespon> searchMovie(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<MovieRespon> getMovieDetails(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/popular")
    Call<MovieRespon> getTvPopular(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/top_rated")
    Call<MovieRespon> getTvTopRated(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/on_the_air")
    Call<MovieRespon> getTvOnAir(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/{tv_id}")
    Call<MovieRespon> getTvDetails(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("search/tv")
    Call<MovieRespon> searchTv(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);
}