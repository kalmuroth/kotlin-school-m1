package com.example.school_project_mobile_app_lb_m1

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.items
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.size.Scale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val vm = MovieViewModel()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MovieView(vm)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieView(vm: MovieViewModel) {
    LaunchedEffect(Unit, block = {
        vm.getMovieList()
    })

    Scaffold(
        content = {
            if (vm.errorMessage.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(vm.movieList) { movie ->
                        MovieListItem(movie)
                    }
                }
            } else {
                Text(vm.errorMessage)
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MovieListItem(movie: Movie) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        MovieImage(posterPath = movie.posterPath)
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = movie.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = movie.overview,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = androidx.compose.ui.text.TextStyle(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Release Date: ${movie.releaseDate}",
                style = androidx.compose.ui.text.TextStyle(color = Color.Gray)
            )
        }
    }
}

@Composable
fun MovieImage(posterPath: String) {
    // Prepend "https://" to the posterPath URL
    val imageUrl = "https://image.tmdb.org/t/p/original/$posterPath"

    val painter = rememberImagePainter(
        data = imageUrl, // Use the corrected URL with "https://"
        builder = {
            placeholder(R.drawable.ic_launcher_background) // Replace with your placeholder image resource
        }
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(100.dp, 150.dp),
        contentScale = ContentScale.Fit, // Use ContentScale.Fit instead of ContentScale.Crop
    )
}