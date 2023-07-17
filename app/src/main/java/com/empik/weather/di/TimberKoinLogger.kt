package com.empik.weather.di

import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import timber.log.Timber

class TimberKoinLogger(level: Level) : Logger(level) {
    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> Timber.d(msg)
            Level.ERROR -> Timber.e(msg)
            Level.INFO -> Timber.i(msg)
            else -> Timber.e(msg)
        }
    }
}