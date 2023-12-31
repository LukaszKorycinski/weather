package com.empik.weather.di

import com.empik.weather.data.repository.LocalRepository
import com.empik.weather.data.repository.WeatherRepository
import com.empik.weather.data.repository.WeatherRepositoryInterface
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single<WeatherRepositoryInterface> { WeatherRepository(get()) } bind WeatherRepositoryInterface::class
    single { LocalRepository(get()) }
}