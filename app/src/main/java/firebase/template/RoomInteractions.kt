package firebase.template

import android.util.Log
import firebase.template.Database.NoteData
import firebase.template.Database.NotesDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomInteractions(private val viewModel: ViewModel, notesDatabase: NotesDatabase.AppDatabase) {
    private val notesDao = notesDatabase.notesDao()

    suspend fun insertNoteIntoDatabase(noteData: NoteData) {
        withContext(Dispatchers.IO) {
            notesDao.insertNote(noteData)
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

    suspend fun noteListFromDatabaseStorage(): List<Note> {
        val noteHolder = mutableListOf<Note>()

        val dbContents = getAllNotesFromDatabase()
        for (i in dbContents) {
            noteHolder.add(Note(i.title, i.body, i.lastEdited))
        }

        return noteHolder
    }

    suspend fun populateLocalNoteListFromDatabase() {
        val localList = mutableListOf<Note>()
        for (i in noteListFromDatabaseStorage()) {
            localList.add(Note(i.title, i.body, i.lastEdited))
        }
        viewModel.updateLocalNoteList(localList)
    }
}