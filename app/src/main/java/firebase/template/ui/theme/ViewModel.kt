package firebase.template.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModel : ViewModel() {
    private val _noteList = MutableStateFlow(emptyList<Note>())
    val noteList: StateFlow<List<Note>> = _noteList.asStateFlow()

}