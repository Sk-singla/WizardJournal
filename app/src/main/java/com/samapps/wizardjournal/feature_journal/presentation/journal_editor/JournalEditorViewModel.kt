package com.samapps.wizardjournal.feature_journal.presentation.journal_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.samapps.wizardjournal.BuildConfig
import com.samapps.wizardjournal.feature_journal.domain.model.AiJournalResponse
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundInfo
import com.samapps.wizardjournal.feature_journal.domain.model.BackgroundType
import com.samapps.wizardjournal.feature_journal.domain.model.InvalidJournalException
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.events.ManualEditorEvent
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.events.RecordNewJournalEvent
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.events.ThemeSelectionEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class JournalEditorViewModel(
    private val journalUseCases: JournalUseCases,
): ViewModel() {

    private val _journalTitle = MutableStateFlow("")
    val journalTitle = _journalTitle.asStateFlow()

    private val _journalContent = MutableStateFlow("")
    val journalContent = _journalContent.asStateFlow()

    private val _journalTheme = MutableStateFlow<JournalTheme>(JournalTheme.HarryPotter)
    val journalTheme = _journalTheme.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _themeSelectionEvents = MutableSharedFlow<ThemeSelectionScreenUiEvent>()
    val themeSelectionEvents = _themeSelectionEvents.asSharedFlow()

    private val _manualEditorEvents = MutableSharedFlow<ManualJournalEditorScreenUiEvent>()
    val manualEditorEvents = _manualEditorEvents.asSharedFlow()


    private var journalId: Int? = null
    private var journal: JournalEntity? = null
    val isEditing: Boolean
        get() = journalId != null

    fun setJournalIdToEdit(id: Int?) {
        journalId = id
        if (id == null) {
            return
        }

        viewModelScope.launch {
            journal = journalUseCases.getJournalById(id)
            _journalTitle.update {
                journal?.title ?: ""
            }
            _journalContent.update {
                journal?.content ?: ""
            }
        }
    }

    fun clearStatesOnJournalUpdate(){
        _journalTitle.update { "" }
        _journalContent.update { "" }
        _journalTheme.update { JournalTheme.HarryPotter }
        journalId = null
        journal = null
    }

    fun onManualEditorEvent(event: ManualEditorEvent) {
        when (event) {
            is ManualEditorEvent.TitleChanged -> {
                _journalTitle.update {
                    event.title
                }
            }

            is ManualEditorEvent.ContentChanged -> {
                _journalContent.update {
                    event.content
                }
            }

            is ManualEditorEvent.Save -> {
                viewModelScope.launch {
                    try {
                        if(isEditing) {
                            journalUseCases.createJournal(
                                journal!!.copy(
                                    title = _journalTitle.value,
                                    content = _journalContent.value,
                                    modifiedTimestamp = System.currentTimeMillis()
                                )
                            )
                        }
                        else {
                            journalUseCases.createJournal(
                                JournalEntity(
                                    title = _journalTitle.value,
                                    content = _journalContent.value,
                                    date = System.currentTimeMillis(),
                                    modifiedTimestamp = System.currentTimeMillis(),
                                    theme = JournalTheme.None,
                                    aiPromptUsed = null,
                                    audioFilePath = null,
                                    backgroundInfo = BackgroundInfo(
                                        type = BackgroundType.SOLID_COLOR,
                                        primaryColor = null,
                                        secondaryColor = null,
                                        patternKey = null,
                                        gradientAngle = null
                                    )
                                )
                            )
                        }
                        clearStatesOnJournalUpdate()
                        _manualEditorEvents.emit(ManualJournalEditorScreenUiEvent.JournalSaved)
                    }
                    catch (e: InvalidJournalException) {
                        _manualEditorEvents.emit(
                            ManualJournalEditorScreenUiEvent.ShowSnackbar(
                                message = e.message ?: "Unknown error"
                            )
                        )
                    }
                }
            }
        }
    }

    fun onRecordNewJournalEvent(event: RecordNewJournalEvent) {
        when (event) {
            is RecordNewJournalEvent.CaptureText -> {
                _journalContent.update {
                    if (it.isEmpty()) {
                        event.content
                    } else {
                        it + " " + event.content
                    }
                }
            }
        }
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = Schema.obj(
                "response",
                "json response",
                Schema.str("title", "title of journal story"),
                Schema.str("content", "rewritten journal story"),
                Schema.obj(
                    "backgroundInfo", "background info",
                    Schema.enum("type", "background type", BackgroundType.entries.map { it.toString() }),
                    Schema.int("primaryColor", "hex value"),
                    Schema.int("secondaryColor", "hex value, nullable"),
                    Schema.double("gradientAngle", "angle, nullable")
                )
            )
        }
    )

    private fun buildPrompt(userInput: String, theme: JournalTheme): String = """
        You are a creative writing assistant.
    
        I'm providing a personal journal entry text written by the user. Your task is to:
            1. Briefly paraphrase the user's input to capture its essence.
            2. Reimagine the entry as a short fictional story inspired by the "${theme.displayName}" theme.
    
        Rewrite Guidelines:
            - Use elements from the world of ${theme.displayName}. ${theme.description}
            - Map real-world elements to equivalents in this theme.
            - Keep the journal's core message and emotion.
            - Suggest a matching journal background: type and color(s).
    
        Respond ONLY in JSON in this format:
        {"title":"...","content":"...","backgroundInfo":{"type":"...","primaryColor":...,"secondaryColor":...,"gradientAngle":...}}
    
        User's journal entry text:
        ---
        $userInput
        ---
    """.trimIndent()

    suspend fun generateJournal(
        userInput: String,
        theme: JournalTheme
    ): Result<AiJournalResponse> {
        val prompt = buildPrompt(userInput, theme)
        val response = generativeModel.generateContent(
            content { text(prompt) }
        )
        if (response.text == null) {
            return Result.failure(Exception("No response from server"))
        }
        return try {
            val obj = Json.decodeFromString<AiJournalResponse>(response.text.toString())
            Result.success(obj)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun onThemeSelectionEvents(event: ThemeSelectionEvent) {
        when (event) {
            is ThemeSelectionEvent.ThemeSelection  -> {
                _journalTheme.update {
                    event.theme
                }
            }
            is ThemeSelectionEvent.GenerateJournal -> {
                viewModelScope.launch {
                    _isGenerating.value = true
                    val result = generateJournal(
                        userInput = _journalContent.value,
                        theme = _journalTheme.value
                    )
                    result.fold(
                        onSuccess = { aiResponse ->
                            journalUseCases.createJournal(
                                JournalEntity(
                                    title = aiResponse.title,
                                    content = aiResponse.content,
                                    date = System.currentTimeMillis(),
                                    modifiedTimestamp = System.currentTimeMillis(),
                                    theme = _journalTheme.value,
                                    aiPromptUsed = _journalContent.value,
                                    audioFilePath = null,
                                    backgroundInfo = aiResponse.backgroundInfo
                                )
                            )
                            clearStatesOnJournalUpdate()
                            _themeSelectionEvents.emit(ThemeSelectionScreenUiEvent.JournalSaved)
                            _isGenerating.value = false
                        },
                        onFailure = { exception ->
                            _themeSelectionEvents.emit(
                                ThemeSelectionScreenUiEvent.ShowSnackbar(
                                    message = exception.message ?: "Unknown error"
                                )
                            )
                            _isGenerating.value = false
                        }
                    )
                }
            }
        }
    }

    sealed class ThemeSelectionScreenUiEvent {
        data class ShowSnackbar(val message: String): ThemeSelectionScreenUiEvent()
        data object JournalSaved: ThemeSelectionScreenUiEvent()
    }

    sealed class ManualJournalEditorScreenUiEvent {
        data class ShowSnackbar(val message: String): ManualJournalEditorScreenUiEvent()
        data object JournalSaved: ManualJournalEditorScreenUiEvent()
    }
}