package firebase.template.ui.theme

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class TestData {
    companion object {
        var noteList: SnapshotStateList<Note> = mutableStateListOf(
            Note("This is my first note header!", "Here is some body stuff", "Jan 1, 1900"),
            Note("This is my second note header!", "Here is some leg stuff", "Jan 1, 2000"),
            Note("This is my third note header!", "Here is some feet stuff", "Jan 1, 2100")
        )
    }

}

