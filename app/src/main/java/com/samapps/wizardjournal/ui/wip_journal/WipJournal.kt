package com.samapps.wizardjournal.ui.wip_journal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.FeedScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WipJournal(
    modifier: Modifier = Modifier,
    viewModel: WipJournalViewModel,
    navController: NavHostController,
    saveJournal: (String, String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var showRecordingModal by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var transcribedText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showRecordingModal = true
            startRecording(
                context,
                speechRecognizer,
                { isRecording = it },
                { transcribedText = it },
                onFinalResults = { finalResult ->
                    viewModel.content += " $finalResult" // Append final result to existing content
                    transcribedText = "" // Clear transcribed text after processing
                    showRecordingModal = false // Hide modal after recording finishes
                }
            )
        } else {
            // Handle permission denial
            // You might want to show a Snackbar or a dialog
        }
    }

    fun handleSaveJournal(title: String, content: String) {
        saveJournal(title, content)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(
                    text = if (viewModel.isNewJournal)  "Create New Journal" else "Edit Journal"
                ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Save Journal",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                navController.navigate(FeedScreen) {
                                    popUpTo(FeedScreen) {
                                        inclusive = true
                                    }
                                }
                            }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Start Recording"
                )
            }
        }


    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                placeholder = { Text("Title", style = MaterialTheme.typography.headlineSmall) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = viewModel.content,
                onValueChange = { viewModel.content = it },
                placeholder = { Text("Journal", style = MaterialTheme.typography.bodyLarge) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showRecordingModal) {
            Dialog(onDismissRequest = {
                showRecordingModal = false
                // Optionally stop recording if the dialog is dismissed externally
                if (isRecording) {
                    stopRecording(speechRecognizer)
                    isRecording = false
                }
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "recording_animation")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ), label = "recording_alpha"
                    )
                    Icon(Icons.Filled.Call, contentDescription = "Recording", modifier = Modifier.size(48.dp).alpha(alpha))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(if (transcribedText.isNotBlank()) transcribedText else "Listening...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                // Only save if not currently recording to avoid interrupting active recording flow
                if (!isRecording && (viewModel.title.isNotBlank() || viewModel.content.isNotBlank())) {
                    handleSaveJournal(viewModel.title, viewModel.content)
                }
            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                speechRecognizer.destroy()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // It's good practice to destroy the SpeechRecognizer here too,
            // though ON_DESTROY should cover it.
            speechRecognizer.destroy()
        }
    }

    LaunchedEffect(showRecordingModal, isRecording) {
        if (!showRecordingModal && isRecording) {
            stopRecording(speechRecognizer)
            isRecording = false
        }
    }
}

private fun startRecording(
    context: Context,
    speechRecognizer: SpeechRecognizer,
    onRecordingStateChange: (Boolean) -> Unit,
    onPartialResults: (String) -> Unit, // Callback for partial results
    onFinalResults: (String) -> Unit // Callback for final results
) {
    if (SpeechRecognizer.isRecognitionAvailable(context)) {
        println("Speech recognition is available. Initializing.")
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.current.language)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Enable partial results
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...") // Optional: text prompt for user
        }
        println("RecognizerIntent created with language: ${Locale.current.language}")

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                println("onReadyForSpeech: Recording started")
                onRecordingStateChange(true)
            }

            override fun onBeginningOfSpeech() {
                println("onBeginningOfSpeech: User started speaking")
                // Called when the user starts speaking
            }

            override fun onRmsChanged(rmsdB: Float) {
                println("onRmsChanged: Sound level changed to $rmsdB dB")
                // Called when the sound level in the audio stream changes
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                println("onBufferReceived: Partial recognition results available")
                // Called when partial recognition results are available
            }

            override fun onEndOfSpeech() {
                println("onEndOfSpeech: User stopped speaking")
                onRecordingStateChange(false)
            }

            override fun onError(error: Int) {
                onRecordingStateChange(false)
                // Handle errors, e.g., display a message to the user
                // You might want to map error codes to user-friendly messages
                println("onError: Speech recognition error code: $error")
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_SERVER -> "Error from server"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown speech recognition error"
                }
                // Log or display errorMessage
                println("onError: Error message: $errorMessage")
                onFinalResults("") // Clear any previous results on error
            }

            override fun onResults(results: Bundle?) {
                println("onResults: Final recognition results received.")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onFinalResults(matches[0]) // Return the most likely result
                } else {
                    onFinalResults("")
                }
                onRecordingStateChange(false)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                println("onPartialResults: Partial recognition results received.")
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onPartialResults(matches[0]) //  Return the current partial result
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                println("onEvent: Event type: $eventType")
                // Reserved for future use
            }
        })
        speechRecognizer.startListening(recognizerIntent)
        println("Speech recognizer started listening.")
    } else {
        // Speech recognition not available on this device
        // You might want to show a message to the user
        println("Speech recognition not available on this device.")
        onRecordingStateChange(false)
    }
}

private fun stopRecording(speechRecognizer: SpeechRecognizer) {
    println("stopRecording: Stopping speech recognizer.")
    speechRecognizer.stopListening()
    println("Speech recognizer stopped listening.")
}