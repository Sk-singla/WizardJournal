package com.samapps.wizardjournal.di

import androidx.room.Room
import com.samapps.wizardjournal.feature_auth.data.datasource.remote.AuthApiService
import com.samapps.wizardjournal.feature_auth.data.repository.AuthRepositoryImpl
import com.samapps.wizardjournal.feature_auth.domain.repository.AuthRepository
import com.samapps.wizardjournal.feature_auth.utils.TokenDataStore
import com.samapps.wizardjournal.feature_auth.domain.use_case.AuthUseCases
import com.samapps.wizardjournal.feature_auth.domain.use_case.LoginUseCase
import com.samapps.wizardjournal.feature_auth.domain.use_case.SignupUseCase
import com.samapps.wizardjournal.feature_auth.presentation.login.LoginViewModel
import com.samapps.wizardjournal.feature_auth.presentation.signup.SignupViewModel
import com.samapps.wizardjournal.feature_journal.data.data_source.JournalDatabase
import com.samapps.wizardjournal.feature_journal.data.repository.JournalRepositoryImpl
import com.samapps.wizardjournal.feature_journal.domain.repository.JournalRepository
import com.samapps.wizardjournal.feature_journal.domain.use_case.CreateJournalUseCase
import com.samapps.wizardjournal.feature_journal.domain.use_case.DeleteJournalUseCase
import com.samapps.wizardjournal.feature_journal.domain.use_case.GetJournalByIdUseCase
import com.samapps.wizardjournal.feature_journal.domain.use_case.GetJournalsUseCase
import com.samapps.wizardjournal.feature_journal.domain.use_case.JournalUseCases
import com.samapps.wizardjournal.feature_journal.presentation.journal_details.JournalDetailsViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_editor.JournalEditorViewModel
import com.samapps.wizardjournal.feature_journal.presentation.journal_home.JournalHomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            JournalDatabase::class.java,
            JournalDatabase.DATABASE_NAME
        ).build()
    }
    single {
        get<JournalDatabase>().journalDao
    }
    single<JournalRepository> {
        JournalRepositoryImpl(get())
    }
    single {
        JournalUseCases(
            getJournals = GetJournalsUseCase(get()),
            deleteJournal = DeleteJournalUseCase(get()),
            createJournal = CreateJournalUseCase(get()),
            getJournalById = GetJournalByIdUseCase(get())
        )
    }

    single {
        TokenDataStore(get())
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            get<AuthApiService>(),
            get<TokenDataStore>(),
        )
    }
    single { AuthUseCases(
        login = LoginUseCase(get()),
        signup = SignupUseCase(get())
    ) }
    viewModelOf(::JournalHomeViewModel)
    viewModelOf(::JournalEditorViewModel)
    viewModel { (journalId: Int) ->
        JournalDetailsViewModel(get(), journalId)
    }
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignupViewModel)
}