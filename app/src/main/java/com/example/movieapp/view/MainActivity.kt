package com.example.movieapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.movieapp.ui.theme.MovieAppTheme

/**
 * This class represent the main activity.
 */
class MainActivity : ComponentActivity() {

    // region OnCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Call the main screen activity function.
                    MainScreen()
                }
            }
        }
    }

    // endregion

}


