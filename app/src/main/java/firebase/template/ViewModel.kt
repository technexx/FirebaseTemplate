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
    //Either list of notes, or single note. Updated as currentScreen stateflow.
    val NOTE_LIST_SCREEN = 0
    val NOTE_SCREEN_ANIMATED = 1
    val NOTE_SCREEN_REFRESHED = 2

    //Applies to single note screen. NOTE_SCREEN_MODE designates whether we are adding or editing a note. Not updated as state flow.
    var NOTE_SCREEN_MODE = 0
    val ADDING_NOTE = 0
    val EDITING_SINGLE_NOTE = 1

    //Used by RoomInteractions to save to database.
    var titleTxtField = ""
    var bodyTxtField = ""
    var selectedNoteIndex: Int = 0

    //Used to retain note title/text fields when note is first open, to determine if note has been edited.
    var uneditedTitleTxtField = ""
    var uneditedBodyTxtField = ""

    private val _colorTheme = MutableStateFlow(0)
    val colorTheme: StateFlow<Int> = _colorTheme.asStateFlow()

    private val _currentScreen = MutableStateFlow(NOTE_LIST_SCREEN)
    val currentScreen: StateFlow<Int> = _currentScreen.asStateFlow()

    private val _showSettingsDropDown = MutableStateFlow(false)
    val showSettingsDropDown: StateFlow<Boolean> = _showSettingsDropDown.asStateFlow()

    private val _localNoteList = MutableStateFlow(emptyList<NoteContents>())
    val localNoteList: StateFlow<List<NoteContents>> = _localNoteList.asStateFlow()

    private val _editingNoteList = MutableStateFlow(false)
    val noteEditMode: StateFlow<Boolean> = _editingNoteList.asStateFlow()

    private val _noteHasBeenEdited = MutableStateFlow(false)
    val noteHasBeenEdited: StateFlow<Boolean> = _noteHasBeenEdited.asStateFlow()

    private val _singleNoteScreenAsAnimation = MutableStateFlow(false)
    val singleNoteScreenAsAnimation: StateFlow<Boolean> = _singleNoteScreenAsAnimation.asStateFlow()

    private val _doesCurrentTitleAndBodyTextMatchUneditedText = MutableStateFlow(false)
    val doesCurrentTitleAndBodyTextMatchUneditedText: StateFlow<Boolean> = _doesCurrentTitleAndBodyTextMatchUneditedText.asStateFlow()

    private val _noteTitleText = MutableStateFlow("")
    val noteTitleText: StateFlow<String> = _noteTitleText.asStateFlow()

    private val _noteBodyText = MutableStateFlow("")
    val noteBodyText: StateFlow<String> = _noteBodyText.asStateFlow()

    fun updateCurrentScreen(value: Int) {
        _currentScreen.value = value
    }

    fun updateColorTheme(theme: Int) {
        _colorTheme.value = theme
    }

    fun updateShowSettingsDropDown(show: Boolean) {
        _showSettingsDropDown.value = show
    }

    fun updateLocalNoteList(note: List<NoteContents>) {
        _localNoteList.value = note
    }

    fun updateNoteEditMode(isActive: Boolean) {
        _editingNoteList.value = isActive
    }

    fun updateNoteHasBeenEdited(edited: Boolean) {
        _noteHasBeenEdited.value = edited
    }

    fun updateSingleNoteAsAnimation(asAnimation: Boolean) {
        _singleNoteScreenAsAnimation.value = asAnimation
    }

    fun updateDoesCurrentTitleAndBodyTextMatchUneditedText() {
        _doesCurrentTitleAndBodyTextMatchUneditedText.value = (titleTxtField == uneditedTitleTxtField && bodyTxtField == uneditedBodyTxtField)
    }

    fun updateNoteTitleText(text: String) {
        _noteTitleText.value = text
    }

    fun updateNoteBodyText(text: String) {
        _noteBodyText.value = text
    }

    fun setGloballyAccessedTextAndUneditedTextFields() {
        if (getLocalNoteList.isNotEmpty()) {
            //Globally accessed title and body set to selected note.
            titleTxtField = savedNoteTitle(selectedNoteIndex)
            bodyTxtField = savedNoteBody(selectedNoteIndex)
            //Globally accessed unedited title and body set to selected note.
            uneditedTitleTxtField = titleTxtField
            uneditedBodyTxtField = bodyTxtField
        }
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

        println("note selected is ${newList[index]}")
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

    fun getLocalNoteListWithNewNoteAdded(id: Int, titleTxtField: String = "Untitled", bodyTxtField: String): MutableList<NoteContents> {
        val noteToAdd = NoteContents(id, titleTxtField, bodyTxtField, formattedDateAndTime(), false)
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

    val getNoteTitleText get() = noteTitleText.value
    val getNoteBodyText get() = noteBodyText.value
    val getColorTheme get() = colorTheme.value
    val getCurrentScreen get() = currentScreen.value
    val getDropDownIsVisible get() = showSettingsDropDown.value
    val getLocalNoteList get() = localNoteList.value
    val getEditingNoteList get() = noteEditMode.value
    val getNoteHasBeenEdited get() = noteHasBeenEdited.value
}