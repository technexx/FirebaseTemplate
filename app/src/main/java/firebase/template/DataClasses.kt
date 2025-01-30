package firebase.template

import androidx.compose.ui.graphics.Color

data class NoteContents(
    var title: String,
    var body: String,
    var lastEdited: String,
//    var backgroundColor: Int,
    var isHighlighted: Boolean
)