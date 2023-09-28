package com.example.msdc.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<MovieResponse?>?

    @GET("movie/now_playing")
    fun getNowPlayingMovies(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<MovieResponse?>?

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<MovieResponse?>?

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<MovieResponse?>?

    @GET("trending/{media_type}/{time_window}")
    fun getTrendingMovies(
        @Path("media_type") mediaType: String?,
        @Path("time_window") timeWindow: String?,
        @Query("api_key") apiKey: String?
    ): Call<MovieResponse?>?

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<MovieDetails?>?

    @GET("movie/{movie_id}/recommendations")
    fun getMovieRecommendations(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<MovieResponse?>?

    @GET("movie/{movie_id}/similar")
    fun getMovieSimilar(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<MovieResponse?>?

    @GET("movie/{movie_id}/images")
    fun getMovieImages(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<ImageResponse?>?

    @GET("movie/{movie_id}/credits")
    fun getMovieCredit(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<CreditResponse?>?

    @GET("movie/{movie_id}/keywords")
    fun getMovieKeywords(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<KeywordResponse?>?

    @GET("search/movie")
    fun searchMovie(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("query") query: String?, @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<MovieResponse?>?

    @GET("discover/movie")
    fun filterMovie(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_genres") genreId: Int, @Query("year") year: Int,
        @Query("region") region: String?, @Query("sort_by") sortBy: String?
    ): Call<MovieResponse?>?

    @GET("discover/movie")
    fun filterMovieGenre(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_genres") genreId: Int, @Query("sort_by") sortBy: String?
    ): Call<MovieResponse?>?

    @GET("discover/movie")
    fun filterMovieKeyword(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_keywords") keywordId: Int, @Query("sort_by") sortBy: String?
    ): Call<MovieResponse?>?

    @GET("discover/movie")
    fun filterMovieYear(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("year") year: Int, @Query("sort_by") sortBy: String?
    ): Call<MovieResponse?>?

    @GET("discover/movie")
    fun filterMovieRegion(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("region") region: String?, @Query("sort_by") sortBy: String?
    ): Call<MovieResponse?>?

    @GET("discover/movie")
    fun filterMovieGenreAndYear(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_genres") genreId: Int, @Query("year") year: Int,
        @Query("sort_by") sortBy: String?
    ): Call<MovieResponse?>?

    @GET("discover/movie")
    fun filterMovieGenreAndRegion(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_genres") genreId: Int, @Query("region") region: String?,
        @Query("sort_by") sortBy: String?
    ): Call<MovieResponse?>?

    @GET("discover/movie")
    fun filterMovieYearAndRegion(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("year") year: Int, @Query("region") region: String?,
        @Query("sort_by") sortBy: String?
    ): Call<MovieResponse?>?

    @GET("tv/airing_today")
    fun getTvAiringToday(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<TVResponse?>?

    @GET("tv/popular")
    fun getTvPopular(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<TVResponse?>?

    @GET("tv/top_rated")
    fun getTvTopRated(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<TVResponse?>?

    @GET("tv/on_the_air")
    fun getTvOnAir(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<TVResponse?>?

    @GET("trending/{media_type}/{time_window}")
    fun getTrendingTV(
        @Path("media_type") mediaType: String?,
        @Path("time_window") timeWindow: String?,
        @Query("api_key") apiKey: String?
    ): Call<TVResponse?>?

    @GET("tv/{series_id}")
    fun getTvDetails(
        @Path("series_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<TVDetails?>?

    @GET("tv/{series_id}/images")
    fun getTvImages(
        @Path("series_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<ImageResponse?>?

    @GET("tv/{series_id}/credits")
    fun getTvCredit(
        @Path("series_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<CreditResponse?>?

    @GET("tv/{series_id}/season/{season_number}")
    fun getTvSeasonAndEpisode(
        @Path("series_id") id: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String?
    ): Call<SeasonResponse?>?

    @GET("search/tv")
    fun searchTv(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("query") query: String?, @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<TVResponse?>?

    @GET("discover/tv")
    fun filterTv(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_genres") genreId: Int, @Query("year") year: Int,
        @Query("region") region: String?, @Query("sort_by") sortBy: String?
    ): Call<TVResponse?>?

    @GET("discover/tv")
    fun filterTvGenre(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_genres") genreId: Int, @Query("sort_by") sortBy: String?
    ): Call<TVResponse?>?

    @GET("discover/tv")
    fun filterTvKeyword(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_keywords") keywordId: Int, @Query("sort_by") sortBy: String?
    ): Call<TVResponse?>?

    @GET("discover/tv")
    fun filterTvYear(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("year") year: Int, @Query("sort_by") sortBy: String?
    ): Call<TVResponse?>?

    @GET("discover/tv")
    fun filterTvRegion(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("region") region: String?, @Query("sort_by") sortBy: String?
    ): Call<TVResponse?>?

    @GET("discover/tv")
    fun filterTvGenreAndYear(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_genres") genreId: Int, @Query("year") year: Int,
        @Query("sort_by") sortBy: String?
    ): Call<TVResponse?>?

    @GET("discover/tv")
    fun filterTvGenreAndRegion(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("with_genres") genreId: Int, @Query("region") region: String?,
        @Query("sort_by") sortBy: String?
    ): Call<TVResponse?>?

    @GET("discover/tv")
    fun filterTvYearAndRegion(
        @Query("api_key") apiKey: String?, @Query("language") language: String?,
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("year") year: Int, @Query("region") region: String?,
        @Query("sort_by") sortBy: String?
    ): Call<TVResponse?>?

    @GET("trending/{media_type}/{time_window}")
    fun getTrendingPerson(
        @Path("media_type") mediaType: String?,
        @Path("time_window") timeWindow: String?,
        @Query("api_key") apiKey: String?
    ): Call<PersonResponse?>?

    @GET("tv/{series_id}/recommendations")
    fun getTVRecommendations(
        @Path("series_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<TVResponse?>?

    @GET("tv/{series_id}/similar")
    fun getTVSimilar(
        @Path("series_id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<TVResponse?>?
}