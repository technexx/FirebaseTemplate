package firebase.template

import android.os.Build
import android.util.Log
import android.util.Log.i
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import firebase.template.Database.NoteData
import firebase.template.NoteContents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ViewModel : ViewModel() {
    val NOTE_LIST_SCREEN = 0
    val NOTE_SCREEN = 1

    var NOTE_SCREEN_MODE = 0
    val ADDING_NOTE = 0
    val EDITING_NOTE = 1

    var selectedNoteIndex: Int = 0

    private val _colorTheme = MutableStateFlow(0)
    val colorTheme: StateFlow<Int> = _colorTheme.asStateFlow()

    private val _currentScreen = MutableStateFlow(NOTE_LIST_SCREEN)
    val currentScreen: StateFlow<Int> = _currentScreen.asStateFlow()

    private val _localNoteList = MutableStateFlow(emptyList<NoteContents>())
    val localNoteList: StateFlow<List<NoteContents>> = _localNoteList.asStateFlow()

    private val _noteEditMode = MutableStateFlow(false)
    val noteEditMode: StateFlow<Boolean> = _noteEditMode.asStateFlow()

    fun updateCurrentScreen(value: Int) {
        _currentScreen.value = value
    }

    fun updateColorTheme(theme: Int) {
        _colorTheme.value = theme
    }

    fun updateLocalNoteList(note: List<NoteContents>) {
        _localNoteList.value = note
    }

    fun updateNoteEditMode(isActive: Boolean) {
        _noteEditMode.value = isActive
    }

    fun savedNoteTitle(noteIndex: Int): String {
        return getNewCopyOfLocalNoteList()[noteIndex].title
    }

    fun savedNoteBody(noteIndex: Int): String {
        return getNewCopyOfLocalNoteList()[noteIndex].body
    }

    fun removeFromLocalNotesList() {
        val newList = SnapshotStateList<NoteContents>()

        for (i in getLocalNoteList) {
            newList.add(i)
            if (i.isSelected) newList.remove(i)
        }
        updateLocalNoteList(newList)
    }

    //Using new list with new copies of each NoteContent item to trigger recomposition.
    fun markNoteAsSelectedOrUnselected(selected: Boolean, index: Int) {
        val newList = getNewCopyOfLocalNoteList()
        newList[index].isSelected = selected

        updateLocalNoteList(newList)
    }

    fun markAllNotesAsUnselected() {
        val localNoteList = getNewCopyOfLocalNoteList()
        for (i in localNoteList) {
            i.isSelected = false
        }
        updateLocalNoteList(localNoteList)
    }

    fun getNewCopyOfLocalNoteList(): SnapshotStateList<NoteContents> {
        val newList = SnapshotStateList<NoteContents>()
        for (i in getLocalNoteList.indices) {
            newList.add(NoteContents(getLocalNoteList[i].id, getLocalNoteList[i].title, getLocalNoteList[i].body, getLocalNoteList[i].lastEdited, getLocalNoteList[i].isSelected))
        }
        return newList
    }

    fun areAnyNotesSelected(): Boolean {
        val localNoteList = getLocalNoteList
        for (i in localNoteList) {
            if (i.isSelected) return true
        }
        return false
    }

    fun getLocalNoteListWithNewNoteAdded(titleTxtField: String = "Untitled", bodyTxtField: String): MutableList<NoteContents> {
        val noteToAdd = NoteContents(getLocalNoteList.size, titleTxtField, bodyTxtField, formattedDateAndTime(), false)
        val oldNoteList = getNewCopyOfLocalNoteList()
        val newNoteList = SnapshotStateList<NoteContents>()
        newNoteList.addAll(oldNoteList)
        newNoteList.add(noteToAdd)
        return newNoteList
    }

    fun getLocalNoteListWithNoteEdited(index: Int, titleTxtField: String, bodyTxtField: String): MutableList<NoteContents> {
        val oldNoteList = getNewCopyOfLocalNoteList()
        oldNoteList[index].title = titleTxtField
        oldNoteList[index].body = bodyTxtField
        return oldNoteList
    }

    fun getLocalNoteListSortedByMostRecent(list: MutableList<NoteContents>): List<NoteContents> {
        return sortDataObjectsByDateTime(list, dateAndTimeStringFormat)
    }

    private fun sortDataObjectsByDateTime(
        dataObjects: MutableList<NoteContents>,
        pattern: String
    ): List<NoteContents> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26+
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            dataObjects.sortedBy { LocalDateTime.parse(it.lastEdited, formatter) }
        } else {
            // API < 26
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            dataObjects.sortedBy { formatter.parse(it.lastEdited)?.time ?: 0 }
        }
    }

    val getColorTheme get() = colorTheme.value
    val getCurrentScreen get() = currentScreen.value
    val getLocalNoteList get() = localNoteList.value
    val getNoteEditMode get() = noteEditMode.value

}