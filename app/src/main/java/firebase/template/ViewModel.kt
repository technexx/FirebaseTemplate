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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ViewModel : ViewModel() {
    val NOTE_LIST_SCREEN = 0
    val ADD_NOTE_SCREEN = 1
    val dateStringFormat = "MMM dd, yyyy"

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

    fun addToLocalNoteList(note: NoteContents) {
        val noteList = getLocalNoteList
        val newList = SnapshotStateList<NoteContents>()
        newList.addAll(noteList)
        newList.add(note)
        updateLocalNoteList(newList)
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

    private fun getNewCopyOfLocalNoteList(): SnapshotStateList<NoteContents> {
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

    //TODO: Sort by time as well, and then:
    //TODO: Put sorted dates into note contents object and update its stateflow.
    @RequiresApi(Build.VERSION_CODES.O)
    fun sortLocalNoteListByMostRecent() {
        val list = getNewCopyOfLocalNoteList()
        val dateOnlyList = emptyList<String>().toMutableList()
        val timeOnlyList = emptyList<String>().toMutableList()

        for (i in list) {
            val array = i.lastEdited.split(" - ")
            dateOnlyList.add(array[0])
            timeOnlyList.add(array[1])
        }
        val sortedDates = sortDateStrings(dateOnlyList)
        val sortedTimes = sortDateTimes(timeOnlyList, "hh:mm a")

        Log.i("test", "dates are $dateOnlyList")
        Log.i("test", "times are $timeOnlyList")
    }

    private fun sortDateStrings(dateStrings: List<String>): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26+
            val formatter = DateTimeFormatter.ofPattern(dateStringFormat)
            val dates = dateStrings.map { LocalDate.parse(it, formatter) }
            val sortedDates = dates.sorted()
            sortedDates.map { formatter.format(it) }
        } else {
            // API < 26
            val formatter = SimpleDateFormat(dateStringFormat, Locale.getDefault())
            val dates = dateStrings.map {
                formatter.parse(it)?.time
            }
            val sortedDates = dates.filterNotNull().sortedDescending()
            sortedDates.map { Date(it).toString() }
        }
    }

    private fun sortDateTimes(timeStrings: List<String>, pattern: String): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val times = timeStrings.map { LocalTime.parse(it, formatter) }
            val sortedTimes = times.sorted() // Or sortedDescending()
            sortedTimes.map { it.format(formatter) }
        } else {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            val dates = timeStrings.mapNotNull { formatter.parse(it) } // Parse to Date
            val sortedDates = dates.sortedBy { it.time } // Sort by milliseconds
            sortedDates.map { formatter.format(it) } // Format back to string
        }

    }

    val getColorTheme get() = colorTheme.value
    val getCurrentScreen get() = currentScreen.value
    val getLocalNoteList get() = localNoteList.value
    val getNoteEditMode get() = noteEditMode.value

}