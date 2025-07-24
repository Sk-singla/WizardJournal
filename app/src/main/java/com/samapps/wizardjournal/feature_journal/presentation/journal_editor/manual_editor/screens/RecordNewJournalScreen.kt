package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.material3.Button
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.R
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.app.utils.rememberSpeechRecognizer
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.components.JournalContentPreview
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.events.RecordNewJournalEvent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecordNewJournalScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: JournalEditorViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val speechRecognizer = rememberSpeechRecognizer(
        onResult = { text ->
            viewModel.onRecordNewJournalEvent(
                RecordNewJournalEvent.CaptureText(text)
            )
        },
        onError = { error ->
            scope.launch {
                snackbarHostState.showSnackbar("Error: $error")
            }
        }
    )
    val recordAudioPermissionState = speechRecognizer.recordAudioPermissionState
    val capturedText by viewModel.journalContent.collectAsState()

    fun handleToggleRecording() {
        if (speechRecognizer.isRecording) {
            speechRecognizer.stopListening()
        } else {
            speechRecognizer.startListening()
        }
    }

    RecordNewJournalInternal(
        modifier = modifier,
        capturedText = capturedText,
        isRecording = speechRecognizer.isRecording,
        isRecordingPermissionGranted = recordAudioPermissionState.isGranted,
        isRecordingPermissionPermanentlyDenied = recordAudioPermissionState.isPermanentlyDenied,
        onToggleRecording = ::handleToggleRecording,
        onCreateManually = {
            navController.navigate(Routes.CreateNewJournalManualEditing)
        },
        onSave = {
            navController.navigate(Routes.CreateNewJournalThemeSelection)
        }
    )
}

@Composable
private fun RecordNewJournalInternal(
    modifier: Modifier = Modifier,
    capturedText: String,
    isRecording: Boolean,
    isRecordingPermissionGranted: Boolean,
    isRecordingPermissionPermanentlyDenied: Boolean,
    onToggleRecording: () -> Unit,
    onCreateManually: () -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isRecording) "Listening..." else "Speak your story...",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isRecording) "Tap to stop recording" else "Tap the microphone to start recording",
                )

                if (!isRecordingPermissionGranted) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = if (isRecordingPermissionPermanentlyDenied) "Audio recording permission was declined. Please enable it in app settings to use this feature." else "Audio recording permission is required to use this feature. Please grant the permission.",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.padding(24.dp))
                IconButton(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            shape = CircleShape,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    if (isRecording) MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.tertiaryContainer
                                )
                            )
                        ),
                    onClick = { onToggleRecording() }
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Outlined.MicOff else Icons.Outlined.Mic,
                        contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                        tint = if (isRecording) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                if (capturedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.padding(24.dp))

                    JournalContentPreview(
                        previewTitle = "Captured so far:",
                        content = capturedText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column {
                if (capturedText.isNotEmpty()) {
                    Button(
                        onClick = { onSave() },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.heroicons_sparkles),
                            contentDescription = "Enhance with AI"
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = "Enhance with AI",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                OutlinedButton(
                    onClick = { onCreateManually() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardAlt,
                        contentDescription = "Write Manually"
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "Create Manually",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordNewJournalScreenPreview() {
    RecordNewJournalInternal(
        capturedText = "This is captured text",
        isRecording = false,
        isRecordingPermissionGranted = true,
        isRecordingPermissionPermanentlyDenied = false,
        onToggleRecording = {},
        onSave = {},
        onCreateManually = {}
    )
}