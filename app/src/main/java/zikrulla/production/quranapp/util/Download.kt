package zikrulla.production.quranapp.util

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloaderImpl {
    private val REQUEST_STORAGE_PERMISSION = 1

    fun checkPermission(context: Context, activity: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        } else {
            // Download audio if permission granted
            return downloadAudio(context)
        }
        return false
    }

    private fun downloadAudio(context: Context): Boolean {
        val file = File(context.filesDir, "audio.mp3")
        val url = URL("https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/1.mp3")
        val inputStream = url.openStream()

        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var bytesRead = inputStream.read(buffer)

        while (bytesRead != -1) {
            outputStream.write(buffer, 0, bytesRead)
            bytesRead = inputStream.read(buffer)
        }

        outputStream.close()
        inputStream.close()

        return true
    }

}

