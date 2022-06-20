package com.example.nestflix


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.nestflix.navigation.NestflixNavigation
import com.example.nestflix.ui.theme.NestflixTheme
import com.example.nestflix.viewmodel.MediaPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val mediaPlayerViewModel: MediaPlayerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NestflixTheme {
                // A surface container using the 'background' color from the theme
               // val mediaPlayerViewModel: MediaPlayerViewModel by viewModels()
                NestflixNavigation(mediaPlayerViewModel = mediaPlayerViewModel)

            }
        }
    }
    override fun onPause() {
        super.onPause()
        mediaPlayerViewModel.player?.pause()
    }
    override fun onStop() {
        super.onStop()
        mediaPlayerViewModel.player?.pause()

    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerViewModel.player?.release()
    }

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NestflixTheme {
        NestflixNavigation()
    }
}