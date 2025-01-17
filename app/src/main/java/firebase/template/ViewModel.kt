package firebase.template

import androidx.lifecycle.ViewModel
import firebase.template.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModel : ViewModel() {
    val NOTE_LIST = 0
    val ADD_NOTE = 1

    private val _noteList = MutableStateFlow(emptyList<Note>())
    val noteList: StateFlow<List<Note>> = _noteList.asStateFlow()

    private val _currentScreen = MutableStateFlow(NOTE_LIST)
    val currentScreen: StateFlow<Int> = _currentScreen.asStateFlow()

    private val _colorTheme = MutableStateFlow(Theme.themeColorsList[0])
    val colorTheme: StateFlow<Themes.Companion.ColorTheme> = _colorTheme.asStateFlow()

    fun updateCurrentScreen(value: Int) {
        _currentScreen.value = value
    }

    fun updateColorTheme(theme: Themes.Companion.ColorTheme) {
        _colorTheme.value = theme
    }

}