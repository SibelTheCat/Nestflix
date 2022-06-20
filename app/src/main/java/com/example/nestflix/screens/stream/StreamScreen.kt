package com.example.nestflix.screens.stream


import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nestflix.R
import com.example.nestflix.SettingsViewModelFactory
import com.example.nestflix.manager.SettingsDataStore
import com.example.nestflix.model.BirdNotes
import com.example.nestflix.viewmodel.BirdNotesViewModel
import com.example.nestflix.viewmodel.MediaPlayerViewModel
import com.example.nestflix.viewmodel.SettingsViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


//to get the size of the videostream and to crop the screenshot accordingly
var myBirdVideo: View? = null


@Composable
fun StreamScreen(
    navController: NavController = rememberNavController(),
    birdNoteViewModel: BirdNotesViewModel = viewModel(),
    mediaPlayerViewModel: MediaPlayerViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(SettingsDataStore(LocalContext.current))),

    ) {
//https://developer.android.com/reference/kotlin/androidx/activity/compose/package-summary#backhandler
    //Back Button on the phone
    BackHandler {
        mediaPlayerViewModel.player?.stop();
        mediaPlayerViewModel.player?.release();
        //Damit beim erneuten öffnen des Stream Screens  der loading screen gezeigt wird
        mediaPlayerViewModel.open.value = true;
        navController.popBackStack() //go back to last screen
    }

    var image: Bitmap? = null
    var pathOfScreenshotToStorage = "test"


    val context = LocalContext.current as Activity

    // Für das Screenshot machen
    val coroutineScope = rememberCoroutineScope()

    //Für den Dialog der den Screenshot anzeigt
    val openDialog = remember { mutableStateOf(false) }

    //Damit der Screenshot Button nicht angezeigt wird, solange der Loading Screen gezeigt wird
    val openLoading by mediaPlayerViewModel.open.observeAsState(true)

    Scaffold(
        topBar = {

            TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant, elevation = 3.dp) {
                Row {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow back",
                        modifier = Modifier.clickable {
                            mediaPlayerViewModel.player?.stop();
                            mediaPlayerViewModel.player?.release();
                            //Damit beim erneuten öffnen des Stream Screens  der loading screen gezeigt wird
                            mediaPlayerViewModel.open.value = true;

                            //auf die RtspMediaSource könnte hier zugegriffen werden, um die
                            //RTSP Datenübertrageung beim schließen des Stream Screens zu beenden
                           // mediaPlayerViewModel.videoSource?.disable()
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

        //Button wird erst angezeigt wenn Buffer Screen nicht mehr angezeigt wird
        floatingActionButton = { if(!openLoading) {
            ExtendedFloatingActionButton({
                Text(text = "Capture Screenshot", color = Color.White)
            },
                onClick = {

                    //variante Leon -> ganzer Bildschirm
                    //https://stackoverflow.com/questions/63861095/jetpack-compose-take-screenshot-of-composable-function
                    coroutineScope.launch {

                        //Funktion returned ein bitmap von der größe des ganzen Bildschirmes
                        val bitmap = context.window.drawToBitmap()

                        //wenn ich den View übergebe -> wieder nur play button aber kein video stream content
                        // val bitmap = myBirdVideo?.drawToBitmap()

                        // bitmap wird auf die größe der View (myBirdVideo) zugeschnitten
                        var resizedBmp: Bitmap =
                            Bitmap.createBitmap(bitmap, 0, 320, bitmap.width, myBirdVideo!!.height);

                        image = resizedBmp

                        // in der Coroutine, damit Fenster erst aufgeht, wenn die bitmap fertig ist
                        openDialog.value = true

                    }

                },

                shape = RoundedCornerShape(18.dp),
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_camera_alt_24),
                        contentDescription = null
                    )
                }
            )
        } },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        backgroundColor = MaterialTheme.colors.primaryVariant

    ) {

        Column {
            if (openDialog.value) {
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
                    // es musste etwas getrickst werden -> Bild und Text zum Button wurden in den
                    //confirm Button gegeben
                    confirmButton = {
                        Button(

                            onClick = {
                                //Dialog wird gechlossen
                                openDialog.value = false

                                //Pfad zum Speicherort des bitmap wird von Funktion returned
                                pathOfScreenshotToStorage = saveImageToExternalStorage(bitmap = image).toString()
                                //https://developer.android.com/studio/debug/am-logcat
                                Log.e("save", pathOfScreenshotToStorage)

                                //es wird ein neuer Eintrag in der Datenbank erstellt mit dem Wert "Pfad" befüllt
                                savePictureAsNewBirdnote(path = pathOfScreenshotToStorage,
                                    birdNoteViewModel = birdNoteViewModel)

                            }) {

                            //Der soeben erstellte Screenshot (image)  wird im Dialog Fenster angezeigt.
                            Column {
                                //https://androidlearnersite.wordpress.com/2021/08/03/jetpack-compose-1-0-0-sample-codes/amp/
                                image?.let { it1 ->
                                    Image(bitmap = it1.asImageBitmap(),
                                        contentDescription = "screenshot")
                                }
                                Row(modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.ThumbUp,
                                        contentDescription = "Save",
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(Modifier.size(10.dp))
                                    Text("Save Image",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(10.dp))
                                }
                            }
                        }
                    },
                    dismissButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                            }) {
                            Row(modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Save",
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.size(10.dp))
                                Text("Don´t save this sreenshot")
                            }
                        }
                    }
                )
            }

            VideoPlayer(settingsViewModel, mediaPlayerViewModel)
        }
    }
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

    if (timeout)
        error("Failed waiting for PixelCopy")
    if (result != PixelCopy.SUCCESS)
        error("Non success result: $result")

    return bitmap
}

//https://www.android--code.com/2018/04/android-kotlin-save-image-to-external.html
// Method to save an image as jpeg to external storage
fun saveImageToExternalStorage(bitmap: Bitmap?): Uri {

    val path = getExternalStoragePublicDirectory(DIRECTORY_PICTURES).absolutePath

    // Create a file to save the image
    val file = File(path, "${UUID.randomUUID()}.jpg")

    // Get the file output stream
    try {
        val stream: OutputStream = FileOutputStream(file)

        // Compress the bitmap
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        // Flush the output stream
        stream.flush()

        // Close the output stream
        stream.close()
    } catch (e: IOException) { // Catch the exception
        e.printStackTrace()
    }

    // Return the saved image path to uri
    return Uri.parse(file.absolutePath)
}

fun savePictureAsNewBirdnote(path: String, birdNoteViewModel: BirdNotesViewModel) {
    birdNoteViewModel.addBirdNote(BirdNotes(pathToPicture = path, title = "", description = ""))
}



@Composable
fun VideoPlayer(
    settingsViewModel: SettingsViewModel,
    mediaPlayerViewModel: MediaPlayerViewModel = viewModel(),) {

    //Für den Loading Screnn-> soll nur solange angezeigt werden solange
    // mediaPlayerViewModel.open true ist, genauso lange soll auch der Screenshot Button
    //nicht zu sehen sein
    val openLoading by mediaPlayerViewModel.open.observeAsState(true)

    //ip Adresse des Streams wird in Data Storage gespeichert. Pber
    //settingsViewModel ist ein Zugriff möglich
    val testvid2 = settingsViewModel.ipAddress.observeAsState().value
    //"rtsp://rtsp.stream/pattern"

    LoadingScreen(isLoading = openLoading) {}

    val context = LocalContext.current

    mediaPlayerViewModel.player = ExoPlayer.Builder(context).build()

    // Damit der Loading screen angezeigt wird, solange das video zu Beginn läd
    mediaPlayerViewModel.player?.addListener(object : Player.Listener{
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if(playbackState == Player.STATE_BUFFERING){
              //  Toast.makeText(context, "loading", Toast.LENGTH_SHORT).show()

            }
            else if (playbackState == Player.STATE_READY){
              //  Toast.makeText(context, "loaded", Toast.LENGTH_SHORT).show()
                mediaPlayerViewModel.open.value = false
            }
        }
    })

    //Video wird in einer Texture View angezeigt, damit Screenshot möglich ist
    //val playerView = createPlayerView(context)  -> old version with surface view as default
    val playerLayout = LayoutInflater.from(context).inflate(R.layout.video_player, null)
    var playerView = playerLayout.findViewById<StyledPlayerView>(R.id.player)

    //  https://exoplayer.dev/doc/reference/com/google/android/exoplayer2/ui/StyledPlayerView.html
    playerView!!.setShowFastForwardButton(false)
    playerView!!.setShowNextButton(false)
    playerView!!.setShowPreviousButton(false)
    playerView!!.setShowRewindButton(false)
    playerView!!.hideController()


    //video Source ist mein media Itm (I guess)
    // mediaPlayerViewModel.videoSource = instance of RtspMediaSource?
    mediaPlayerViewModel.videoSource = RtspMediaSource.Factory().setDebugLoggingEnabled(true)
        .createMediaSource(MediaItem.fromUri(testvid2.toString()))

    val playWhenReady by rememberSaveable {
        mutableStateOf(true)
    }

    mediaPlayerViewModel.player?.setMediaSource(mediaPlayerViewModel.videoSource!!)
    playerView!!.setPlayer(mediaPlayerViewModel.player)

    LaunchedEffect(mediaPlayerViewModel.player) {
        mediaPlayerViewModel.player?.prepare()
        mediaPlayerViewModel.player?.playWhenReady= playWhenReady
    }

    //hier wird das Video sichtbar
    AndroidView(factory = {
        playerView!!
    },
        update = { view ->
            //Varialbe brauche ich um den Screenshot auf die richtige Größe zuzuschneiden
            myBirdVideo = view
        }
    )
}

//https://www.bakulabs.me/posts/loading-view-jetpack-compose/
//https://semicolonspace.com/jetpack-compose-dialog-loading/?fbclid=IwAR3c7a3lX1sXVjaIm3zp-GIp11cCZisY2R-6I8Bv2MuNgaG7s4GhHtFjDe8
@Composable
fun LoadingScreen(
    isLoading: Boolean,
    content: @Composable () -> Unit
) = if (isLoading
) {
    Box(modifier = Modifier.fillMaxSize().
    background(color = MaterialTheme.colors.secondaryVariant)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Loading", fontSize = 25.sp)
            CircularProgressIndicator()
        }
    }
} else {
    content()
}


