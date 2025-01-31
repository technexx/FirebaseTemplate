package firebase.template

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import firebase.template.Database.NoteData
import firebase.template.ui.theme.showPlaceHolderTextIfFieldIsEmpty
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class NotePad(private val viewModel: ViewModel, private val roomInteraction: RoomInteractions) {
    @Composable
    fun NoteBoard() {
        val colorTheme = viewModel.colorTheme.collectAsStateWithLifecycle()
        val currentScreen = viewModel.currentScreen.collectAsStateWithLifecycle()

        Box(modifier = Modifier
            .fillMaxSize()
            //This should recompose child composables.
            .background(colorResource(id = Theme.themeColorsList[colorTheme.value].defaultNoteBackground))
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
        val localNoteList = viewModel.localNoteList.collectAsStateWithLifecycle()

        LazyColumn (
            modifier = Modifier
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            items (localNoteList.value.size) { index ->
                NoteContainer(localNoteList.value, index)
            }
        }
    }

    @Composable
    fun NoteContainer(note: List<NoteContents>, i: Int) {
        var colorTheme = viewModel.colorTheme.collectAsStateWithLifecycle()
        val localNoteList = viewModel.localNoteList.collectAsStateWithLifecycle()
        var editMode = viewModel.noteEditMode.collectAsStateWithLifecycle()
        var isLongPressed by remember { mutableStateOf(false) }

        //For each note (card), have background color correspond to isHighlighted boolean from its data object.
        val backgroundColor = if (!localNoteList.value[i].isHighlighted) Theme.themeColorsList[viewModel.getColorTheme].defaultNoteBackground else Theme.themeColorsList[viewModel.getColorTheme].highlightedNoteBackGround
        val borderColor = Theme.themeColorsList[viewModel.getColorTheme].noteBorder
        val textColor = Theme.themeColorsList[viewModel.getColorTheme].noteText

        Card(modifier = Modifier
            .fillMaxSize()
            //TODO: .pointerInput overwriting .selectable
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        //If in edit mode and note clicked, highlight if not highlighted and vice-versa.
                        Log.i("test", "edit mode is ${viewModel.getNoteEditMode}")
                        if (viewModel.getNoteEditMode) {
                            if (viewModel.getLocalNoteList[i].isHighlighted) viewModel.editLocalNoteListHighlight(i, false) else viewModel.editLocalNoteListHighlight(i, true)
                        }
                },
                    onLongPress = {
                        isLongPressed = true
                        //Long press should trigger edit mode and highlight only note selected. Further highlights will be regular presses.
                        if (viewModel.getSelectedNoteList.isEmpty()) {
                            viewModel.addToSelectedNoteList(viewModel.getLocalNoteList[i])
                            viewModel.editLocalNoteListHighlight(i, true)
                            viewModel.updateNoteEditMode(true)
                        }
//                        Log.i("test", "selected note list post add is ${viewModel.getSelectedNoteList}")
                    }
                )
            },
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = backgroundColor),
            ),
            border = BorderStroke(2.dp, colorResource(borderColor)),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)){
                RegText(text = note[i].title, fontSize = 18, color = colorResource(textColor), fontWeight = FontWeight.Bold)
                RegText(text = note[i].body, fontSize = 15, color = colorResource(textColor))
                Row(modifier = Modifier
                    .fillMaxSize(),
                    horizontalArrangement = Arrangement.End) {
                    RegText(text = note[i].lastEdited, fontSize = 15, color = colorResource(textColor))
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
                val newNote = NoteContents(titleTxtField, bodyTxtField, dateTime(),false)
                viewModel.addToLocalNoteList(newNote)
                viewModel.updateCurrentScreen(viewModel.NOTE_LIST_SCREEN)

                coroutineScope.launch {
                    val databaseNote = NoteData(null, newNote.title, newNote.body, newNote.lastEdited)
                    roomInteraction.insertNoteIntoDatabase(databaseNote)
                }
            }
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = Theme.themeColorsList[0].notePadBackground))) {

                TextField(modifier = Modifier,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = titleTxtField,
                    placeholder = {
                        Text(showPlaceHolderTextIfFieldIsEmpty(titleTxtField, "Title")) },
                    onValueChange = {
                        titleTxtField = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldColor), fontSize = 22.sp, fontWeight = FontWeight.Bold),
                    colors = TextFieldDefaults.colors(
                        cursorColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldCursorColor),
                        focusedContainerColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldColor),
                        unfocusedContainerColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldColor),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedPlaceholderColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldPlaceHolderTextColor),
                        unfocusedPlaceholderColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldPlaceHolderTextColor),
                    )
                )

                HorizontalDivider(color = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].primaryColor), thickness = 2.dp)

                TextField(modifier = Modifier,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = bodyTxtField,
                    placeholder = { Text(showPlaceHolderTextIfFieldIsEmpty(titleTxtField, "Note")) },
                    onValueChange = {
                        bodyTxtField = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldColor), fontSize = 16.sp),

                    colors = TextFieldDefaults.colors(
                        cursorColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldCursorColor),
                        focusedContainerColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldColor),
                        unfocusedContainerColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldColor),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedPlaceholderColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldPlaceHolderTextColor),
                        unfocusedPlaceholderColor = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].textFieldPlaceHolderTextColor),
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

