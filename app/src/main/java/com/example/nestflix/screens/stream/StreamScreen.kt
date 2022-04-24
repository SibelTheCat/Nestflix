package com.example.nestflix.screens.stream


import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Environment.*
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nestflix.R
import okhttp3.internal.http2.Http2Reader
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

import kotlin.math.roundToInt



//var mediaPlayer: MediaPlayer? = null

@Composable
fun StreamScreen(navController: NavController = rememberNavController(), mediaPlayer: MediaPlayer? = null) {
    var mediaPlayer: MediaPlayer? = null

    var image : Bitmap? = null
    var text : String = "test"

    val view = LocalView.current


    //var jetCaptureView: MutableState<View>? = null
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
    val openDialog = remember { mutableStateOf(false)  }



    var libVLC: LibVLC?
   // var mediaPlayer: MediaPlayer?
    val testurl = "rtsp://rtsp.stream/pattern"
    //"rtsp://rtsp.stream/pattern"
    //"tcp/h264://10.0.0.134:55555"
    val raspberry = "rtsp://10.0.0.134:3366/stream1"


    Scaffold( modifier = Modifier.onGloballyPositioned {
        capturingViewBounds = it.boundsInRoot()
    },
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant, elevation = 3.dp) {
                Row {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow back",
                        modifier = Modifier.clickable {
                            navController.popBackStack() //go back to last screen
                        })

                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = "Stream")
                }

            }
        },
        bottomBar = {
            BottomAppBar(
                elevation = 10.dp,
                cutoutShape = RoundedCornerShape(18.dp),
                backgroundColor = MaterialTheme.colors.primaryVariant
            ) {}
        },
        floatingActionButton = {
            ExtendedFloatingActionButton({
                Text(text = "Capture Screenshot", color = Color.White)},
                onClick = {

                    val bounds = capturingViewBounds ?: return@ExtendedFloatingActionButton
                        image = Bitmap.createBitmap(
                        bounds.width.roundToInt(), bounds.height.roundToInt(),
                        Bitmap.Config.ARGB_8888
                    ).applyCanvas {
                        this.translate((-bounds.left), (-bounds.top))
                        view.draw(this)
                    }

                    openDialog.value = true
                          },
                shape = RoundedCornerShape(18.dp),
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_camera_alt_24),
                        contentDescription = null
                    )
                }
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        backgroundColor = MaterialTheme.colors.primaryVariant

    ) {

        Column() {if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onCloseRequest.
                    openDialog.value = false
                },
                title = {
                    Text(text = "Do you want to save this screenshot?", modifier = Modifier.padding(10.dp), fontSize = 20.sp,)

                },
                confirmButton = {
                    Button(

                        onClick = {
                            openDialog.value = false

                            text = saveImageToExternalStorage(bitmap = image).toString()
                            //https://developer.android.com/studio/debug/am-logcat
                            Log.e("save", text)

                        }) {Column{
                        image?.let { it1 -> Image(bitmap = it1.asImageBitmap(), contentDescription = "") }
                        Row(modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = "Save",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                            Spacer(Modifier.size(10.dp))
                        Text("Save Image", fontSize = 18.sp, modifier = Modifier.padding(10.dp))
                    }}}
                },
                dismissButton = {
                    Button(

                        onClick = {
                            openDialog.value = false
                        }) { Row(modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Save",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(10.dp))
                        Text("Don´t save this sreenshot")
                    }}
                }
            )
        }
           // View Model code from:  https://nphau.medium.com/android-how-to-embed-vlc-media-player-to-android-app-1828d34c54f
            // use AndroidView to use "view"-system based components
            AndroidView(
                // factory expects a function that gets an Android Context and will return
                // a class that extends an AndroidView
                // will only be executed at initial rendering and when parent composable
                // triggers a new execution
                factory = { context ->  //
                    VLCVideoLayout(context).apply {
                        libVLC = LibVLC(context)
                        mediaPlayer = MediaPlayer(libVLC)
                        mediaPlayer?.attachViews(this, null, false, false)

                        try {
                            Media(libVLC, Uri.parse(testurl)).apply {
                                setHWDecoderEnabled(true, false)
                                mediaPlayer?.media = this
                            }.release()

                            mediaPlayer?.play()
                        } catch (e: Exception) {
                            Log.e("StreamScreen", e.message!!)
                        }

                    }
                },
                // modifiers can be applied to the AndroidView too
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                  //  .onGloballyPositioned {
                    //    capturingViewBounds = it.boundsInRoot()
                    //)  }
                        ,
                // with update you can access the view itself (VLCVideoLayout in this case)
                // this function is used to handle all updates of the composition tree
                // handle state objects here
                update = { view ->
                    Log.i("update", view.toString())
                }
            )

        }}
    }


//https://www.android--code.com/2018/04/android-kotlin-save-image-to-external.html
// Method to save an image to external storage
fun saveImageToExternalStorage(bitmap:Bitmap?):Uri{

    val path = getExternalStoragePublicDirectory(DIRECTORY_PICTURES).absolutePath

    // Create a file to save the image
    val file = File(path, "${UUID.randomUUID()}.jpg")

        // Get the file output stream
    try { val stream: OutputStream = FileOutputStream(file)

        // Compress the bitmap
    if (bitmap != null) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    }
        // Flush the output stream
        stream.flush()

        // Close the output stream
        stream.close()
    } catch (e: IOException){ // Catch the exception
        e.printStackTrace() }

    // Return the saved image path to uri
    return Uri.parse(file.absolutePath)
}


