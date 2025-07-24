package com.samapps.wizardjournal.feature_journal.presentation.journal_editor.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.samapps.wizardjournal.R
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.feature_journal.domain.model.JournalTheme
import com.samapps.wizardjournal.feature_journal.presentation.components.CustomTopAppBar
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.components.JournalContentPreview
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.components.ThemeSelection
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.events.ThemeSelectionEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ThemeSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: JournalEditorViewModel,
    navController: NavHostController
) {

    val journalContent by viewModel.journalContent.collectAsState()
    val journalTheme by viewModel.journalTheme.collectAsState()
    val journalGenerationInProgress by viewModel.isGenerating.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.themeSelectionEvents.collectLatest { event ->
            when (event) {
                is JournalEditorViewModel.ThemeSelectionScreenUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is JournalEditorViewModel.ThemeSelectionScreenUiEvent.JournalSaved -> {
                    navController.popBackStack(
                        route = Routes.JournalHome::class,
                        inclusive = false
                    )
                }
            }
        }
    }

    BackHandler (enabled = journalGenerationInProgress) {
        // Do nothing
    }

    if (journalGenerationInProgress) {
        LoadingScreen()
    }
    else {
        ThemeSelectionScreenInternal(
            modifier = modifier,
            snackbarHostState = snackbarHostState,
            journalContent = journalContent,
            journalTheme = journalTheme,
            onEvent = { event ->
                viewModel.onThemeSelectionEvents(event)
            },
            onSave = {
                viewModel.onThemeSelectionEvents(ThemeSelectionEvent.GenerateJournal)
            }
        )
    }
}

@Composable
private fun ThemeSelectionScreenInternal(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    journalContent: String,
    journalTheme: JournalTheme,
    onEvent: (event: ThemeSelectionEvent) -> Unit,
    onSave: () -> Unit
) {

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomTopAppBar(
                title = "Choose Your Theme"
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
                .padding(16.dp)
        ) {
            JournalContentPreview(
                previewTitle = "Your Journal Entry",
                content = journalContent,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ThemeSelection(
                value = journalTheme,
                onValueChange = { onEvent(ThemeSelectionEvent.ThemeSelection(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onSave()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.heroicons_sparkles),
                    contentDescription = "Generate Journal!"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Generate Journal!")
            }
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    message: String = "Brewing your magical journal..."
) {
    val dotCount = 3
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotAnim = List(dotCount) { idx ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.3f at (idx * 200)
                    1f at (400 + idx * 200)
                    0.3f at (800 + idx * 200)
                },
                repeatMode = RepeatMode.Restart
            ), label = "dot$idx"
        )
    }

    Box(
        modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Gradient Circle with Sparkle Icon in center
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.90f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                            ),
                            center = Offset(44f, 44f), radius = 44f
                        ),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.heroicons_sparkles),
                    contentDescription = "Sparkle Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(36.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Animated loading dots
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(dotCount) { i ->
                    Box(
                        Modifier
                            .padding(horizontal = 4.dp)
                            .size(10.dp)
                            .graphicsLayer { alpha = dotAnim[i].value }
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun ThemeSelectionScreenPreview() {
    ThemeSelectionScreenInternal(
        modifier = Modifier,
        journalContent = "This is a preview",
        journalTheme = JournalTheme.FantasyAdventure,
        onEvent = {},
        onSave = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}