package zikrulla.production.quranapp.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class FileDownloader(private val context: Context) {

    fun downloadFileFromHiddenFolder() {
        val hiddenFolderPath = getHiddenFolderPath()
        val hiddenFile = File(hiddenFolderPath, "example.txt")

        // Check if the file exists before attempting to download
        if (hiddenFile.exists()) {
            // You can perform your download logic here
            // For example, copy the file to a destination folder
            val destinationFolder = getDestinationFolderPath()
            val destinationFile = File(destinationFolder, "example_copy.txt")

            copyFile(hiddenFile, destinationFile)
        } else {
            // Handle the case where the file doesn't exist
        }
    }

    private fun getHiddenFolderPath(): String {
        // Get the path to the external storage directory
        val externalStorageDir = context.getExternalFilesDir(null)

        // Define the name of the hidden folder
        val hiddenFolderName = ".hidden_folder"

        // Create the hidden folder if it doesn't exist
        val hiddenFolder = File(externalStorageDir, hiddenFolderName)
        if (!hiddenFolder.exists()) {
            hiddenFolder.mkdirs()
        }

        return hiddenFolder.absolutePath
    }

    private fun getDestinationFolderPath(): String {
        // Define the destination folder path
        val destinationFolderName = "destination_folder"

        // Create the destination folder if it doesn't exist
        val destinationFolder = File(context.filesDir, destinationFolderName)
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs()
        }

        return destinationFolder.absolutePath
    }

    private fun copyFile(sourceFile: File, destFile: File) {
        try {
            FileInputStream(sourceFile).use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
