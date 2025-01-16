package firebase.template.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import firebase.template.AnimatedComposable
import firebase.template.R
import firebase.template.RegText
import firebase.template.ui.theme.TestData.Companion.noteList

class NotePad(private val viewModel: ViewModel) {
    @Composable
    fun NoteBoard() {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.grey_400))
        )
        {
            Column {
                NoteCards()
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {
                    AddButton(modifier = Modifier
                        .size(50.dp))
                }
            }
        }
    }
    @Composable
    fun NoteCards() {
        LazyColumn (
            modifier = Modifier
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            items (TestData.noteList.size) { index ->
                NoteText(noteList, index)
            }
        }
    }

    @Composable
    fun NoteText(note: List<Note>, i: Int) {
        Card(modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.grey_200),
            ),
            border = BorderStroke(2.dp, Color.Black),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
        ) {
            Column(modifier = Modifier
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

    @Composable
    fun AddButton(modifier: Modifier = Modifier) {
        val currentScreen = viewModel.noteList.collectAsStateWithLifecycle()

        Box() {
            OutlinedButton(
                onClick = {
                    viewModel.updateCurrentScreen(1)
                },
                modifier = modifier,
                shape = CircleShape,
                border = BorderStroke(4.dp, Color.Blue),
                contentPadding = PaddingValues(0.dp),  //avoid the little icon
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            ) {
                Icon(Icons.Default.Add, contentDescription = "content description")
            }
        }
    }
}