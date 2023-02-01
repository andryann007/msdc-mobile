package com.example.msdc.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("trending/{media_type}/{time_window}")
    Call<MovieResponse> getTrendingMovies(@Path("media_type") String mediaType, @Path("time_window") String timeWindow, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<MovieDetails> getMovieDetails(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/recommendations")
    Call<MovieResponse> getMovieRecommendations(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/similar")
    Call<MovieResponse> getMovieSimilar(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/images")
    Call<ImageResponse> getMovieImages(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<MovieResponse> getMovieReviews(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<CreditResponse> getMovieCredit(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/keywords")
    Call<KeywordResponse> getMovieKeywords(@Path("movie_id") String id, @Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MovieResponse> searchMovie(@Query("api_key") String apiKey, @Query("language") String language, @Query("query") String query, @Query("page") int page);

    @GET("tv/airing_today")
    Call<TVResponse> getTvAiringToday(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/popular")
    Call<TVResponse> getTvPopular(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/top_rated")
    Call<TVResponse> getTvTopRated(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("tv/on_the_air")
    Call<TVResponse> getTvOnAir(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("trending/{media_type}/{time_window}")
    Call<TVResponse> getTrendingTV(@Path("media_type") String mediaType, @Path("time_window") String timeWindow, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}")
    Call<TVDetails> getTvDetails(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/images")
    Call<ImageResponse> getTvImages(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/reviews")
    Call<ImageResponse> getTvReviews(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/keywords")
    Call<KeywordResponse> getTvKeywords(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/credits")
    Call<CreditResponse> getTvCredit(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("search/tv")
    Call<TVResponse> searchTv(@Query("api_key") String apiKey, @Query("language") String language, @Query("query") String query, @Query("page") int page);

    @GET("person/popular")
    Call<PersonResponse> getPopularPerson(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("trending/{media_type}/{time_window}")
    Call<PersonResponse> getTrendingPerson(@Path("media_type") String mediaType, @Path("time_window") String timeWindow, @Query("api_key") String apiKey);

    @GET("person/{person_id}")
    Call<PersonResponse> getPersonDetails(@Path("person_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/recommendations")
    Call<TVResponse> getTVRecommendations(@Path("tv_id") String id, @Query("api_key") String apiKey);

    @GET("tv/{tv_id}/similar")
    Call<TVResponse> getTVSimilar(@Path("tv_id") String id, @Query("api_key") String apiKey);
}