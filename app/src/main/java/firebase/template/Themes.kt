package firebase.template

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class Themes {
    companion object {
        data class ColorTheme(
            val notePadBackground: Int,
        )

    }
}

object Theme {
    val LIGHT = 0
    val DARK = 1

    val themeColorsList: SnapshotStateList<Themes.Companion.ColorTheme> = mutableStateListOf(
        //Light
        Themes.Companion.ColorTheme(
            notePadBackground = R.color.grey_400,
        ),

        //Dark
        Themes.Companion.ColorTheme(
            notePadBackground = R.color.black
        )
    )
}
