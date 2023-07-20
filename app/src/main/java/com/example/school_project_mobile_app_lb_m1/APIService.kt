package com.example.school_project_mobile_app_lb_m1

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class Movie(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("backdrop_path") val backdropPath: String,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("release_date") val releaseDate: String
)
data class MovieResponse(
    @SerializedName("results") val results: List<Movie>
)

const val BASE_URL = "https://api.themoviedb.org/3/"

interface APIService {
    @GET("movie/popular")
    suspend fun getMovies(
        @Query("api_key") apiKey: String = "8462a368268d6817ff5947a2f3027998",
        @Query("page") page: Int = 1
    ): MovieResponse // Change the return type to MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Long,
        @Query("api_key") apiKey: String = "8462a368268d6817ff5947a2f3027998"
    ): Movie

    companion object {
        var apiService: APIService? = null
        fun getInstance(): APIService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}