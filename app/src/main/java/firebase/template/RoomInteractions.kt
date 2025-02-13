package firebase.template

import android.util.Log
import android.util.Log.i
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

    private suspend fun databaseNoteList(): List<NoteData> {
        var noteList: List<NoteData>
        withContext(Dispatchers.IO) {
            notesDao.getAllNotesData()
            noteList = notesDao.getAllNotesData()
        }
        return noteList
    }

    private suspend fun localNoteListFromDatabaseStorage(): List<NoteContents> {
        val noteHolder = mutableListOf<NoteContents>()

        val dbContents = databaseNoteList()
        for (i in dbContents) {
            noteHolder.add(NoteContents(i.id, i.title, i.body, i.lastEdited, false))
        }

        return noteHolder
    }

    suspend fun deleteSelectedNotesFromDatabase() {
        withContext(Dispatchers.IO) {
//            val databaseNoteList = getDatabaseNoteListFromLocalNoteList()
            val databaseNoteList = databaseNoteList()
            val idList = listOfIdsOfSelectedNotes()

            for (i in databaseNoteList) {
//                notesDao.deleteNotes(i)
                if (idList.contains(i.id)) {
                    notesDao.deleteNotes(i)
                    Log.i("test", "deleting note $i with id of ${i.id}")
                }
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

    fun listOfIdsOfSelectedNotes(): List<Int> {
        val localNoteList = viewModel.getLocalNoteList
        val listOfSelectedIds = mutableListOf<Int>()
        for (i in localNoteList) {
            if (i.isSelected) listOfSelectedIds.add(i.id)
        }
        return listOfSelectedIds
    }

    suspend fun populateLocalNoteListFromDatabase() {
        val localList = mutableListOf<NoteContents>()
        for (i in localNoteListFromDatabaseStorage()) {
            localList.add(NoteContents(viewModel.getLocalNoteList.size -1, i.title, i.body, i.lastEdited, false))
        }
        viewModel.updateLocalNoteList(localList)
    }
}