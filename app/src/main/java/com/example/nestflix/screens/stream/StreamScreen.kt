package com.example.nestflix.screens.stream


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Environment.*
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.test.runner.screenshot.Screenshot
import com.example.nestflix.MainActivity
import com.example.nestflix.R
import com.example.nestflix.model.BirdNotes
import com.example.nestflix.viewmodel.BirdNotesViewModel
import com.example.nestflix.viewmodel.MediaPlayerViewModel
import com.kpstv.compose.kapture.ScreenshotController
import com.kpstv.compose.kapture.attachController
import com.kpstv.compose.kapture.rememberScreenshotController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import kotlin.math.roundToInt
import androidx.compose.runtime.LaunchedEffect as LaunchedEffect


//var mediaPlayer: MediaPlayer? = null

@Composable
fun StreamScreen(navController: NavController = rememberNavController(),
                 mediaPlayer: MediaPlayer? = null,
                 birdNoteViewModel: BirdNotesViewModel = viewModel(),
                 mediaPlayerViewModel : MediaPlayerViewModel = viewModel()
) {
    var mediaPlayer: MediaPlayer? = null
    var myBirdVideo : View? = null  //für variante 3

    var image : Bitmap? = null
    var text : String = "test"

    val screenshotController = rememberScreenshotController()  //für variante 2

    val scope = rememberCoroutineScope()

    val view = LocalView.current
    val context = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()


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
                            mediaPlayerViewModel.mediaPlayer?.stop();
                            mediaPlayerViewModel.mediaPlayer?.detachViews();
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

                    coroutineScope.launch {
                        val bitmap = context.window.drawToBitmap()
                        
                        val x = 0
                    }


                    // variante 1 gibt den ganzen Bildschirm aus, da capturingViewBounds = it.boundsInRoot() am Scaffold angehängt wurde
                 /*   val bounds = capturingViewBounds ?: return@ExtendedFloatingActionButton
                        image = Bitmap.createBitmap(
                        bounds.width.roundToInt(), bounds.height.roundToInt(),
                        Bitmap.Config.ARGB_8888
                    ).applyCanvas {
                        this.translate((-bounds.left), (-bounds.top))
                        view.draw(this)
                    }*/

                    // variante 2 -> sollte eigentlich nur Android View ausgeben, gibt aber auch bootom bar aus
                    // //https://github.com/KaustubhPatange/kapture
                    // corotines die man hier braucht -> https://developer.android.com/jetpack/compose/side-effects
/*
                    scope.launch {
                       val bitmap : Result<Bitmap> = screenshotController.captureToBitmap(
                        config = Bitmap.Config.ARGB_8888)
                        image = bitmap.getOrNull()
                    }
*/

                  // variante 3 -> sollte nur den Teil des Videos ausgeben
                    //https://hiteshkrsahu.medium.com/a-complete-guide-for-taking-screenshot-in-android-28-bcb9a19a2b6e
                  //  image = getBitmapFromView(myBirdVideo as VLCVideoLayout)


                   // val screenshot = Screenshot.capture()
                    //val bitmap =  screenshot.bitmap
                    //image = bitmap
                    //startProjection()
                    //MainActivity
                    Log.e("bildtest", image.toString())



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
                            savePictureAsNewBirdnote(path = text, birdNoteViewModel = birdNoteViewModel)

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
           // View Model code from:
            // and https://lindevs.com/display-progress-dialog-when-rtsp-stream-is-buffering-in-vlc-player-on-android/
            // use AndroidView to use "view"-system based components
            AndroidView(
                // factory expects a function that gets an Android Context and will return
                // a class that extends an AndroidView
                // will only be executed at initial rendering and when parent composable
                // triggers a new execution
                factory = { context ->  //
                    VLCVideoLayout(context).apply{
                        libVLC = LibVLC(context)
                        mediaPlayerViewModel.mediaPlayer = MediaPlayer(libVLC)
                        //mediaPlayer = MediaPlayer(libVLC)
                       // mediaPlayer?.attachViews(this, null, false, false)
                        mediaPlayerViewModel.mediaPlayer?.attachViews(this, null, false, false)


                        try {
                            Media(libVLC, Uri.parse(testurl)).apply {
                                setHWDecoderEnabled(true, false)
                               // mediaPlayer?.media = this
                                mediaPlayerViewModel.mediaPlayer?.media = this
                            }.release()

                           // mediaPlayer?.play()
                            mediaPlayerViewModel.mediaPlayer?.play()
                        } catch (e: Exception) {
                            Log.e("StreamScreen", e.message!!)
                        }

                    }
                },
                // modifiers can be applied to the AndroidView too
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .border(BorderStroke(width = 15.dp,color = MaterialTheme.colors.secondary))
                    .attachController(screenshotController)
                  //  .onGloballyPositioned {
                    //    capturingViewBounds = it.boundsInRoot()
                    //)  }
                        ,
                // with update you can access the view itself (VLCVideoLayout in this case)
                // this function is used to handle all updates of the composition tree
                // handle state objects here
                update = { view ->
                    Log.i("update", view.height.toString())
                    myBirdVideo = view


                }


            )

        }}
    }

suspend fun Window.drawToBitmap(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    timeoutInMs: Long = 10000
): Bitmap {
    var result = PixelCopy.ERROR_UNKNOWN
    val latch = CountDownLatch(1)

    val bitmap = Bitmap.createBitmap(decorView.width, decorView.height, config)
    PixelCopy.request(this, bitmap, { copyResult ->
        result = copyResult
        latch.countDown()
    }, Handler(Looper.getMainLooper()))

    var timeout = false
    withContext(Dispatchers.IO) {
        runCatching {
            timeout = !latch.await(timeoutInMs, TimeUnit.MILLISECONDS)
        }
    }

    if (timeout) error("Failed waiting for PixelCopy")
    if (result != PixelCopy.SUCCESS) error("Non success result: $result")

    return bitmap
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

fun savePictureAsNewBirdnote(path : String, birdNoteViewModel: BirdNotesViewModel){
    birdNoteViewModel.addBirdNote(BirdNotes(pathToPicture = path, title = "", description = ""))}

// für Variante 3
    fun getBitmapFromView(view: View): Bitmap? {
        var bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
       // canvas.drawColor(defaultColor)
        view.draw(canvas)
        return bitmap
    }

//noch nicht verwendet-> da ich nicht weiss wie
// https://hiteshkrsahu.medium.com/a-complete-guide-for-taking-screenshot-in-android-28-bcb9a19a2b6e
fun getBitMapFromSurfaceView(videoView: SurfaceView, callback: (Bitmap?) -> Unit) {
    val bitmap: Bitmap = Bitmap.createBitmap(
        videoView.width,
        videoView.height,
        Bitmap.Config.ARGB_8888
    );
    try {
        // Create a handler thread to offload the processing of the image.
        val handlerThread = HandlerThread("PixelCopier");
        handlerThread.start();
        PixelCopy.request(
            videoView,
            bitmap,
            PixelCopy.OnPixelCopyFinishedListener { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    callback(bitmap)
                }
                handlerThread.quitSafely();
            },
            Handler(handlerThread.looper)
        )
    } catch (e: IllegalArgumentException) {
        callback(null)
        // PixelCopy may throw IllegalArgumentException, make sure to handle it
        e.printStackTrace()
    }
}


