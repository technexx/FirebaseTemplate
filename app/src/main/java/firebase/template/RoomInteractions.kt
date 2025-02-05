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

    suspend fun noteListFromDatabaseStorage(): List<NoteContents> {
        val noteHolder = mutableListOf<NoteContents>()

        val dbContents = getAllNotesFromDatabase()
        for (i in dbContents) {
            noteHolder.add(NoteContents(viewModel.getLocalNoteList.size, i.title, i.body, i.lastEdited, false))
        }

        return noteHolder
    }

    suspend fun deleteSelectedNotesFromDatabase() {
        val databaseNoteList = getDatabaseNoteListFromLocalNoteList()
        val idList = listOfIdsOfSelectedNotes()
        val listOfDatabaseNotesToDelete = mutableListOf<NoteData>()

        for (i in databaseNoteList) {
            if (idList.contains(i.id)) {
                listOfDatabaseNotesToDelete.add(i)
            }
        }
    }

    private fun getDatabaseNoteListFromLocalNoteList(): List<NoteData>{
        val localNoteList = viewModel.getLocalNoteList
        val databaseNoteList = mutableListOf<NoteData>()
        for (i in localNoteList) {
            databaseNoteList.add(NoteData(null, i.id, i.title, i.body, i.lastEdited))
        }
        return databaseNoteList
    }

    private fun listOfIdsOfSelectedNotes(): List<Int> {
        val localNoteList = viewModel.getLocalNoteList
        val listOfSelectedIds = mutableListOf<Int>()
        for (i in localNoteList) {
            if (i.isSelected) listOfSelectedIds.add(i.id)
        }
        return listOfSelectedIds
    }

    suspend fun populateLocalNoteListFromDatabase() {
        val localList = mutableListOf<NoteContents>()
        for (i in noteListFromDatabaseStorage()) {
            localList.add(NoteContents(viewModel.getLocalNoteList.size -1, i.title, i.body, i.lastEdited, false))
        }
        viewModel.updateLocalNoteList(localList)
    }
}