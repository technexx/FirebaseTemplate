package firebase.template

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import firebase.template.Database.NotesDatabase
import firebase.template.ui.theme.FirebaseTemplateTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("StaticFieldLeak")
private lateinit var activity: Activity
@SuppressLint("StaticFieldLeak")
private lateinit var appContext : Context
val ioScope = CoroutineScope(Job() + Dispatchers.IO)
val mainScope = CoroutineScope(Job() + Dispatchers.Main)
private lateinit var viewModel : ViewModel
private lateinit var noteDatabase: NotesDatabase.AppDatabase
private lateinit var roomInteractions: RoomInteractions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("test", "hi")

        activity = this@MainActivity
        appContext = applicationContext
        viewModel = ViewModel()

        noteDatabase = Room.databaseBuilder(
            appContext,
            NotesDatabase.AppDatabase::class.java,
            "notes-database"
        ).build()

        roomInteractions = RoomInteractions(viewModel, noteDatabase)
        val notePad = NotePad(viewModel, roomInteractions)

        ioScope.launch {
            roomInteractions.populateLocalNoteListFromDatabase()
            //Testing db upload
            uploadDatabase(appContext, "room_database")
        }

        setContent {
            FirebaseTemplateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainSurface {
                        notePad.MainBoard()
                    }
                }
            }
        }
    }
}

suspend fun uploadDatabase(context: Context, databaseName: String) {
    val storage = Firebase.storage

    // Create a reference to 'images/mountains.jpg'
    val storageRef = storage.reference.child("data/data/firebase.template/databases/$databaseName.db")
    val notesDatabaseShm = storage.reference.child("data/data/firebase.template/databases/notes-database-shm")
    val notesDatabaseWal = storage.reference.child("data/data/firebase.template/databases/notes-database-wal")

    val databaseFile = context.getDatabasePath("$databaseName.db")
    if (databaseFile == null || !databaseFile.exists()) {
        println("database file does not exist")
        return;
    }

    val fileUri = Uri.fromFile(databaseFile)

    try {
        storageRef.putFile(fileUri).await()
        println("Database uploaded successfully")
    } catch (e: Exception) {
        println("Database upload failed: ${e.message}")
        e.printStackTrace()
    }

}

@Composable
fun MainSurface(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = color
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}