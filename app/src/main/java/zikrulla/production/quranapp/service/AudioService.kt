package zikrulla.production.quranapp.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import zikrulla.production.quranapp.util.Constants.DATA_URI
import zikrulla.production.quranapp.util.Constants.DATA_URL
import zikrulla.production.quranapp.util.Constants.TAG
import java.util.concurrent.TimeUnit

class AudioService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var url: String? = null
    private var uri: Uri? = null
    private val binder: IBinder = LocalBinder()
    private var duration: String? = null
    private var durationLength = 0

    inner class LocalBinder : Binder() {
        fun getService(): AudioService {
            return this@AudioService
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        url = intent?.getStringExtra(DATA_URL)

        if (url != null)
            createMediaPlayer(url = url)
        else{
            val uriString = intent?.getStringExtra(DATA_URI)
            uri = Uri.parse(uriString)
            uri?.let {
                createMediaPlayer(uri = uri)
            }
        }
        return START_STICKY
    }

    fun createMediaPlayer(url: String? = null, uri: Uri? = null) {
        mediaPlayer = MediaPlayer()
        if (url != null) {
            Log.d(TAG, "createMediaPlayer: $url")
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
        } else if (uri != null) {
            Log.d(TAG, "createMediaPlayer: $uri")
            mediaPlayer?.setDataSource(this, uri)
            mediaPlayer?.prepare()
        }
        mediaPlayer?.setOnPreparedListener {
            it.start()
            Log.d(TAG, "createMediaPlayer: start")
            try {
                durationLength = mediaPlayer?.duration ?: 0
                val totalSec =
                    TimeUnit.SECONDS.convert(durationLength.toLong(), TimeUnit.MILLISECONDS)
                val min = TimeUnit.MINUTES.convert(durationLength.toLong(), TimeUnit.SECONDS)
                val sec = totalSec - 60 * min
                duration = "$min:$sec"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaPlayer?.setOnCompletionListener {
            Log.d(TAG, "createMediaPlayer: stop")
            releaseMediaPlayer()
            stopSelf()
        }
    }

    fun releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun onStop() {
        releaseMediaPlayer()
        stopSelf()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: ")
        return binder
    }

    fun currentPosition(): Int? {
        return mediaPlayer?.currentPosition
    }

    fun isPlaying(): Boolean? {
        return mediaPlayer?.isPlaying
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true)
            mediaPlayer?.pause()
    }

    fun getDuration() = durationLength

    fun seekTo(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }
}