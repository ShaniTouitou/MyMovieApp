package com.example.movieapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * This class is representing the view model of the main activity.
 */
class MainViewModel : ViewModel() {

    // region Members

    // Represent whether the user is searching a movie in this moment.
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // Represent the current text of the movie the user is searching.
    private val _searchMovieText = MutableStateFlow("")
    val searchMovieText = _searchMovieText.asStateFlow()

    // Represent a list of all the movies.
    private val _moviesList = MutableStateFlow<List<Movie>>(emptyList())
    val moviesList = _moviesList.asStateFlow()

    // Represent a list of my favorites movies.
    private val _favoriteMoviesList = MutableStateFlow(createFavoriteMoviesList())
    val favoriteMovies = _favoriteMoviesList.asStateFlow()

    // endregion

    // region Public Methods

    /**
     * This function is to initialize the movies list when the ViewModel is created.
     */
    init {
        fetchMovies()
    }

    /**
     * This function is updating the text while the user is writing.
     * @param text- the text the user is writing to find a movie.
     */
    fun onMovieSearchTextChanged(text: String) {
        _searchMovieText.value = text
        // Updating the movie list to the user searching.
        fetchMovies()
    }

    /**
     * This function is updating whether the user is searching a movie in this moment.
     */
    fun onMovieSearch() {
        _isSearching.value = !_isSearching.value

        // If the user is not searching a movie.
        if (!_isSearching.value) {
            // Clear the search text and the movies list.
            _searchMovieText.value = ""
            fetchMovies()
        }
    }

    // endregion

    // region Private Methods

    /**
     * This function in getting the movies from the api by the searching of the user.
     */
    private fun fetchMovies() {
        // Run only if the view model is active to not using a lot of memories.
        viewModelScope.launch {
            try {
                // Verify that the user searched a movie by writing text.
                if (_searchMovieText.value.isNotBlank()) {
                    // Load the api to create a movies list.
                    val responseBody = loadApi()

                    // If the response we got from the api is not empty.
                    if (responseBody.isNotBlank()) {
                        // Create the movies list.
                        val movies = parseMovies(responseBody)
                        _moviesList.value = movies
                    }
                    else {
                        Log.e("MainViewModel", "Empty response body.")
                    }
                }
                else {
                    // If search text is empty, clear the movies list.
                    _moviesList.value = emptyList()
                }
            }
            catch (e: IOException) {
                Log.e("MainViewModel", "Error fetching movies: ${e.message}")
            }
        }
    }

    /**
     * This function is loading the api with the searching movie text of the user.
     * @return the movies with his details that are matching to the user search.
     */
    private suspend fun loadApi(): String {
        // Running the api loading asynchronous on the network.
        return withContext(Dispatchers.IO) {
            // Make an http request.
            val client = OkHttpClient()

            // Search from the api if there are movies that matching the user searching.
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/search/movie?query=${_searchMovieText.value}&include_adult=true&language=en-US&page=1")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMWNhNzU3MWNjNzI0MTYyZGUyMDJhYzFjM2NhOTRkYyIsIm5iZiI6MTcyMTIyOTYyNC4yODE3MTQsInN1YiI6IjY2OTdkZWU5YjhiZDIwZWQ3YzRlYmQ4NyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.A2x9eeTrYVIdXgSt0rSWmCmRp7Mj9cj_1k5ihgO9I6I")
                .build()

            // Getting the response of the api searching.
            val response = client.newCall(request).execute()

            // When response in successful return the matching movies.
            if (response.isSuccessful) {
                response.body?.string() ?: ""
            }
            else {
                throw IOException("Unsuccessful response: ${response.code}")
            }
        }
    }

    /**
     * This function is parsing the movies to create a list of all the matching user research movies.
     * @param responseBody- the response we got from the api (the matching movies).
     * @return the list of all the matching search movie of the user.
     */
    private suspend fun parseMovies(responseBody: String): List<Movie> {
        // Enable multiple execution of task in parallel.
        return withContext(Dispatchers.Default) {
            // Read the json value.
            val mapper = jacksonObjectMapper()
            val parsedJson = mapper.readValue<Map<String, Any>>(responseBody)
            val results = parsedJson["results"] as List<Map<String, Any>>

            // Create a collection of all the details movies for the movies list.
            results.map {
                Movie(
                    it["id"] as Int,
                    it["title"] as String?,
                    it["original_language"] as String?,
                    it["overview"] as String?,
                    it["popularity"] as Double,
                    it["poster_path"] as String?,
                    it["release_date"] as String?,
                    it["vote_average"] as Double,
                    it["vote_count"] as Int
                )
            }
        }
    }

    /**
     * This function is creating a list of my favorites movies.
     * @return a list of my favorites movies.
     */
    private fun createFavoriteMoviesList() : List<Movie> {
        val favoriteMovies: List<Movie> = listOf(
            Movie(682507, "Where the Crawdads Sing", "en",
                "Abandoned by her family, Kya raises herself all alone in the marshes outside of her small town." +
                        " When her former boyfriend is found dead, Kya is instantly branded by the local townspeople and law enforcement as the prime suspect for his murder.",
                51.212, "/6h5OCqRnWH7Dcm4IeP4JypBdtuI.jpg", "2022-07-14",
                7.545, 1635),

            Movie(321612, "Beauty and the Beast", "en",
                "A live-action adaptation of Disney's version of the classic tale of a cursed prince and a beautiful young woman who helps him break the spell.",
                102.889, "/hKegSKIDep2ewJWPUQD7u0KqFIp.jpg", "2017-03-16",
                6.968, 15248),

            Movie(38757, "Tangled", "en",
                "When the kingdom's most wanted-and most charming-bandit Flynn Rider hides out in a mysterious tower, he's taken hostage by Rapunzel, a beautiful and feisty tower-bound teen with 70 feet of magical, golden hair. Flynn's curious captor, who's looking for her ticket out of the tower where she's been locked away for years, strikes a deal with the handsome thief and the unlikely duo sets off on an action-packed escapade, complete with a super-cop horse, an over-protective chameleon and a gruff gang of pub thugs.",
                130.603,
                "/ym7Kst6a4uodryxqbGOxmewF235.jpg",
                "2010-11-24", 7.607, 11171),

            Movie(9602, "Coming to America", "en",
                "An African prince decides it’s time for him to find a princess... and his mission leads him and his most loyal friend to Queens, New York. In disguise as an impoverished immigrant, the pampered prince quickly finds himself a new job, new friends, new digs, new enemies and lots of trouble.",
                62.817,
                "/djRAvxyvvN2yqlJKDbT3uy4vOBw.jpg",
                "1988-06-29",
                6.882, 4218),

            Movie( 637920, "Miracle in Cell No. 7", "tr",
                "Separated from his daughter, a father with an intellectual disability must prove his innocence when he is jailed for the death of a commander's child.",
                58.285,  "/bOth4QmNyEkalwahfPCfiXjNh1r.jpg", "2019-10-10",
                8.266, 4330))

        // return the list of my favorites movies.
        return favoriteMovies
    }

    // endregion

}
