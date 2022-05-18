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
    private var mProjectionManager: MediaProjectionManager? = null
    private var mImageReader: ImageReader? = null
    private var mHandler: Handler? = null
    private var mDisplay: Display? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mDensity = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mRotation = 0
    private var mOrientationChangeCallback: OrientationChangeCallback? = null


    companion object {
        private val TAG = MainActivity::class.java.name
        private const val REQUEST_CODE = 100
        private var STORE_DIRECTORY: String? = null
        private var IMAGES_PRODUCED = 0
        private const val SCREENCAP_NAME = "Capture"
        private const val VIRTUAL_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
        private var sMediaProjection: MediaProjection? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

// start capture handling thread
        object : Thread() {
            override fun run() {
                Looper.prepare()
                mHandler = Handler()
                Looper.loop()
            }
        }.start()

        setContent {
            NestflixTheme {
                // A surface container using the 'background' color from the theme
                val mediaPlayerViewModel: MediaPlayerViewModel by viewModels()
                NestflixNavigation(mediaPlayerViewModel = mediaPlayerViewModel)

            }
        }
    }


    // parameter dazugeschummelt weril startActionForResult activity al parameter will, aber
    //      LocalContext.current as Activity man nur innerhalb einer Composable Funktion aufrufen kann
    //options null sind auch dazugeschrieben
    fun startProjection() {
        ActivityCompat.startActivityForResult(this, mProjectionManager!!.createScreenCaptureIntent(), REQUEST_CODE, null)
    }

    private fun stopProjection() {
        mHandler!!.post {
            if (sMediaProjection != null) {
                sMediaProjection!!.stop()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            sMediaProjection = data?.let { mProjectionManager!!.getMediaProjection(resultCode, it) }
            if (sMediaProjection != null) {

                // display metrics
                val metrics = resources.displayMetrics
                mDensity = metrics.densityDpi
                mDisplay = windowManager.defaultDisplay
                // create virtual display depending on device width / height
                createVirtualDisplay()
                // register orientation change callback
                mOrientationChangeCallback = OrientationChangeCallback(this)
                if (mOrientationChangeCallback!!.canDetectOrientation()) {
                    mOrientationChangeCallback!!.enable()

                }
                // register media projection stop callback
                sMediaProjection!!.registerCallback(MediaProjectionStopCallback(), mHandler)
            }
        }
    }
    @SuppressLint("WrongConstant")
    private fun createVirtualDisplay() { // get width and height
        val size = Point()
        mDisplay!!.getSize(size)
        mWidth = size.x
        mHeight = size.y
        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2)
        mVirtualDisplay = sMediaProjection!!.createVirtualDisplay(
            SCREENCAP_NAME,
            mWidth,
            mHeight,
            mDensity,
            VIRTUAL_DISPLAY_FLAGS,
            mImageReader!!.surface,
            null,
            mHandler
        )
        mImageReader!!.setOnImageAvailableListener(ImageAvailableListener(), mHandler)
    }
    private inner class ImageAvailableListener : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {
            var image: Image? = null
            var resizedBitmap: Bitmap? = null
            try {
                image = reader.acquireLatestImage()
                if (image != null) {
                    val planes = image.planes
                    val buffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * mWidth
                    // create bitmap
                    val bitmap = Bitmap.createBitmap(
                        mWidth + rowPadding / pixelStride,
                        mHeight,
                        Bitmap.Config.ARGB_8888
                    )

                    //fill from buffer
                    bitmap.copyPixelsFromBuffer(buffer)

                    //DO SOMETHING WITH CAPTURE BITMAP

                    bitmap.recycle()


                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                image?.close()
            }
        }
    }

    private inner class OrientationChangeCallback internal constructor(context: Context?) :
        OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            val rotation = mDisplay!!.rotation
            if (rotation != mRotation) {
                mRotation = rotation
                try { // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay!!.release()
                    if (mImageReader != null) mImageReader!!.setOnImageAvailableListener(null, null)
                    // re-create virtual display depending on device width / height
                    createVirtualDisplay()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private inner class MediaProjectionStopCallback : MediaProjection.Callback() {
        override fun onStop() {
            Log.e("ScreenCapture", "stopping projection.")
            mHandler!!.post {
                if (mVirtualDisplay != null) mVirtualDisplay!!.release()
                if (mImageReader != null) mImageReader!!.setOnImageAvailableListener(null, null)
                if (mOrientationChangeCallback != null) mOrientationChangeCallback!!.disable()
                sMediaProjection!!.unregisterCallback(this@MediaProjectionStopCallback)
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