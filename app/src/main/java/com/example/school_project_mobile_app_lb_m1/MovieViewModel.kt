package com.example.school_project_mobile_app_lb_m1

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


object LikedMovies {
    val likedMovieIds = mutableSetOf<Long>()
}

class MovieViewModel : ViewModel() {
    private val _movieList = mutableStateListOf<Movie>()
    var errorMessage: String by mutableStateOf("")
    val movieList: List<Movie>
        get() = _movieList

    fun getMovieList() {
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                _movieList.clear()
                _movieList.addAll(apiService.getMovies().results) // Access the movie list from MovieResponse

            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    private val _movieDetail = mutableStateOf<Movie?>(null)
    val movieDetail: Movie? get() = _movieDetail.value
    fun getMovieDetailById(movieId: Long) {
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                _movieDetail.value = apiService.getMovieDetail(movieId)
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    suspend fun searchMoviesByTitle(title: String) {
        val apiService = APIService.getInstance()
        try {
            _movieList.clear()
            _movieList.addAll(apiService.searchMoviesByTitle(query = title).results)
        } catch (e: Exception) {
            Log.e("MovieViewModel", "Error: ${e.message}")
        }
    }

    fun toggleLike(movieId: Long) {
        if (LikedMovies.likedMovieIds.contains(movieId)) {
            LikedMovies.likedMovieIds.remove(movieId)
        } else {
            LikedMovies.likedMovieIds.add(movieId)
        }
    }

    fun getLikedMovies(): List<Movie> {
        val likedMovies = LikedMovies.likedMovieIds
        return movieList.filter { movie -> movie.id in likedMovies }
    }
}