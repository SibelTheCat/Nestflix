package com.example.nestflix.viewmodel

import androidx.lifecycle.ViewModel
import org.videolan.libvlc.MediaPlayer

class MediaPlayerViewModel  : ViewModel() {
    var mediaPlayer: MediaPlayer? = null
}