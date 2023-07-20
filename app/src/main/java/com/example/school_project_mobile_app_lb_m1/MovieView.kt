package com.example.school_project_mobile_app_lb_m1

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.compose.material3.*
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.school_project_mobile_app_lb_m1.ui.theme.Schoolprojectmobileapplbm1Theme


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val vm = MovieViewModel()
        super.onCreate(savedInstanceState)
        setContent {
            Schoolprojectmobileapplbm1Theme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "moviesList") {
                    composable("moviesList") { MovieView(vm, navController) }
                    composable(
                        "movieDetail/{movieId}",
                        arguments = listOf(navArgument("movieId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val arguments = requireNotNull(backStackEntry.arguments)
                        val movieId = arguments.getLong("movieId")
                        MovieDetailScreen(movieId, navController)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieView(vm: MovieViewModel, navController: NavController) {
    LaunchedEffect(Unit, block = {
        vm.getMovieList()
    })

    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(vm.movieList) { movie ->
            MovieListItem(movie, navController)
        }
    }

    Scaffold(
        content = {
            if (vm.errorMessage.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(vm.movieList) { movie ->
                        MovieListItem(movie, navController)
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
fun MovieListItem(movie: Movie, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { navController.navigate("movieDetail/${movie.id}") }
        ) {
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
}

@Composable
fun MovieImage(posterPath: String, bigImage: Boolean = false) {
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

@Composable
fun BigMovieImage(posterPath: String) {
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
            .fillMaxWidth()
            .height(300.dp) // Adjust the height based on bigImage flag
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(movieId: Long, navController: NavController) {
    val vm = remember { MovieViewModel() } // Use remember to retain the ViewModel

    // Fetch the movie details using the movieId only once
    // This will prevent unnecessary API calls on recomposition
    LaunchedEffect(movieId) {
        vm.getMovieDetailById(movieId)
    }

    val movieDetail = vm.movieDetail

    // Display the movie details using the 'movieDetail' data
    if (movieDetail != null) {
        Scaffold(
            topBar = {
                CustomAppBar(title = movieDetail.title) {
                    navController.popBackStack()
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Display the big image at the top
                    BigMovieImage(posterPath = movieDetail.posterPath)

                    // Movie info below the image
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = movieDetail.title,
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movieDetail.releaseDate,
                        style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movieDetail.overview,
                        style = TextStyle(fontSize = 16.sp),
                        maxLines = 10,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        )
    } else {
        Text(text = "Erreur d'appel API...")
    }
}

@Composable
fun CustomAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Gray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = title,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}