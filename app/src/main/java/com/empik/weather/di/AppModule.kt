package com.empik.weather.di

import com.empik.weather.data.data_store.DataStore
import org.koin.dsl.module

internal val appModule = module {
    single { DataStore(get(), get()) }
}