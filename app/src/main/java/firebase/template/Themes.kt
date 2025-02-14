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
            val topBarBackground: Int,
            val notePadBackground: Int,
            val noteBackground: Int,
            val highlightedNoteBackGround: Int,
            val noteBorder: Int,
            val noteText: Int,
            val textFieldColor: Int,
            val textFieldCursorColor: Int,
            val textFieldPlaceHolderTextColor: Int,
            val iconBackground: Int,
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
            topBarBackground = R.color.orange_1,
            notePadBackground = R.color.yellow_1,
            noteBackground = R.color.yellow_2,
            highlightedNoteBackGround = R.color.blue_400,
            noteBorder = R.color.black,
            noteText = R.color.black,
            textFieldColor = R.color.yellow_1,
            textFieldCursorColor = R.color.grey_300,
            textFieldPlaceHolderTextColor = R.color.black,
            iconBackground = R.color.black
        ),

        //Dark
        Themes.Companion.ColorTheme(
            primaryColor = R.color.white,
            topBarBackground = R.color.black,
            notePadBackground = R.color.black,
            noteBackground = R.color.grey_400,
            highlightedNoteBackGround = R.color.blue_400,
            noteBorder = R.color.white,
            noteText = R.color.white,
            textFieldColor = R.color.white,
            textFieldCursorColor = R.color.white,
            textFieldPlaceHolderTextColor = R.color.white,
            iconBackground = R.color.white
        )
    )
}
