package com.example.movieapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.movieapp.R
import com.example.movieapp.model.Movie
import com.example.movieapp.ui.theme.MovieAppTheme
import java.net.URI

/**
 * This class representing the movie details activity that is opening when clicking on a movie.
 */
class MovieDetailsActivity : ComponentActivity() {

    // region OnCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Get the object movie the user clicked on to display his details.
                    val chosenMovie = intent.getSerializableExtra("chosenMovie") as? Movie
                    chosenMovie?.let {
                        // Pass the chose movie to the function.
                        MovieDetails(chosenMovie)
                    }
                }
            }
        }
    }

    // endregion

}

// region Ui

/**
 * This function is to create the movie details screen by his ui.
 * @param chosenMovie- the movie the user clicked on.
 */
@Composable
fun MovieDetails(chosenMovie: Movie) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            // Make the screen scrollable to show all the details,
            // even if they are longer than the screen can display.
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // Represent the image of the movies -
        // by getting the image path and with uri create a image that we can display for the user.
        val uri = URI.create("https://image.tmdb.org/t/p/w500${chosenMovie.imgPath}")
        val painter = rememberImagePainter(
            data = uri.toASCIIString(),
            builder = {
                crossfade(true)
                // Represent the image we are displaying until the movie image is loading.
                placeholder(R.drawable.movie_icon)
                error(R.drawable.movie_icon)
            }
        )
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f/16f)
                .padding(top = 0.dp),
            contentScale = ContentScale.FillWidth
        )

        // The row for the title and share button.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title
            Text(
                text = chosenMovie.title ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 28.sp),
                modifier = Modifier
                    .weight(1f)
            )

            // Represent the share button to optionally share the movie with all his details.
            IconButton(
                onClick = {
                    shareMovieDetails(context, chosenMovie)
                },
                modifier = Modifier
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color(0xFF039be5)
                )
            }
        }

        // Represent the movie details.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${chosenMovie.language} | " +
                        "${chosenMovie.releaseDate} | " +
                        "${chosenMovie.voteAverage}/10 | " +
                        "${chosenMovie.voteCount} votes",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Represent the overview of the movie.
        Text(
            text = "${chosenMovie.overview}",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
        )

    }
}

// endregion

// region Private Methods

/**
 * This function is sharing the chosen movie with his details.
 * @param context- the context of the current activity.
 * @param movie- the chosen movie by the user.
 */
private fun shareMovieDetails(context: Context, chosenMovie: Movie) {
    // Create an intent for the share option.
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    // Pass the chosen movie to the share intent.
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, chosenMovie.title)

    // Create the message the user will share with all the movie details.
    val shareMessage = """
        Title: ${chosenMovie.title}
        Language: ${chosenMovie.language}
        Overview: ${chosenMovie.overview}
        Release Date: ${chosenMovie.releaseDate}
        Vote Average: ${chosenMovie.voteAverage}
        Vote Count: ${chosenMovie.voteCount}
    """.trimIndent()

    // Open the sharing option with the message we created.
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
    context.startActivity(Intent.createChooser(shareIntent, "Share movie details using"))
}

// endregion