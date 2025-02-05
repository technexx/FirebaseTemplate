package firebase.template

import androidx.compose.ui.graphics.Color

data class NoteContents(
    var id: Int,
    var title: String,
    var body: String,
    var lastEdited: String,
    var isSelected: Boolean
//    var backgroundColor: Int,
//    var isHighlighted: Boolean
)