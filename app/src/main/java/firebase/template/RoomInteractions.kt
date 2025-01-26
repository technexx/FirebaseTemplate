package firebase.template

import android.util.Log
import firebase.template.Database.NoteData
import firebase.template.Database.NotesDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomInteractions(viewModel: ViewModel, notesDatabase: NotesDatabase.AppDatabase) {
    val notesDao = notesDatabase.notesDao()

    suspend fun insertNoteIntoDatabase(noteData: NoteData) {
        withContext(Dispatchers.IO) {
            notesDao.insertNote(noteData)
            Log.i("noteList", "database is ${getAllNotesFromDatabase()}")

        }
    }

    suspend fun getAllNotesFromDatabase(): List<NoteData> {
        var noteList: List<NoteData>
        withContext(Dispatchers.IO) {
            notesDao.getAllNotesData()
            noteList = notesDao.getAllNotesData()
        }
        return noteList
    }

    suspend fun noteListFromDatabaseStorage() {
        val noteHolder = mutableListOf<Note>()

        val dbContents = getAllNotesFromDatabase()
        for (i in dbContents) {
            noteHolder.add(Note(i.title, i.body, i.lastEdited))
        }

        Log.i("noteList", "db is $noteHolder")
    }
}