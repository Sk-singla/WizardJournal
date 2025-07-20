package com.samapps.wizardjournal

import android.app.Application
import com.samapps.wizardjournal.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WizardJournalApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WizardJournalApplication)
            modules(appModule)
        }
    }
}