package com.example.msdc.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language,
                                         @Query("page") int page, @Query("limit") int limit);

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(@Query("api_key") String apiKey, @Query("language") String language,
                                            @Query("page") int page, @Query("limit") int limit);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(@Query("api_key") String apiKey, @Query("language") String language,
                                          @Query("page") int page, @Query("limit") int limit);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String language,
                                          @Query("page") int page, @Query("limit") int limit);

    @GET("trending/{media_type}/{time_window}")
    Call<MovieResponse> getTrendingMovies(@Path("media_type") String mediaType,
                                          @Path("time_window") String timeWindow,
                                          @Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<MovieDetails> getMovieDetails(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/recommendations")
    Call<MovieResponse> getMovieRecommendations(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/similar")
    Call<MovieResponse> getMovieSimilar(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/images")
    Call<ImageResponse> getMovieImages(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<CreditResponse> getMovieCredit(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/keywords")
    Call<KeywordResponse> getMovieKeywords(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MovieResponse> searchMovie(@Query("api_key") String apiKey, @Query("language") String language,
                                    @Query("query") String query, @Query("page") int page,
                                    @Query("limit") int limit);

    @GET("discover/movie")
    Call<MovieResponse> filterMovie(@Query("api_key") String apiKey, @Query("language") String language,
                                      @Query("page") int page, @Query("limit") int limit,
                                      @Query("with_genres") int genreId, @Query("year") int year,
                                      @Query("region") String region, @Query("sort_by") String sortBy);

    @GET("discover/movie")
    Call<MovieResponse> filterMovieGenre(@Query("api_key") String apiKey, @Query("language") String language,
                                    @Query("page") int page, @Query("limit") int limit,
                                    @Query("with_genres") int genreId, @Query("sort_by") String sortBy);

    @GET("discover/movie")
    Call<MovieResponse> filterMovieKeyword(@Query("api_key") String apiKey, @Query("language") String language,
                                         @Query("page") int page, @Query("limit") int limit,
                                         @Query("with_keywords") int keywordId, @Query("sort_by") String sortBy);

    @GET("discover/movie")
    Call<MovieResponse> filterMovieYear(@Query("api_key") String apiKey, @Query("language") String language,
                                         @Query("page") int page, @Query("limit") int limit,
                                         @Query("year") int year, @Query("sort_by") String sortBy);

    @GET("discover/movie")
    Call<MovieResponse> filterMovieRegion(@Query("api_key") String apiKey, @Query("language") String language,
                                          @Query("page") int page, @Query("limit") int limit,
                                          @Query("region") String region, @Query("sort_by") String sortBy);

    @GET("discover/movie")
    Call<MovieResponse> filterMovieGenreAndYear(@Query("api_key") String apiKey, @Query("language") String language,
                                         @Query("page") int page, @Query("limit") int limit,
                                         @Query("with_genres") int genreId, @Query("year") int year,
                                                @Query("sort_by") String sortBy);

    @GET("discover/movie")
    Call<MovieResponse> filterMovieGenreAndRegion(@Query("api_key") String apiKey, @Query("language") String language,
                                                @Query("page") int page, @Query("limit") int limit,
                                                @Query("with_genres") int genreId, @Query("region") String region,
                                                  @Query("sort_by") String sortBy);

    @GET("discover/movie")
    Call<MovieResponse> filterMovieYearAndRegion(@Query("api_key") String apiKey, @Query("language") String language,
                                                 @Query("page") int page, @Query("limit") int limit,
                                                 @Query("year") int year, @Query("region") String region,
                                                 @Query("sort_by") String sortBy);
    @GET("tv/airing_today")
    Call<TVResponse> getTvAiringToday(@Query("api_key") String apiKey, @Query("language") String language,
                                      @Query("page") int page,  @Query("limit") int limit);

    @GET("tv/popular")
    Call<TVResponse> getTvPopular(@Query("api_key") String apiKey, @Query("language") String language,
                                  @Query("page") int page, @Query("limit") int limit);

    @GET("tv/top_rated")
    Call<TVResponse> getTvTopRated(@Query("api_key") String apiKey, @Query("language") String language,
                                   @Query("page") int page,  @Query("limit") int limit);

    @GET("tv/on_the_air")
    Call<TVResponse> getTvOnAir(@Query("api_key") String apiKey, @Query("language") String language,
                                @Query("page") int page,  @Query("limit") int limit);

    @GET("trending/{media_type}/{time_window}")
    Call<TVResponse> getTrendingTV(@Path("media_type") String mediaType,
                                   @Path("time_window") String timeWindow,
                                   @Query("api_key") String apiKey);

    @GET("tv/{series_id}")
    Call<TVDetails> getTvDetails(@Path("series_id") int id, @Query("api_key") String apiKey);

    @GET("tv/{series_id}/images")
    Call<ImageResponse> getTvImages(@Path("series_id") int id, @Query("api_key") String apiKey);

    @GET("tv/{series_id}/keywords")
    Call<KeywordResponse> getTvKeywords(@Path("series_id") int id, @Query("api_key") String apiKey);

    @GET("tv/{series_id}/credits")
    Call<CreditResponse> getTvCredit(@Path("series_id") int id, @Query("api_key") String apiKey);

    @GET("tv/{series_id}/season/{season_number}")
    Call<SeasonResponse> getTvSeasonAndEpisode(@Path("series_id") int id, @Path("season_number")
                                                int seasonNumber, @Query("api_key") String apiKey);

    @GET("search/tv")
    Call<TVResponse> searchTv(@Query("api_key") String apiKey, @Query("language") String language,
                              @Query("query") String query, @Query("page") int page,
                              @Query("limit") int limit);

    @GET("discover/tv")
    Call<TVResponse> filterTv(@Query("api_key") String apiKey, @Query("language") String language,
                              @Query("page") int page, @Query("limit") int limit,
                              @Query("with_genres") int genreId, @Query("year") int year,
                              @Query("region") String region, @Query("sort_by") String sortBy);

    @GET("discover/tv")
    Call<TVResponse> filterTvGenre(@Query("api_key") String apiKey, @Query("language") String language,
                              @Query("page") int page, @Query("limit") int limit,
                              @Query("with_genres") int genreId, @Query("sort_by") String sortBy);

    @GET("discover/tv")
    Call<TVResponse> filterTvKeyword(@Query("api_key") String apiKey, @Query("language") String language,
                                   @Query("page") int page, @Query("limit") int limit,
                                   @Query("with_keywords") int keywordId, @Query("sort_by") String sortBy);

    @GET("discover/tv")
    Call<TVResponse> filterTvYear(@Query("api_key") String apiKey, @Query("language") String language,
                                   @Query("page") int page, @Query("limit") int limit,
                                  @Query("year") int year, @Query("sort_by") String sortBy);

    @GET("discover/tv")
    Call<TVResponse> filterTvRegion(@Query("api_key") String apiKey, @Query("language") String language,
                                   @Query("page") int page, @Query("limit") int limit,
                                    @Query("region") String region, @Query("sort_by") String sortBy);

    @GET("discover/tv")
    Call<TVResponse> filterTvGenreAndYear(@Query("api_key") String apiKey, @Query("language") String language,
                                   @Query("page") int page, @Query("limit") int limit,
                                   @Query("with_genres") int genreId, @Query("year") int year,
                                          @Query("sort_by") String sortBy);

    @GET("discover/tv")
    Call<TVResponse> filterTvGenreAndRegion(@Query("api_key") String apiKey, @Query("language") String language,
                                          @Query("page") int page, @Query("limit") int limit,
                                          @Query("with_genres") int genreId, @Query("region") String region,
                                          @Query("sort_by") String sortBy);

    @GET("discover/tv")
    Call<TVResponse> filterTvYearAndRegion(@Query("api_key") String apiKey, @Query("language") String language,
                                          @Query("page") int page, @Query("limit") int limit,
                                           @Query("year") int year, @Query("region") String region,
                                          @Query("sort_by") String sortBy);

    @GET("trending/{media_type}/{time_window}")
    Call<PersonResponse> getTrendingPerson(@Path("media_type") String mediaType,
                                           @Path("time_window") String timeWindow,
                                           @Query("api_key") String apiKey);

    @GET("tv/{series_id}/recommendations")
    Call<TVResponse> getTVRecommendations(@Path("series_id") int id, @Query("api_key") String apiKey);

    @GET("tv/{series_id}/similar")
    Call<TVResponse> getTVSimilar(@Path("series_id") int id, @Query("api_key") String apiKey);
}