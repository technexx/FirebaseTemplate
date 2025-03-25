package firebase.template

import android.util.Log
import android.util.Log.i
import firebase.template.Database.NoteData
import firebase.template.Database.NotesDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomInteractions(private val viewModel: ViewModel, notesDatabase: NotesDatabase.AppDatabase) {
    private val notesDao = notesDatabase.notesDao()

    suspend fun databaseNoteList(): List<NoteData> {
        var noteList: List<NoteData>
        withContext(Dispatchers.IO) {
            noteList = notesDao.getAllNotesData()
        }
        println("note list from db on firebase write is $noteList")
        return noteList
    }

    suspend fun localNoteListFromDatabaseStorage(): List<NoteContents> {
        val noteHolder = mutableListOf<NoteContents>()

        val dbContents = databaseNoteList()
        for (i in dbContents) {
            noteHolder.add(NoteContents(i.uid!!, i.title, i.body, i.lastEdited, false))
        }

        return noteHolder
    }

    suspend fun editNoteInDatabase(index: Int, title: String, body: String) {
        val noteList = viewModel.getNewCopyOfLocalNoteList()
        noteList[index].title = title
        noteList[index].body = body
        //Index of local list will not be the same for db list.
        val databaseNote = NoteData(null, noteList[index].id, noteList[index].title, noteList[index].body, noteList[index].lastEdited)

        updateNoteInDatabase(databaseNote)
        delay(2000)
    }

    suspend fun updateNoteInDatabase(note: NoteData) {
        withContext(Dispatchers.IO) {
            notesDao.updateNotes(note)
        }
    }

    //TODO: Not working.
    suspend fun deleteSelectedNotesFromDatabase() {
        withContext(Dispatchers.IO) {
            val databaseNoteList = databaseNoteList()
            val idList = listOfIdsOfSelectedNotes()

            println("id list is $idList")

            for (i in databaseNoteList) {
                println("db ids are ${i.id}")
                if (idList.contains(i.id)) {
                    notesDao.deleteNotes(i)
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
        for (i in localNoteListFromDatabaseStorage()) {
            localList.add(NoteContents(i.id, i.title, i.body, i.lastEdited, false))
        }
        viewModel.updateLocalNoteList(localList)
    }

    suspend fun saveAddedOrEditedNoteToLocalListAndDatabase() {
        val id = viewModel.getLocalNoteList.size
        var newLocalList = emptyList<NoteContents>().toMutableList()
        var titleTxtField = viewModel.getNoteTitleText
        if (titleTxtField.isEmpty()) titleTxtField = "Untitled"
        val bodyTxtField = viewModel.getNoteBodyText

        if (viewModel.NOTE_SCREEN_MODE == viewModel.ADDING_NOTE) {
            newLocalList = viewModel.getLocalNoteListWithNewNoteAdded(id, titleTxtField, bodyTxtField = bodyTxtField)
            addNoteToDatabase(id, titleTxtField, bodyTxtField = bodyTxtField)
        }
        if (viewModel.NOTE_SCREEN_MODE == viewModel.EDITING_SINGLE_NOTE) {
            newLocalList = viewModel.getLocalNoteListWithNoteEdited(viewModel.selectedNoteIndex, titleTxtField, bodyTxtField)
            editNoteInDatabase(viewModel.selectedNoteIndex, titleTxtField, bodyTxtField)
        }

        val sortedLocalList = viewModel.getLocalNoteListSortedByMostRecent(newLocalList)
        viewModel.updateLocalNoteList(sortedLocalList)
        viewModel.updateCurrentScreen(viewModel.NOTE_LIST_SCREEN)
    }

    suspend fun addNoteToDatabase(id: Int, titleTxtField: String = "Untitled", bodyTxtField: String) {
        val newNote = NoteContents(id, titleTxtField, bodyTxtField, formattedDateAndTime(), false)
        //Passing local NoteContents object inputs into database NoteData object.
        val databaseNote = NoteData(null, newNote.id, newNote.title, newNote.body, newNote.lastEdited)
        insertNoteIntoDatabase(databaseNote)
    }

    suspend fun insertNoteIntoDatabase(noteData: NoteData) {
        withContext(Dispatchers.IO) {
            notesDao.insertNote(noteData)
        }
    }

}