package firebase.template

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

class FirebaseQueries {
    suspend fun uploadDatabase(context: Context, databaseName: String) {
        val storage = Firebase.storage

        val databaseFile = context.getDatabasePath(databaseName)

        //This is the directory in cloud storage where we will store database.
        val storageRef = storage.reference.child("databases/our_notes_database/$databaseName")

        if (databaseFile == null || !databaseFile.exists()) {
            println("database file does not exist")
            return;
        }

//    val fileUri = Uri.fromFile(databaseFile)
        val fileUri = uriFile(context, databaseFile)

        // Create a temporary file in the cache directory


        println("our database file is $databaseFile")
        println("storage reference is $storageRef")
        println("uri from file is $fileUri")

        try {
            storageRef.putFile(fileUri).await()
            println("Database uploaded successfully")
        } catch (e: Exception) {
            println("Database upload failed: ${e.message}")
            e.printStackTrace()
        }
    }

    fun uriFile(context: Context, databaseFile: File): Uri {
        val tempFile = File.createTempFile("temp_db", ".db", context.cacheDir)

        try {
            // Copy the database file to the temporary file
            databaseFile.inputStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e:Exception) {
            println("Copying to public access location failed ${e.message}")
        }

        // Create a ContentUri using FileProvider
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Replace with your FileProvider authority
            tempFile
        )
        return tempFile.toUri()
    }

}