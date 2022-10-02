package com.isaakhanimann.healthassistant.ui.addingestion.time

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.healthassistant.data.room.experiences.entities.SubstanceColor
import com.isaakhanimann.healthassistant.ui.utils.JournalTopAppBar
import com.isaakhanimann.healthassistant.ui.utils.getStringOfPattern
import java.time.LocalDateTime


@Composable
fun ChooseTimeScreen(
    dismissAddIngestionScreens: () -> Unit,
    viewModel: ChooseTimeViewModel = hiltViewModel()
) {
    val dateAndTime = viewModel.dateAndTimeFlow.collectAsState().value
    ChooseTimeScreen(
        createAndSaveIngestion = viewModel::createAndSaveIngestion,
        onSubmitDate = viewModel::onSubmitDate,
        onSubmitTime = viewModel::onSubmitTime,
        dateAndTime = dateAndTime,
        dismissAddIngestionScreens = dismissAddIngestionScreens,
        isLoadingColor = viewModel.isLoadingColor,
        isShowingColorPicker = viewModel.isShowingColorPicker,
        selectedColor = viewModel.selectedColor,
        onChangeColor = { viewModel.selectedColor = it },
        alreadyUsedColors = viewModel.alreadyUsedColorsFlow.collectAsState().value,
        otherColors = viewModel.otherColorsFlow.collectAsState().value,
        previousNotes = viewModel.previousNotesFlow.collectAsState().value,
        note = viewModel.note,
        onNoteChange = {
            viewModel.note = it
        },
        experienceTitleToAddTo = viewModel.experienceTitleToAddToFlow.collectAsState().value,
        check = viewModel::toggleCheck,
        isChecked = viewModel.userWantsToContinueSameExperienceFlow.collectAsState().value
    )
}

@Preview
@Composable
fun ChooseTimeScreenPreview() {
    val alreadyUsedColors = listOf(SubstanceColor.BLUE, SubstanceColor.PINK)
    val otherColors = SubstanceColor.values().filter { color ->
        !alreadyUsedColors.contains(color)
    }
    ChooseTimeScreen(
        createAndSaveIngestion = {},
        onSubmitDate = { _: Int, _: Int, _: Int -> },
        onSubmitTime = { _: Int, _: Int -> },
        dateAndTime = LocalDateTime.now(),
        dismissAddIngestionScreens = {},
        isLoadingColor = false,
        isShowingColorPicker = true,
        selectedColor = SubstanceColor.BLUE,
        onChangeColor = {},
        alreadyUsedColors = alreadyUsedColors,
        otherColors = otherColors,
        previousNotes = listOf(
            "My previous note where I make some remarks",
            "Another previous note and this one is very long, such that it doesn't fit on one line"
        ),
        note = "",
        onNoteChange = {},
        experienceTitleToAddTo = "New Years Eve",
        check = {},
        isChecked = false
    )
}

@Composable
fun ChooseTimeScreen(
    createAndSaveIngestion: () -> Unit,
    onSubmitDate: (Int, Int, Int) -> Unit,
    onSubmitTime: (Int, Int) -> Unit,
    dateAndTime: LocalDateTime,
    dismissAddIngestionScreens: () -> Unit,
    isLoadingColor: Boolean,
    isShowingColorPicker: Boolean,
    selectedColor: SubstanceColor,
    onChangeColor: (SubstanceColor) -> Unit,
    alreadyUsedColors: List<SubstanceColor>,
    otherColors: List<SubstanceColor>,
    previousNotes: List<String>,
    note: String,
    onNoteChange: (String) -> Unit,
    experienceTitleToAddTo: String?,
    check: (Boolean) -> Unit,
    isChecked: Boolean,
) {
    Scaffold(
        topBar = { JournalTopAppBar(title = "Choose Ingestion Time") },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isLoadingColor,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val context = LocalContext.current
                ExtendedFloatingActionButton(
                    onClick = {
                        createAndSaveIngestion()
                        Toast.makeText(
                            context,
                            "Ingestion Added",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismissAddIngestionScreens()
                    },
                    icon = {
                        Icon(
                            Icons.Filled.Done,
                            contentDescription = "Next"
                        )
                    },
                    text = { Text("Done") },
                )
            }
        }
    ) {
        Column {
            LinearProgressIndicator(progress = 0.9f, modifier = Modifier.fillMaxWidth())
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                if (isShowingColorPicker) {
                    ColorPicker(
                        selectedColor = selectedColor,
                        onChangeOfColor = onChangeColor,
                        alreadyUsedColors = alreadyUsedColors,
                        otherColors = otherColors
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DatePickerButton(
                        day = dateAndTime.dayOfMonth,
                        month = dateAndTime.monthValue,
                        year = dateAndTime.year,
                        onSubmitDate = onSubmitDate,
                        dateString = dateAndTime.getStringOfPattern("EEE dd MMM yyyy"),
                        modifier = Modifier.fillMaxWidth()
                    )
                    TimePickerButton(
                        hour = dateAndTime.hour,
                        minute = dateAndTime.minute,
                        onSubmitTime = onSubmitTime,
                        timeString = dateAndTime.getStringOfPattern("HH:mm"),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                NoteSection(
                    previousNotes,
                    note,
                    onNoteChange
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (experienceTitleToAddTo != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { check(isChecked.not()) }) {
                        Checkbox(checked = isChecked, onCheckedChange = check)
                        Text(text = "Add to $experienceTitleToAddTo")
                    }
                }
            }
        }
    }
}

@Composable
fun NoteSection(
    previousNotes: List<String>,
    note: String,
    onNoteChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isShowingSuggestions by remember { mutableStateOf(true) }
    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.heightIn(max = TextFieldDefaults.MinHeight * 6)
    ) {
        item {
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text(text = "Notes") },
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    isShowingSuggestions = false
                }),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            isShowingSuggestions = true
                        }
                    }
            )
        }
        if (previousNotes.isNotEmpty() && isShowingSuggestions) {
            items(previousNotes.size) { i ->
                val previousNote = previousNotes[i]
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            focusManager.clearFocus()
                            isShowingSuggestions = false
                            onNoteChange(previousNote)
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            Modifier.size(15.dp)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = previousNote, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerButton(
    hour: Int,
    minute: Int,
    onSubmitTime: (Int, Int) -> Unit,
    timeString: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val timePickerDialog = TimePickerDialog(
        context,
        { _, newHour: Int, newMinute: Int ->
            onSubmitTime(newHour, newMinute)
        }, hour, minute, false
    )
    OutlinedButton(onClick = timePickerDialog::show, modifier = modifier) {
        Text(timeString)
    }
}
