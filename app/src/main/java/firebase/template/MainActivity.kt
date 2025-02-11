package firebase.template

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import firebase.template.Database.NotesDatabase
import firebase.template.ui.theme.FirebaseTemplateTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
private lateinit var activity: Activity
@SuppressLint("StaticFieldLeak")
private lateinit var appContext : Context
val ioScope = CoroutineScope(Job() + Dispatchers.IO)
private lateinit var viewModel : ViewModel
private lateinit var noteDatabase: NotesDatabase.AppDatabase
private lateinit var roomInteractions: RoomInteractions

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
        val notePad = NotePad(viewModel, roomInteractions)

        ioScope.launch {
            roomInteractions.populateLocalNoteListFromDatabase()
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