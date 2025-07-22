package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import com.samapps.wizardjournal.app.utils.rememberSpeechRecognizer
import kotlinx.coroutines.launch

@Composable
fun RecordNewJournalScreen(
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var capturedText by remember { mutableStateOf("") }
    val speechRecognizer = rememberSpeechRecognizer(
        onResult = { text ->
            capturedText += if (capturedText.isEmpty()) text else " $text"
        },
        onError = { error ->
            scope.launch {
                snackbarHostState.showSnackbar("Error: $error")
            }
        }
    )
    val recordAudioPermissionState = speechRecognizer.recordAudioPermissionState


    fun handleToggleRecording() {
        if (speechRecognizer.isRecording) {
            speechRecognizer.stopListening()
        } else {
            speechRecognizer.startListening()
        }
    }

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
                    text = if (speechRecognizer.isRecording) "Listening..." else "Speak your story...",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (speechRecognizer.isRecording) "Tap to stop recording" else "Tap the microphone to start recording",
                )

                if (!recordAudioPermissionState.isGranted) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = if (recordAudioPermissionState.isPermanentlyDenied) "Audio recording permission was declined. Please enable it in app settings to use this feature." else "Audio recording permission is required to use this feature. Please grant the permission.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
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
                                    if (speechRecognizer.isRecording) MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.tertiaryContainer
                                )
                            )
                        ),
                    onClick = { handleToggleRecording() }
                ) {
                    Icon(
                        imageVector = if (speechRecognizer.isRecording) Icons.Outlined.MicOff else Icons.Outlined.Mic,
                        contentDescription = if (speechRecognizer.isRecording) "Stop Recording" else "Start Recording",
                        tint = if (speechRecognizer.isRecording) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                if (capturedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.padding(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .height(IntrinsicSize.Min)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Captured so far:",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = capturedText,
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = {

                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardAlt,
                    contentDescription = "Write Manually"
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "Write Manually Instead",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordNewJournalScreenPreview() {
    RecordNewJournalScreen()
}