package com.samapps.wizardjournal.ui.wip_journal

import android.content.ContentResolver
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.samapps.wizardjournal.BuildConfig
import com.samapps.wizardjournal.UiState
import com.samapps.wizardjournal.model.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.io.File

class WipJournalViewModel(journal: JournalEntry? = null): ViewModel() {
    val isNewJournal = journal == null

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    var title by mutableStateOf(journal?.title ?: "")
    var content by mutableStateOf(journal?.content ?: "")

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(
        contentResolver: ContentResolver,
        audioFile: File
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                contentResolver.openInputStream(audioFile.toUri()).use { stream ->
                    stream?.let {
                        generativeModel.generateContentStream(
                            content {
                                blob("audio/mpeg", stream.readBytes())
                                text("You are a creative writing assistant.\n" +
                                        "\n" +
                                        "I’m providing an audio file that contains a personal journal entry recorded by the user. Your task is to:\n" +
                                        "\t1.\tTranscribe the audio to text.\n" +
                                        "\t2.\tReimagine the content as a fictional short story set in the Harry Potter universe.\n" +
                                        "\n" +
                                        "When rewriting, please:\n" +
                                        "\t•\tUse the tone, setting, and atmosphere of J.K. Rowling’s world (Hogwarts, Diagon Alley, magical creatures, etc.).\n" +
                                        "\t•\tTransform real-world concepts (like “school”, “boss”, or “friends”) into magical equivalents (like “Hogwarts”, “Ministry of Magic”, “Aurors”, etc.).\n" +
                                        "\t•\tKeep the core message and emotional tone of the journal.\n" +
                                        "\t•\tFeel free to introduce magical characters, events, or locations that fit the theme.\n" +
                                        "Your response should be just the ")
                            }
                        ).onCompletion { errorCause ->
                            println("==== RESPONSE COMPLETED ====")
                            println("Error cause: $errorCause")
                            if (errorCause != null) {
                                _uiState.value = UiState.Error(errorCause.localizedMessage ?: "")
                            }
                            else {
                                _uiState.value = UiState.Success(content)
                            }
                        }.collect { chunk ->
                            println("CHUNK: $chunk")
                            content += chunk.text
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

}