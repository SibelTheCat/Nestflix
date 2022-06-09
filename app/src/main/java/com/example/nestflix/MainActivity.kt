package com.example.nestflix

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.OrientationEventListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nestflix.manager.SettingsDataStore
import com.example.nestflix.navigation.NestflixNavigation
import com.example.nestflix.ui.theme.NestflixTheme
import com.example.nestflix.viewmodel.MediaPlayerViewModel
import com.example.nestflix.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.videolan.libvlc.MediaPlayer



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

    override fun onStop() {
        super.onStop()
        mediaPlayerViewModel.mediaPlayer?.stop()
        mediaPlayerViewModel.mediaPlayer?.detachViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerViewModel.mediaPlayer?.release()
    }

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NestflixTheme {
        NestflixNavigation()
    }
}