package com.samapps.wizardjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.samapps.wizardjournal.app.Routes
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.ManualJournalEditorScreen
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.manual_editor.RecordNewJournalScreen
import com.samapps.wizardjournal.feature_journal.presentation.journal_home.JournalHomeScreen
import com.samapps.wizardjournal.ui.feed.FeedViewModel
import com.samapps.wizardjournal.ui.theme.WizardJournalTheme
import com.samapps.wizardjournal.ui.wip_journal.WipJournal
import com.samapps.wizardjournal.ui.wip_journal.WipJournalViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

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
          var feedViewModel = getViewModel<FeedViewModel>()
          val journals by feedViewModel.journals.collectAsState(emptyList())


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
              composable<Routes.EditJournal> {
                val args = it.toRoute<Routes.EditJournal>()
                WipJournal(
                  viewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                      return WipJournalViewModel(
                        journal = journals.find { it.id == args.journalId }
                      ) as T
                    }
                  }
                  ),
                  navController = navController,
                  saveJournal = { title, content ->
                    feedViewModel.editJournal(args.journalId, title, content)
                  }
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
