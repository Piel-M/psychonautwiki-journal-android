package com.isaakhanimann.healthassistant.ui.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.outlined.ContactSupport
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.healthassistant.ui.theme.horizontalPadding

@Preview
@Composable
fun SettingsPreview() {
    SettingsScreen(
        deleteEverything = {},
        navigateToFAQ = {},
        importFile = {}
    )
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigateToFAQ: () -> Unit
) {
    SettingsScreen(
        navigateToFAQ,
        deleteEverything = viewModel::deleteEverything,
        importFile = viewModel::importFile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateToFAQ: () -> Unit,
    deleteEverything: () -> Unit,
    importFile: (uri: Uri?) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val launcher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
                    importFile(result)
                }
            TextButton(
                onClick = {
                    launcher.launch("*/*")
                },
                modifier = Modifier.padding(horizontal = horizontalPadding)
            ) {
                Text("Open file")
            }
            Divider()
            val uriHandler = LocalUriHandler.current
            TextButton(
                onClick = {
                    uriHandler.openUri("https://psychonautwiki.org/wiki/Responsible_drug_use")
                },
                modifier = Modifier.padding(horizontal = horizontalPadding)
            ) {
                Icon(
                    Icons.Default.OpenInBrowser,
                    contentDescription = "Open Link",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Responsible Drug Use")
                Spacer(modifier = Modifier.weight(1f))
            }
            Divider()
            TextButton(
                onClick = navigateToFAQ,
                modifier = Modifier.padding(horizontal = horizontalPadding)
            ) {
                Icon(
                    Icons.Outlined.QuestionAnswer,
                    contentDescription = "Frequently Asked Questions",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("FAQ")
                Spacer(modifier = Modifier.weight(1f))
            }
            Divider()
            TextButton(
                onClick = {
                    uriHandler.openUri("https://t.me/isaakhanimann")
                },
                modifier = Modifier.padding(horizontal = horizontalPadding)
            ) {
                Icon(
                    Icons.Outlined.ContactSupport,
                    contentDescription = "Contact Support",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Question / Feedback / Bug Report")
                Spacer(modifier = Modifier.weight(1f))
            }
            Divider()
            TextButton(
                onClick = {
                    uriHandler.openUri("https://github.com/isaakhanimann/HealthAssistant")
                },
                modifier = Modifier.padding(horizontal = horizontalPadding)
            ) {
                Icon(
                    Icons.Filled.Code,
                    contentDescription = "Open Source Code",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Source Code")
                Spacer(modifier = Modifier.weight(1f))
            }
            Divider()
            var isShowingDeleteDialog by remember { mutableStateOf(false) }
            TextButton(
                onClick = {
                    isShowingDeleteDialog = true
                },
                modifier = Modifier.padding(horizontal = horizontalPadding)
            ) {
                Icon(
                    Icons.Filled.DeleteForever,
                    contentDescription = "Delete",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Delete Everything")
                Spacer(modifier = Modifier.weight(1f))
            }
            if (isShowingDeleteDialog) {
                val context = LocalContext.current
                AlertDialog(
                    onDismissRequest = { isShowingDeleteDialog = false },
                    title = {
                        Text(text = "Delete Everything?")
                    },
                    text = {
                        Text("This will delete all your experiences, ingestions and custom substances.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                isShowingDeleteDialog = false
                                deleteEverything()
                                Toast.makeText(
                                    context,
                                    "Everything Deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { isShowingDeleteDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
            Divider()
            Text(
                text = "Last Substance Update: 25. October 2022",
                modifier = Modifier
                    .padding(horizontal = horizontalPadding, vertical = 10.dp)
                    .fillMaxWidth()
            )
        }
    }
}