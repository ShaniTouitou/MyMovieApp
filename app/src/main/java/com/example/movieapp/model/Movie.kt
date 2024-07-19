package com.example.movieapp.model

import java.io.Serializable

/**
 * This class is representing an movie object.
 * @param id- the id of the movie.
 * @param title- the title of the movie.
 * @param language- the original language of the movie.
 * @param overview- the overview of the movie.
 * @param popularity- the popularity of the movie.
 * @param imgPath- the image path of the movie to see the image of the movie.
 * @param releaseDate- the release date of the movie.
 * @param voteAverage- the vote average of the movie.
 * @param voteCount- the vote count of the movie.
 */
data class Movie(val id: Int, val title: String?, val language: String?,
    val overview: String?, val popularity: Double, val imgPath: String?,
    val releaseDate: String?, val voteAverage: Double, val voteCount: Int) : Serializable
