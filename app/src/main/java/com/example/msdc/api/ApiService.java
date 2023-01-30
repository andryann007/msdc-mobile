package com.example.msdc.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
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

    @GET("trending/{media_type}/{time_window}")
    Call<MovieRespon> getTrendingMovies(@Path("media_type") String mediaType, @Path("time_window") String timeWindow, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<MovieDetails> getMovieDetails(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/recommendations")
    Call<MovieRespon> getMovieRecommendations(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/similar")
    Call<MovieRespon> getMovieSimilar(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/images")
    Call<ImageRespon> getMovieImages(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<MovieRespon> getMovieReviews(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<MovieRespon> getMovieCredits(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MovieRespon> searchMovie(@Query("api_key") String apiKey, @Query("language") String language, @Query("query") String query, @Query("page") int page);

    @GET("tv/airing_today")
    Call<TVRespon> getTvAiringToday(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/popular")
    Call<TVRespon> getTvPopular(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/top_rated")
    Call<TVRespon> getTvTopRated(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/on_the_air")
    Call<TVRespon> getTvOnAir(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("trending/{media_type}/{time_window}")
    Call<TVRespon> getTrendingTV(@Path("media_type") String mediaType, @Path("time_window") String timeWindow, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}")
    Call<TVDetails> getTvDetails(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/images")
    Call<ImageRespon> getTvImages(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/reviews")
    Call<ImageRespon> getTvReviews(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/credits")
    Call<ImageRespon> getTvCredits(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("search/tv")
    Call<TVRespon> searchTv(@Query("api_key") String apiKey, @Query("language") String language, @Query("query") String query, @Query("page") int page);

    @GET("person/popular")
    Call<PersonRespon> getPopularPerson(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("trending/{media_type}/{time_window}")
    Call<PersonRespon> getTrendingPerson(@Path("media_type") String mediaType, @Path("time_window") String timeWindow, @Query("api_key") String apiKey);

    @GET("person/{person_id}")
    Call<PersonRespon> getPersonDetails(@Path("person_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/recommendations")
    Call<TVRespon> getTVRecommendations(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/similar")
    Call<TVRespon> getTVSimilar(@Path("tv_id") String id, @Query("api_key") String apiKey);
}