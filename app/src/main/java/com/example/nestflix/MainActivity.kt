package com.example.nestflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.nestflix.navigation.NestflixNavigation
import com.example.nestflix.ui.theme.NestflixTheme
import dagger.hilt.android.AndroidEntryPoint
import org.videolan.libvlc.MediaPlayer

//dependency container -> we get the dependencies here
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NestflixTheme {
                // A surface container using the 'background' color from the theme
                NestflixNavigation()
                }
            }
        }
    }




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NestflixTheme {
        NestflixNavigation()
    }
}