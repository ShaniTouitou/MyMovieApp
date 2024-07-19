package com.example.movieapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.movieapp.R
import com.example.movieapp.model.Movie
import com.example.movieapp.viewModel.MainViewModel
import java.net.URI

/**
 * This class is representing the details of thr main activity.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {

    // region Members

    val viewModel = MainViewModel()

    val context = LocalContext.current

    val isSearching by viewModel.isSearching.collectAsState()
    val searchMovieText by viewModel.searchMovieText.collectAsState()
    val moviesListState by viewModel.moviesList.collectAsState()
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()

    // endregion

    // region Ui

    Scaffold(
        topBar = {
            Column {
                // Represent the search bar to search a movie.
                SearchBar(
                    placeholder = {
                        Text(
                            text = "Search your movie",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "search icon") },
                    query = searchMovieText,
                    onQueryChange = viewModel::onMovieSearchTextChanged,
                    onSearch = viewModel::onMovieSearchTextChanged,
                    active = isSearching,
                    onActiveChange = { viewModel.onMovieSearch() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp) ,

                    // Represent the filter in the search bar- for advanced research.
                    trailingIcon = {
                        Icon (
                            imageVector = Icons.Default.Create,
                            contentDescription = "filterIcon",
                            modifier = Modifier.clickable {  }.padding(horizontal = 12.dp)
                        )
                    }
                ) {
                    // Represent the content of the search movie-
                    // the list of the movies that are researching.
                    LazyColumn {
                        items(moviesListState) { movie ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    // When user is clicking on a movie.
                                    .clickable {
                                        // Pass the movie object to the movie details activity-
                                        // to display the details of the movie.
                                        displayMovieDetails(context, movie)
                                    }
                            ) {
                                // Represent the image of the movies -
                                // by getting the image path and with uri create a image that we can display for the user.
                                movie.imgPath?.let { img ->
                                    val uri = URI.create("https://image.tmdb.org/t/p/w500$img")
                                    val painter = rememberImagePainter(
                                        data = uri.toASCIIString(),
                                        builder = {
                                            crossfade(true)
                                            scale(Scale.FILL)
                                            placeholder(R.drawable.movie_icon)
                                            error(R.drawable.movie_icon)
                                        }
                                    )
                                    Image(
                                        painter = painter,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                                // Represent the title of the movie.
                                Text(
                                    text = movie.title ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Represent my favorite movies that will be display in the start of the app-
                // without searching them.
                Text(
                    text = "Favorite Movies",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 28.sp),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
                )
                // Display my favorite movies.
                LazyColumn {
                    items(favoriteMovies) { favoriteMovie ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                // When user is clicking on a movie.
                                .clickable {
                                    // Pass the movie object to the movie details activity-
                                    // to display the details of the movie.
                                    displayMovieDetails(context, favoriteMovie)
                                }
                        ) {
                            // Represent the image of the movies -
                            // by getting the image path and with uri create a image that we can display for the user.
                            favoriteMovie.imgPath?.let { img ->
                                val uri = URI.create("https://image.tmdb.org/t/p/w500$img")
                                val painter = rememberImagePainter(
                                    data = uri.toASCIIString(),
                                    builder = {
                                        crossfade(true)
                                        scale(Scale.FILL)
                                        placeholder(R.drawable.movie_icon)
                                        error(R.drawable.movie_icon)
                                    }
                                )
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                            // The title of the movie.
                            Text(
                                text = favoriteMovie.title ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    ) {}

    // endregion

}

// region Private Methods

/**
 * This function is to pass the movie object to the movie details activity-
 * to display the details of the movie.
 * @param context- the context of the current activity.
 * @param movie- the chosen movie by the user.
 */
private fun displayMovieDetails(context : Context, movie: Movie) {
    val intent = Intent(context, MovieDetailsActivity::class.java)
    intent.putExtra("chosenMovie", movie)
    context.startActivity(intent)
}

// endregion
