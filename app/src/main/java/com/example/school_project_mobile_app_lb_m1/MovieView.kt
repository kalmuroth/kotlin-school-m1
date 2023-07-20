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
import androidx.compose.foundation.lazy.items
import coil.compose.rememberImagePainter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.school_project_mobile_app_lb_m1.ui.theme.Schoolprojectmobileapplbm1Theme
import kotlinx.coroutines.launch


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
                    composable("library") { LibraryView(vm, navController) }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieView(vm: MovieViewModel, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        vm.searchMoviesByTitle(searchQuery)
    }

    LaunchedEffect(Unit) {
        vm.getMovieList()
    }

    Scaffold(
        topBar = {
            HomeAppBar(
                title = "Movies",
                searchQuery = searchQuery,
                onSearchConfirmed = { query ->
                    vm.viewModelScope.launch {
                        vm.searchMoviesByTitle(query.toString())
                    }
                }
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(vm.movieList) { movie ->
                    MovieListItem(movie, navController)
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.Gray)
            ) {
                IconButton(
                    onClick = { navController.navigate("moviesList") },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                        Text(text = "Home", color = Color.White)
                    }
                }

                IconButton(
                    onClick = { navController.navigate("library") },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Library",
                            tint = Color.White
                        )
                        Text(text = "Library", color = Color.White)
                    }
                }
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
                    style = TextStyle(fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Release Date: ${movie.releaseDate}",
                    style = TextStyle(color = Color.Gray)
                )
            }
        }
    }
}

@Composable
fun MovieImage(posterPath: String) {
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
            .size(100.dp, 150.dp),
        contentScale = ContentScale.Fit,
    )
}

@ExperimentalMaterial3Api
@Composable
fun HomeAppBar(
    title: String,
    searchQuery: String,
    onSearchConfirmed: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Gray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                expanded = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White
            )
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
        Spacer(modifier = Modifier.weight(1f))
    }

    if (expanded) {
        showSearchDialog(
            searchText = searchQuery,
            onSearchConfirmed = {
                onSearchConfirmed(it)
                expanded = false
            },
            onDismiss = {
                expanded = false
            }
        )
    }
}

@Composable
fun showSearchDialog(
    searchText: String,
    onSearchConfirmed: (String) -> Unit, // Modify the callback to accept a String parameter
    onDismiss: () -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(searchText)) }
    val textFieldBorderColor = Color.Gray
    val cornerRadius = RoundedCornerShape(8.dp)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Search Movie") },
        text = {
            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(textFieldBorderColor, cornerRadius),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSearchConfirmed(textFieldValue.text)
                    Log.w("Movie", "Title 1 : ${textFieldValue.text}")
                }
            ) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}



