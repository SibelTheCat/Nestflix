package com.example.nestflix.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource


class MediaPlayerViewModel  : ViewModel() {
    var  player: ExoPlayer? = null
    var videoSource: RtspMediaSource? = null

    //Variable für den Bufferscreen -> wird von 2 Composables in StreamScreen verwendet
    var open = MutableLiveData<Boolean>(true)


}