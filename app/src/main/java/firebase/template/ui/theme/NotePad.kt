package firebase.template.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import firebase.template.R
import firebase.template.RegText
import firebase.template.ui.theme.TestData.Companion.noteList

class NotePad {
    @Composable
    fun NoteCards() {
        LazyColumn (
            modifier = Modifier
                .background(colorResource(id = R.color.grey_400))
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            items (TestData.noteList.size) { index ->
                NoteText(noteList, index)
            }
        }
    }

    @Composable
    fun NoteText(note: List<Note>, i: Int) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.grey_200),
            ),
            border = BorderStroke(2.dp, Color.Black),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
        ) {
            Column (modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)){
                RegText(text = note[i].title, fontSize = 18, fontWeight = FontWeight.Bold)
                RegText(text = note[i].body, fontSize = 15)
                Row(modifier = Modifier
                    .fillMaxSize(),
                    horizontalArrangement = Arrangement.End) {
                    RegText(text = note[i].lastEdited, fontSize = 15)
                }
            }

        }
    }
}