package com.example.nestflix.screens.stream


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment.*
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.view.*
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.core.graphics.set
import androidx.core.view.drawToBitmap
import androidx.core.view.get
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nestflix.MainActivity
import com.example.nestflix.R
import com.example.nestflix.SettingsViewModelFactory
import com.example.nestflix.manager.SettingsDataStore
import com.example.nestflix.model.BirdNotes
import com.example.nestflix.viewmodel.BirdNotesViewModel
import com.example.nestflix.viewmodel.MediaPlayerViewModel
import com.example.nestflix.viewmodel.SettingsViewModel
import com.google.android.exoplayer2.BasePlayer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.jraska.falcon.Falcon
import com.kpstv.compose.kapture.attachController
import com.kpstv.compose.kapture.rememberScreenshotController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


//var mediaPlayer: MediaPlayer? = null

var playerView: StyledPlayerView? = null
var myBirdVideo : View? = null  //für variante 3


@Composable
fun StreamScreen(
    navController: NavController = rememberNavController(),
    mediaPlayer: MediaPlayer? = null,
    birdNoteViewModel: BirdNotesViewModel = viewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(SettingsDataStore(LocalContext.current))),

    ) {
//https://developer.android.com/reference/kotlin/androidx/activity/compose/package-summary#backhandler
    //Back Button on the phone
    BackHandler{
        mediaPlayerViewModel.mediaPlayer?.stop()
        mediaPlayerViewModel.mediaPlayer?.detachViews()
        navController.popBackStack() //go back to last screen
    }

    var mediaPlayer: MediaPlayer? = null




    var image : Bitmap? = null
    var text : String = "test"

    val screenshotController = rememberScreenshotController()  //für variante 2

    val scope = rememberCoroutineScope()

    val view = LocalView.current
    val context = LocalContext.current as Activity

    var viewtest : SurfaceView? = null

    val coroutineScope = rememberCoroutineScope()


    //var jetCaptureView: MutableState<View>? = null
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
    val openDialog = remember { mutableStateOf(false)  }

    //variante 7
    val captureController = rememberCaptureController()



    var libVLC: LibVLC?



    // var mediaPlayer: MediaPlayer?
    val testurl = settingsViewModel.ipAddress.observeAsState().value
    //"rtsp://rtsp.stream/pattern"
    //"tcp/h264://10.0.0.134:55555"
    val raspberry = "rtsp://10.0.0.134:3366/stream1"

    //Variante 7 https://github.com/PatilShreyas/Capturable
    // image wird hier automatisch gespeichert weil zugriff auf bitmap nicht implementiert wurde
    //aber es kommt sowieso wieder nur die schwarze box
   /* Capturable(
        controller = captureController,
        onCaptured = { bitmap, error ->
            // This is captured bitmap of a content inside Capturable Composable.
            if (bitmap != null) {
                // Bitmap is captured successfully. Do something with it!
                val image2 = bitmap.asAndroidBitmap()
                text = saveImageToExternalStorage(bitmap = image2).toString()
                //https://developer.android.com/studio/debug/am-logcat
                Log.e("save", text)
                savePictureAsNewBirdnote(path = text, birdNoteViewModel = birdNoteViewModel)
            }

            if (error != null) {
                // Error occurred. Handle it!
            }
        }
    ){*/
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



                    //Variante 7 -> mittels Capturable API
                   // captureController.capture()

                    // Version 6
                  // (context as MainActivity).startProjection()

                    //version 5 https://github.com/jraska/Falcon
                   // val bitmap = Falcon.takeScreenshotBitmap(context as MainActivity)
                 //    image = bitmap


                    //variante Leon -> ganzer Bildschirm no video :( -> man muss meist 2Mal drücken
                    //https://stackoverflow.com/questions/63861095/jetpack-compose-take-screenshot-of-composable-function
                  coroutineScope.launch {
                       val bitmap = context.window.drawToBitmap()

                     // val bitmap = myBirdVideo?.drawToBitmap() -> wieder nur play button aber kein video screen content
                       // bitmap.reconfigure(400, bitmap.width,null)

                    //  val bitmap = myBirdVideo?.drawToBitmap()
                       // image = bitmap

                       var resizedBmp: Bitmap  = Bitmap.createBitmap(bitmap, 0,320, bitmap.width, myBirdVideo!!.height);

                      Log.i("bitmap",  resizedBmp.height.toString() )
                      image = resizedBmp

                    }

                  //  Log.i("playerVies",  playerView?.height.toString() )
                    Log.i("myBird",  myBirdVideo?.height.toString()+myBirdVideo?.x.toString() )

                    Log.i("bitmap",  image?.height.toString() )




                    // variante 1 gibt den ganzen Bildschirm aus, da capturingViewBounds = it.boundsInRoot() am Scaffold angehängt wurde
                    /*  val bounds = capturingViewBounds ?: return@ExtendedFloatingActionButton
                           image = Bitmap.createBitmap(
                           bounds.width.roundToInt(), bounds.height.roundToInt(),
                           Bitmap.Config.ARGB_8888
                       ).applyCanvas {
                           this.translate((-bounds.left), (-bounds.top))
                           view.draw(this)
                       }*/

                    // variante 2 -> sollte eigentlich nur Android View ausgeben, gibt aber auch bottom bar aus
                    // //https://github.com/KaustubhPatange/kapture
                    // corotines die man hier braucht -> https://developer.android.com/jetpack/compose/side-effects

                    /*scope.launch {
                       val bitmap : Result<Bitmap> = screenshotController.captureToBitmap(
                        config = Bitmap.Config.ARGB_8888)
                        image = bitmap.getOrNull()
                    }*/




                    // variante 3 -> sollte nur den Teil des Videos ausgeben
                    //https://hiteshkrsahu.medium.com/a-complete-guide-for-taking-screenshot-in-android-28-bcb9a19a2b6e
                    //  image = getBitmapFromView(myBirdVideo!!)


                    // val screenshot = Screenshot.capture()
                    //val bitmap =  screenshot.bitmap
                    //image = bitmap
                   // startProjection()
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

        Column {if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onCloseRequest.
                    openDialog.value = false
                },
                title = {
                    Text(text = "Do you want to save this screenshot?",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 20.sp)

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
         /*   AndroidView(
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
            )*/
            VideoPlayer(settingsViewModel )
        }}
}
//}

suspend fun Window.drawToBitmap(
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    timeoutInMs: Long = 10000,
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



@Composable
fun VideoPlayer(settingsViewModel: SettingsViewModel ){

   // val testurl = settingsViewModel.ipAddress.observeAsState().value
    val testvid2 = settingsViewModel.ipAddress.observeAsState().value
    //"rtsp://rtsp.stream/pattern"



   

    val streamVideo = "rtsp://10.0.0.134:8554/stream1"
    val testVideo = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"
    val context = LocalContext.current
    val player = ExoPlayer.Builder(context).build()


    //val playerView = createPlayerView(context)  -> old version with surface view as default
    val playerLayout = LayoutInflater.from(context).inflate(R.layout.video_player, null)
    playerView = playerLayout.findViewById<StyledPlayerView>(R.id.player)





    //  https://exoplayer.dev/doc/reference/com/google/android/exoplayer2/ui/StyledPlayerView.html
    playerView!!.setShowFastForwardButton(false)
    playerView!!.setShowNextButton(false)
    playerView!!.setShowPreviousButton(false)
    playerView!!.setShowRewindButton(false)
    playerView!!.hideController()

    //video Source ist mein media Itm (I guess)
    val videoSource = RtspMediaSource.Factory().setDebugLoggingEnabled(true)
        .createMediaSource(MediaItem.fromUri(testvid2.toString()))

    val playWhenReady by rememberSaveable {
        mutableStateOf(true)
    }

    player.setMediaSource(videoSource)

   // myBirdVideo = playerView!!.getVideoSurfaceView()
    //// Bind the player to the view.
    // other option -> playerView.setPlayer(player);

                playerView!!.setPlayer(player)
               // Log.i("update",  playerView!!.height.toString() )


    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady

    }
    AndroidView(factory = {

        playerView!!

    }
        ,
        update = { view ->
           // Log.i("update",  playerView!!.height.toString() )
            myBirdVideo = view
        }
    )
}


// https://stackoverflow.com/questions/31967259/how-to-dynamically-add-an-attributeset-to-a-textview
//geht nicht mehr
fun createPlayerView(context: Context): StyledPlayerView? {
    val attributes = "<attribute xmlns:android=\"http://schemas.android.com/apk/res/android\"" +
            " xmlns:app=\"http://schemas.android.com/apk/res-auto\"" +
           // " app:surface_type=\"surface_view\"" +
            "/>"
    var factory: XmlPullParserFactory? = null
    try {
        factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(StringReader(attributes))
        parser.next()
        val attrs = Xml.asAttributeSet(parser)
        return StyledPlayerView(context, attrs)
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}



