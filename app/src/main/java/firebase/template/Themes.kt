package firebase.template

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class Themes {
    companion object {
        data class ColorTheme(
            val primaryColor: Int,
            val notePadBackground: Int,
            val defaultNoteBackground: Int,
            val highlightedNoteBackGround: Int,
            val noteBorder: Int,
            val noteText: Int,
            val textFieldColor: Int,
            val textFieldCursorColor: Int,
            val textFieldPlaceHolderTextColor: Int,
        )

    }
}

object Theme {
    val LIGHT = 0
    val DARK = 1

    val themeColorsList: SnapshotStateList<Themes.Companion.ColorTheme> = mutableStateListOf(
        //Light
        Themes.Companion.ColorTheme(
            primaryColor = R.color.black,
            notePadBackground = R.color.grey_400,
            defaultNoteBackground = R.color.grey_200,
            highlightedNoteBackGround = R.color.blue_400,
            noteBorder = R.color.black,
            noteText = R.color.black,
            textFieldColor = R.color.grey_400,
            textFieldCursorColor = R.color.grey_400,
            textFieldPlaceHolderTextColor = R.color.black,
        ),

        //Dark
        Themes.Companion.ColorTheme(
            primaryColor = R.color.white,
            notePadBackground = R.color.black,
            defaultNoteBackground = R.color.grey_400,
            highlightedNoteBackGround = R.color.blue_400,
            noteBorder = R.color.white,
            noteText = R.color.white,
            textFieldColor = R.color.white,
            textFieldCursorColor = R.color.white,
            textFieldPlaceHolderTextColor = R.color.white,
        )
    )
}
