package com.samapps.wizardjournal.ui.wip_journal

import android.content.ContentResolver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.samapps.wizardjournal.BuildConfig
import com.samapps.wizardjournal.UiState
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.model.AiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File

class WipJournalViewModel(journal: JournalEntity? = null): ViewModel() {
    val isNewJournal = journal == null

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    var title by mutableStateOf(journal?.title ?: "")
    var content by mutableStateOf(journal?.content ?: "")

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = Schema.obj(
                "response",
                "json response",
                Schema.str("transcription", "transcription"),
                Schema.str("journalTitle", "title of journal story"),
                Schema.str("journalStory", "story")
            )
        }
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
                        var response = generativeModel.generateContent(
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
                                        "Your response should be JSON. It should include the following fields: \n" +
                                        "\t•\t“transcription”: A string containing the transcribed audio.\n" +
                                        "\t•\t“title”: A string containing a suitable title for the reimagined story (max 10 words).\n" +
                                        "\t•\t“story”: A string containing the reimagined story in the Harry Potter universe." +
                                        "\n\nIMP: Response should be json object with properties: {transcript, journalTitle, journalStory}"
                                )
                            }
                        )

                        if(response.text == null){
                            _uiState.value = UiState.Error("No response from server")
                        } else {
                            val serializedJson = response.text.toString()
                            val obj = Json.decodeFromString<AiResponse>(serializedJson)
                            title = obj.journalTitle
                            content = obj.journalStory
                            _uiState.value = UiState.Success(obj.journalStory)
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