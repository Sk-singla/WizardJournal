package com.samapps.wizardjournal.app.utils

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.util.Locale

/**
 * Composable function to provide speech recognition capabilities.
 *
 * @param onResult Callback function invoked when speech recognition results are available.
 * @param onError Callback function invoked when an error occurs during speech recognition.
 * @param onEndOfSpeech Callback function invoked when the end of speech is detected.
 * @return [SpeechRecognizerActions] object containing functions to start and stop listening.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberSpeechRecognizer(
    onResult: (String) -> Unit,
    onError: (Int) -> Unit = {},
    onEndOfSpeech: () -> Unit = {}
): SpeechRecognizerActions {
    val context = LocalContext.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val isRecordingState = remember { mutableStateOf(false) }
    val recordAudioPermissionState = rememberCustomPermissionState(
        Manifest.permission.RECORD_AUDIO,
    )

    DisposableEffect(speechRecognizer) {
        onDispose {
            speechRecognizer.destroy()
        }
    }

    LaunchedEffect(speechRecognizer) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isRecordingState.value = false
                onEndOfSpeech()
            }
            override fun onError(error: Int) {
                isRecordingState.value = false
                onError(error)
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult(matches[0])
                }
                isRecordingState.value = false
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    val startListening = {
        if (recordAudioPermissionState.isPermanentlyDenied) {
            recordAudioPermissionState.openAppSettings()
        } else if (!recordAudioPermissionState.isGranted) {
            recordAudioPermissionState.requestPermission()
        } else {
            isRecordingState.value = true
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            speechRecognizer.startListening(intent)
        }
    }

    val stopListening = {
        speechRecognizer.stopListening()
        isRecordingState.value = false
    }

    return SpeechRecognizerActions(
        startListening = startListening,
        stopListening = stopListening,
        isRecording = isRecordingState.value,
        recordAudioPermissionState = recordAudioPermissionState
    )
}

/**
 * Data class to hold actions for controlling the speech recognizer.
 *
 * @property startListening Function to start speech recognition.
 * @property stopListening Function to stop speech recognition.
 * @property isRecording Boolean indicating whether the recognizer is currently recording.
 */
data class SpeechRecognizerActions(
    val startListening: () -> Unit,
    val stopListening: () -> Unit,
    val isRecording: Boolean,
    val recordAudioPermissionState: PermissionHandler
)