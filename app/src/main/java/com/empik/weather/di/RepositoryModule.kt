package com.empik.weather.di

import com.empik.weather.repo.WeatherRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { WeatherRepository(get()) }
}