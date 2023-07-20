package com.example.school_project_mobile_app_lb_m1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(movieId: Long, navController: NavController) {
    val vm = viewModel<MovieViewModel>()

    LaunchedEffect(movieId) {
        vm.getMovieDetailById(movieId)
    }

    val movieDetail = vm.movieDetail
    var isLiked by remember { mutableStateOf(false) }

    if (movieDetail != null) {
        isLiked = LikedMovies.likedMovieIds.contains(movieId)

        Scaffold(
            topBar = {
                DetailAppBar(
                    title = movieDetail.title,
                    onBackClick = { navController.popBackStack() },
                    onLikeClick = {
                        vm.toggleLike(movieId)
                        isLiked = !isLiked
                    },
                    isLiked = isLiked
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    BigMovieImage(posterPath = movieDetail.posterPath)

                    Spacer(modifier = Modifier.height(16.dp))

                    StarRating(rating = movieDetail.rating)

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
fun DetailAppBar(
    title: String,
    onBackClick: () -> Unit,
    onLikeClick: () -> Unit, // New callback for the like button
    isLiked: Boolean // New state to determine the like button's appearance
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
            modifier = Modifier
                .weight(1f) // Allow the title to take the available space
                .padding(start = 16.dp)
        )

        IconButton(
            onClick = onLikeClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isLiked) "Liked" else "Not Liked",
                tint = if (isLiked) Color.Green else Color.White
            )
        }
    }
}

@Composable
fun StarRating(rating: Float) {
    val adjustedRating = rating / 2.0f
    val fullStars = adjustedRating.toInt()
    val hasHalfStar = (adjustedRating - fullStars) >= 0.5f

    Row {
        // Full stars
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = Color.Green,
                modifier = Modifier.size(24.dp)
            )
        }

        // Half star
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Default.Star, // Use your custom half star icon
                contentDescription = "Half Star",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        // Empty stars
        val remainingStars = 5 - (fullStars + if (hasHalfStar) 1 else 0)
        repeat(remainingStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Empty Star",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BigMovieImage(posterPath: String) {
    val imageUrl = "https://image.tmdb.org/t/p/original/$posterPath"

    val painter = rememberImagePainter(
        data = imageUrl,
        builder = {
            placeholder(R.drawable.ic_launcher_background)
        }
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
    )
}