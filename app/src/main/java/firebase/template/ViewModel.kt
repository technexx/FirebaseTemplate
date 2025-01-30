package firebase.template

import android.util.Log.i
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import firebase.template.NoteContents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ViewModel : ViewModel() {
    val NOTE_LIST_SCREEN = 0
    val ADD_NOTE_SCREEN = 1

    private val _colorTheme = MutableStateFlow(0)
    val colorTheme: StateFlow<Int> = _colorTheme.asStateFlow()

    private val _currentScreen = MutableStateFlow(NOTE_LIST_SCREEN)
    val currentScreen: StateFlow<Int> = _currentScreen.asStateFlow()

    private val _localNoteList = MutableStateFlow(emptyList<NoteContents>())
    val localNoteList: StateFlow<List<NoteContents>> = _localNoteList.asStateFlow()

    private val _selectedNotesList = MutableStateFlow(emptyList<NoteContents>())
    val selectedNoteList: StateFlow<List<NoteContents>> = _selectedNotesList.asStateFlow()

    private val _noteEditMode = MutableStateFlow(false)
    val noteEditMode: StateFlow<Boolean> = _noteEditMode.asStateFlow()

    fun updateCurrentScreen(value: Int) {
        _currentScreen.value = value
    }

    fun updateColorTheme(theme: Int) {
        _colorTheme.value = theme
    }

    fun addToLocalNoteList(note: NoteContents) {
        val noteList = getLocalNoteList
        val newList = SnapshotStateList<NoteContents>()
        newList.addAll(noteList)
        newList.add(note)
        updateLocalNoteList(newList)
    }

    fun updateLocalNoteList(note: List<NoteContents>) {
        _localNoteList.value = note
    }

    fun addToSelectedNoteList(note: NoteContents) {
        val currentList = getSelectedNoteList.toMutableList()
        currentList.add(note)
        updateSelectedNoteList(currentList)
    }

    fun highlightSelectedNotes() {
        val selectedNotes = getSelectedNoteList
        val localNoteList = getLocalNoteList

        for (i in getLocalNoteList) {
            if (selectedNotes.contains(i)) {
            }
        }
    }

    fun updateSelectedNoteList(note: List<NoteContents>) {
        _selectedNotesList.value = note
    }

    fun updateNoteEditMode(isActive: Boolean) {
        _noteEditMode.value = isActive
    }

    val getColorTheme get() = colorTheme.value
    val getCurrentScreen get() = currentScreen.value
    val getLocalNoteList get() = localNoteList.value
    val getSelectedNoteList get() = selectedNoteList.value
    val getNoteEditMode get() = noteEditMode.value

}