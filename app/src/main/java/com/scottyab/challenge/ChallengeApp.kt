package com.scottyab.challenge

import android.app.Application
import com.scottyab.challenge.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class ChallengeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initDI()
    }

    private fun initLogger() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initDI() {
        startKoin {
            androidLogger()
            androidContext(this@ChallengeApp)
            modules(allModules)
        }
    }
}
