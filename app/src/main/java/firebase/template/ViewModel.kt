package firebase.template

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import firebase.template.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModel : ViewModel() {
    val NOTE_LIST_SCREEN = 0
    val ADD_NOTE_SCREEN = 1

    private val _noteList = MutableStateFlow(emptyList<Note>())
    val noteList: StateFlow<List<Note>> = _noteList.asStateFlow()

    private val _currentScreen = MutableStateFlow(NOTE_LIST_SCREEN)
    val currentScreen: StateFlow<Int> = _currentScreen.asStateFlow()

    private val _colorTheme = MutableStateFlow(Theme.themeColorsList[0])
    val colorTheme: StateFlow<Themes.Companion.ColorTheme> = _colorTheme.asStateFlow()

    private fun updateNoteList(note: List<Note>) {
        _noteList.value = note
    }

    fun addToNoteList(note: Note) {
        val noteList = getNoteList
        val newList = SnapshotStateList<Note>()
        newList.addAll(noteList)
        newList.add(note)
        updateNoteList(newList)
    }

    fun updateCurrentScreen(value: Int) {
        _currentScreen.value = value
    }

    fun updateColorTheme(theme: Themes.Companion.ColorTheme) {
        _colorTheme.value = theme
    }

    val getNoteList get() = noteList.value
    val getCurrentScreen get() = currentScreen.value
    val getColorTheme get() = colorTheme.value

}