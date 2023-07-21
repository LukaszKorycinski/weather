package com.empik.weather

import android.app.Application
import com.empik.weather.di.TimberKoinLogger
import com.empik.weather.di.apiModule
import com.empik.weather.di.appModule
import com.empik.weather.di.networkModule
import com.empik.weather.di.repositoryModule
import com.empik.weather.di.repositoryModuleMocked
import com.empik.weather.di.viewModelModule
import com.empik.weather.helper.testMode
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber


class App: Application() {

    companion object {
        lateinit var appContext: App
            private set
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
        AndroidThreeTen.init(this);
        initTimber()
        initKoin()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            logger(
                TimberKoinLogger(
                    if (BuildConfig.DEBUG) Level.DEBUG
                    else Level.ERROR
                )
            )
            modules(
                apiModule,
                appModule,
                networkModule,
                if(testMode) repositoryModuleMocked else repositoryModule,
                viewModelModule
            )
        }
    }
}