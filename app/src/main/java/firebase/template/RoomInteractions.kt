package firebase.template

import firebase.template.Database.NoteData
import firebase.template.Database.NotesDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class RoomInteractions(viewModel: ViewModel, notesDatabase: NotesDatabase.AppDatabase) {

    private val ioScope = CoroutineScope(Job() + Dispatchers.IO)
    val notesDao = notesDatabase.notesDao()

    suspend fun insertNoteIntoDatabase(noteData: NoteData) {
        withContext(Dispatchers.IO) {
            notesDao.insertNote(noteData)
        }
    }

    suspend fun getAllNotesFromDatabase() {
        withContext(Dispatchers.IO) {
            notesDao.getAllNotesData()
        }
    }
}