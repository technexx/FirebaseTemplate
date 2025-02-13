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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import firebase.template.Database.NoteData
import firebase.template.ui.theme.showPlaceHolderTextIfFieldIsEmpty
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class NotePad(private val viewModel: ViewModel, private val roomInteraction: RoomInteractions) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainBoard() {
//        val colorTheme = viewModel.colorTheme.collectAsStateWithLifecycle()
        val currentScreen = viewModel.currentScreen.collectAsStateWithLifecycle()
        val editMode = viewModel.noteEditMode.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = colorResource(Theme.themeColorsList[viewModel.getColorTheme].topBarBackground),
                        titleContentColor = colorResource(Theme.themeColorsList[viewModel.getColorTheme].noteText),
                    ),
                    title = {
                        Text("Meal Decider")
                    },
                    navigationIcon = {
                        if (editMode.value) {
                            MaterialIconButton(
                                icon = Icons.Filled.KeyboardArrowLeft,
                                description = "back arrow",
                                tint = Theme.themeColorsList[viewModel.getColorTheme].iconBackground
                            ) {
                                viewModel.updateNoteEditMode(false)
                                viewModel.markAllNotesAsUnselected()
                            }
                        } else {
                            MaterialIconButton(
                                icon = Icons.Filled.Menu,
                                description = "back arrow",
                                tint = Theme.themeColorsList[viewModel.getColorTheme].iconBackground
                            ) {

                            }
                        }
                    },
                    actions = {
                        if (editMode.value && viewModel.getLocalNoteList.isNotEmpty()) {
                            MaterialIconButton(
                                icon = Icons.Filled.Delete,
                                description = "delete",
                                tint = Theme.themeColorsList[viewModel.getColorTheme].iconBackground
                            ) {
                                coroutineScope.launch {
                                    roomInteraction.deleteSelectedNotesFromDatabase()
                                    viewModel.removeFromLocalNotesList()
                                }
                            }
                        }

                        //For drop down menu.
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopEnd)
                        ) {

                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    //This should recompose child composables.
                    .background(colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].notePadBackground))
                )
                {
                    Column {
                        if (currentScreen.value == viewModel.NOTE_LIST_SCREEN) {
                            NoteLazyColumn()
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
                }            }
        }
    }

    @Composable
    fun NoteLazyColumn() {
        val localNoteList = viewModel.localNoteList.collectAsStateWithLifecycle()

        LazyColumn (
            modifier = Modifier
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            items (localNoteList.value.size) { index ->
                NoteContainer(index)
            }
        }
    }

    @Composable
    fun NoteContainer(index: Int) {
        val localNoteList = viewModel.localNoteList.collectAsStateWithLifecycle()
        var isLongPressed by remember { mutableStateOf(false) }

        val backgroundColor = if (localNoteList.value[index].isSelected){
            Theme.themeColorsList[viewModel.getColorTheme].highlightedNoteBackGround
        } else {
            Theme.themeColorsList[viewModel.getColorTheme].noteBackground
        }

        val borderColor = Theme.themeColorsList[viewModel.getColorTheme].noteBorder
        val textColor = Theme.themeColorsList[viewModel.getColorTheme].noteText

        Card(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        //If in edit mode and note clicked, toggle selection.
                        if (viewModel.getNoteEditMode) {
                            if (viewModel.getLocalNoteList[index].isSelected) {
                                viewModel.markNoteAsSelectedOrUnselected(false, index)
                            } else {
                                viewModel.markNoteAsSelectedOrUnselected(true, index)
                            }
                        }
                    },
                    onLongPress = {
                        isLongPressed = true
                        //Triggers edit mode and single highlight if no notes are selected.
                        if (!viewModel.areAnyNotesSelected()) {
                            viewModel.updateNoteEditMode(true)
                            viewModel.markNoteAsSelectedOrUnselected(true, index)
                        }
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
                RegText(text = viewModel.getLocalNoteList[index].title, fontSize = 18, color = colorResource(textColor), fontWeight = FontWeight.Bold)
                RegText(text = viewModel.getLocalNoteList[index].body, fontSize = 15, color = colorResource(textColor))
                Row(modifier = Modifier
                    .fillMaxSize(),
                    horizontalArrangement = Arrangement.End) {
                    RegText(text = viewModel.getLocalNoteList[index].lastEdited, fontSize = 15, color = colorResource(textColor))
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

        AnimatedComposable(
            backHandler = {
                if (titleTxtField.isBlank()) titleTxtField = "Untitled"
                val newNote = NoteContents(viewModel.getLocalNoteList.size, titleTxtField, bodyTxtField, dateTime(), false)
                viewModel.addToLocalNoteList(newNote)
                viewModel.updateCurrentScreen(viewModel.NOTE_LIST_SCREEN)
                Log.i("test", "id of local note added is ${newNote.id}")

                coroutineScope.launch {
                    val databaseNote = NoteData(null, newNote.id, newNote.title, newNote.body, newNote.lastEdited)
                    roomInteraction.insertNoteIntoDatabase(databaseNote)
                    Log.i("test", "id of db note added is ${newNote.id}")
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
                    textStyle = TextStyle(color = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].noteText), fontSize = 22.sp, fontWeight = FontWeight.Bold),
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
                    placeholder = { Text(showPlaceHolderTextIfFieldIsEmpty(bodyTxtField, "Note")) },
                    onValueChange = {
                        bodyTxtField = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = colorResource(id = Theme.themeColorsList[viewModel.getColorTheme].noteText), fontSize = 16.sp),

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

