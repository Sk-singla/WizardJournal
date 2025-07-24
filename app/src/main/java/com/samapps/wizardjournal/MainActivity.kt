package com.samapps.wizardjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.feature_journal.presentation.journal_details.JournalDetails
import com.samapps.wizardjournal.feature_journal.presentation.journal_details.JournalDetailsViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.screens.ManualJournalEditorScreen
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.screens.RecordNewJournalScreen
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.screens.ThemeSelectionScreen
import com.samapps.wizardjournal.feature_journal.presentation.journal_home.JournalHomeScreen
import com.samapps.wizardjournal.ui.theme.WizardJournalTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      WizardJournalTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {

          val navController = rememberNavController()

          NavHost(
            navController = navController,
            startDestination = Routes.Root
          ) {
            navigation<Routes.Root>(startDestination = Routes.JournalHome) {
              composable<Routes.JournalHome> {
                JournalHomeScreen(navController = navController)
              }
              composable<Routes.CreateNewJournalByRecording> {
                val editorViewModel = it.sharedKoinViewModel<JournalEditorViewModel>(navController)
                editorViewModel.setJournalIdToEdit(null)

                RecordNewJournalScreen(
                  navController = navController,
                  viewModel = editorViewModel
                )
              }
              composable<Routes.CreateNewJournalManualEditing> {
                val editorViewModel = it.sharedKoinViewModel<JournalEditorViewModel>(navController)
                editorViewModel.setJournalIdToEdit(null)

                ManualJournalEditorScreen(
                  navController = navController,
                  viewModel = editorViewModel
                )
              }

              composable<Routes.CreateNewJournalThemeSelection> {
                val editorViewModel = it.sharedKoinViewModel<JournalEditorViewModel>(navController)
                editorViewModel.setJournalIdToEdit(null)

                ThemeSelectionScreen(
                  viewModel = editorViewModel,
                  navController = navController
                )
              }

              composable<Routes.ViewJournal> {
                val args = it.toRoute<Routes.ViewJournal>()
                val viewModel = koinViewModel<JournalDetailsViewModel>{ parametersOf(args.journalId) }
                JournalDetails(
                  navController = navController,
                  viewModel = viewModel
                )
              }

              composable<Routes.EditJournal> {
                val args = it.toRoute<Routes.EditJournal>()
                val editorViewModel = it.sharedKoinViewModel<JournalEditorViewModel>(navController)
                editorViewModel.setJournalIdToEdit(args.journalId)

                ManualJournalEditorScreen(
                  navController = navController,
                  viewModel = editorViewModel
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedKoinViewModel(
  navController: NavHostController
) : T {
  val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
  val parentEntry = remember(this) {
    navController.getBackStackEntry(navGraphRoute)
  }
  return koinViewModel(viewModelStoreOwner = parentEntry)
}
