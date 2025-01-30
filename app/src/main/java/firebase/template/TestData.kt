package firebase.template

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import firebase.template.NoteContents
class TestData {
    var testNoteList: SnapshotStateList<NoteContents> = mutableStateListOf(
        NoteContents("This is my first note header!", "Here is some body stuff", "Jan 1, 1900"),
        NoteContents("This is my second note header!", "Here is some leg stuff", "Jan 1, 2000"),
        NoteContents("This is my third note header!", "Here is some feet stuff", "Jan 1, 2100")
    )
}

