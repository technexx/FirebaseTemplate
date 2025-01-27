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
            val textFieldColor: Int,
            val textFieldCursorColor: Int,
            val textFieldUnFocusedPlaceHolderTextColor: Int,
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
            textFieldColor = R.color.black,
            textFieldCursorColor = R.color.black,
            textFieldUnFocusedPlaceHolderTextColor = R.color.black,
        ),

        //Dark
        Themes.Companion.ColorTheme(
            primaryColor = R.color.white,
            notePadBackground = R.color.black,
            textFieldColor = R.color.white,
            textFieldCursorColor = R.color.white,
            textFieldUnFocusedPlaceHolderTextColor = R.color.white,
        )
    )
}
