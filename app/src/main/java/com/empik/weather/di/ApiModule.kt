package com.empik.weather.di

import com.empik.weather.data.api.clients.WeatherApiClient
import org.koin.dsl.module

internal val apiModule = module {
    single { retrofitService<WeatherApiClient>(get()) }
}