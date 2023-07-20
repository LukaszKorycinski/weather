package com.empik.weather.di

import com.empik.weather.data.repository.LocalRepository
import com.empik.weather.data.repository.WeatherRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { WeatherRepository(get()) }
    single { LocalRepository(get()) }
}