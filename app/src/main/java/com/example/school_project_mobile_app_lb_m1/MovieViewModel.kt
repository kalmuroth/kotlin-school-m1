package com.example.school_project_mobile_app_lb_m1

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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
}