package firebase.template

import android.util.Log
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import firebase.template.TestData.Companion.noteList
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class NotePad(private val viewModel: ViewModel, private val roomInteraction: RoomInteractions) {
    @Composable
    fun NoteBoard() {
        val currentScreen = viewModel.currentScreen.collectAsStateWithLifecycle()
        val colorTheme = viewModel.colorTheme.collectAsStateWithLifecycle()

        Box(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = colorTheme.value.notePadBackground))
        )
        {
            Column {
                if (currentScreen.value == viewModel.NOTE_LIST_SCREEN) {
                    NoteCards()
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End) {
                        AddButton(modifier = Modifier
                            .size(50.dp))
                    }
                }

                if (currentScreen.value == viewModel.ADD_NOTE_SCREEN) {
                    AddNoteScreen()
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
        Box() {
            OutlinedButton(
                onClick = {
                    viewModel.updateCurrentScreen(viewModel.ADD_NOTE_SCREEN)
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

    @Composable
    fun AddNoteScreen() {
        var titleTxtField by remember { mutableStateOf("") }
        var bodyTxtField by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
        val colorTheme = viewModel.colorTheme.collectAsStateWithLifecycle()

        AnimatedComposable(
            backHandler = {
                if (titleTxtField.isBlank()) titleTxtField = "Untitled"
                val newNote = Note(titleTxtField, bodyTxtField, dateTime())
                viewModel.addToNoteList(newNote)
                viewModel.updateCurrentScreen(viewModel.NOTE_LIST_SCREEN)

                coroutineScope.launch {
//                    roomInteraction.insertNoteIntoDatabase(newNote)
                    Log.i("noteList", "database is ${roomInteraction.getAllNotesFromDatabase()}")
                }

                Log.i("noteList", "note added is $newNote")
                Log.i("noteList", "note list is ${viewModel.getNoteList}")
            }
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = Theme.themeColorsList[0].notePadBackground))) {

                TextField(modifier = Modifier,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = titleTxtField,
                    placeholder = { Text( "Add a title!") },
                    onValueChange = {
                        titleTxtField = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = colorResource(id = colorTheme.value.textFieldColor), fontSize = 22.sp, fontWeight = FontWeight.Bold),
                    colors = TextFieldDefaults.colors(
                        cursorColor = colorResource(id = colorTheme.value.textFieldCursorColor),
                        focusedContainerColor = colorResource(id = colorTheme.value.notePadBackground),
                        unfocusedContainerColor = colorResource(id = colorTheme.value.notePadBackground),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedPlaceholderColor = Color.Transparent,
                        unfocusedPlaceholderColor = colorResource(id = colorTheme.value.textFieldUnFocusedPlaceHolderTextColor),
                    )
                )

                TextField(modifier = Modifier,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = bodyTxtField,
                    placeholder = { Text( "Add a note!") },
                    onValueChange = { 
                        bodyTxtField = it           
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = colorResource(id = colorTheme.value.textFieldColor), fontSize = 22.sp, fontWeight = FontWeight.Bold),

                    colors = TextFieldDefaults.colors(
                        cursorColor = colorResource(id = colorTheme.value.textFieldCursorColor),
                        focusedContainerColor = colorResource(id = colorTheme.value.notePadBackground),
                        unfocusedContainerColor = colorResource(id = colorTheme.value.notePadBackground),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedPlaceholderColor = Color.Transparent,
                        unfocusedPlaceholderColor = colorResource(id = colorTheme.value.textFieldUnFocusedPlaceHolderTextColor),
                    )
                )
            }
        }
    }

    private fun dateTime(): String {
        val date = SimpleDateFormat.getDateInstance()
        return date.format(Date())
    }
}

