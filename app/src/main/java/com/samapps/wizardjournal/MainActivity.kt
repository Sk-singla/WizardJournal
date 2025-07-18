package com.samapps.wizardjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.samapps.wizardjournal.ui.feed.FeedScreen
import com.samapps.wizardjournal.ui.feed.FeedViewModel
import com.samapps.wizardjournal.ui.theme.WizardJournalTheme
import com.samapps.wizardjournal.ui.wip_journal.WipJournal
import com.samapps.wizardjournal.ui.wip_journal.WipJournalViewModel
import kotlinx.serialization.Serializable

@Serializable
object FeedScreen

@Serializable
object CreateNewJournalScreen

@Serializable
data class EditJournalScreen(
  val journalId: String
)


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
          var feedViewModel = viewModel<FeedViewModel>()

          NavHost(
            navController = navController,
            startDestination = FeedScreen
          ) {
            composable<FeedScreen> {
              FeedScreen(navController = navController, viewModel = feedViewModel)
            }
            composable<CreateNewJournalScreen> {
              WipJournal(
                navController = navController,
                viewModel = viewModel(),
                saveJournal = { title, content ->
                  feedViewModel.createNewJournal(title, content)
                }
              )
            }
            composable<EditJournalScreen> {
              val args = it.toRoute<EditJournalScreen>()
              WipJournal(
                viewModel = viewModel(factory = object : ViewModelProvider.Factory {
                  override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WipJournalViewModel(
                      journal = feedViewModel.journals.find { it.id == args.journalId }
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