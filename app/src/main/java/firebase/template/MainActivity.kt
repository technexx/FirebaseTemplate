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
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import firebase.template.Database.NotesDatabase
import firebase.template.ui.theme.FirebaseTemplateTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

@SuppressLint("StaticFieldLeak")
private lateinit var activity: Activity
@SuppressLint("StaticFieldLeak")
private lateinit var appContext : Context
val ioScope = CoroutineScope(Job() + Dispatchers.IO)
val mainScope = CoroutineScope(Job() + Dispatchers.Main)
private lateinit var viewModel : ViewModel
private lateinit var noteDatabase: NotesDatabase.AppDatabase
private lateinit var roomInteractions: RoomInteractions
private lateinit var firebaseQueries: FirebaseQueries

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this@MainActivity
        appContext = applicationContext
        viewModel = ViewModel()

        noteDatabase = Room.databaseBuilder(
            appContext,
            NotesDatabase.AppDatabase::class.java,
            "notes-database"
        ).build()

        roomInteractions = RoomInteractions(viewModel, noteDatabase)
        firebaseQueries = FirebaseQueries(roomInteractions)
        val notePad = NotePad(viewModel, roomInteractions)

        ioScope.launch {
            roomInteractions.populateLocalNoteListFromDatabase()
            viewModel.setGloballyAccessedTextAndUneditedTextFields()
            //Testing db uploadAppDatabase
            delay(2000)
            firebaseQueries.writeToFirebaseDatabase()
            firebaseQueries.readFromFirebaseDatabase()
//            firebaseQueries.uploadDatabase(appContext, "notes-database")
        }

        setContent {
            FirebaseTemplateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainSurface {
                        notePad.HomeBoard()
                    }
                }
            }
        }
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