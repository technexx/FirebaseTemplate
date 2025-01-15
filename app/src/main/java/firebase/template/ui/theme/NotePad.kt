package firebase.template.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import firebase.template.NoteSwiper
import firebase.template.AnimatedTransitionDialog
import firebase.template.R
import firebase.template.RegText

class NotePad {
    @Composable
    fun NoteCards() {
        AnimatedTransitionDialog(modifier = Modifier
            .background(Color.Transparent)
            .fillMaxSize(),
            onDismissRequest = {
            },
            content = {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colorResource(R.color.white)
                ) {
                    Box(modifier = Modifier
                    ) {
                        Column(modifier = Modifier
                            .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween)
                        {
                            LazyColumn (
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ){
                                items (TestData.noteList.size) { index ->
                                    NoteText(noteList = TestData.noteList)
                                }
                            }
//                                            SwipeToDismissExample()
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun NoteText(noteList: List<Note>) {
        Column {
            for (i in noteList.indices) {
                RegText(text = noteList[i].title)
                RegText(text = noteList[i].body)
                RegText(text = noteList[i].lastEdited)
            }   
        }
    }
}