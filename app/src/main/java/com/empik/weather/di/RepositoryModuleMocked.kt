package com.empik.weather.di

import com.empik.weather.data.repository.LocalRepository
import com.empik.weather.data.repository.WeatherRepositoryInterface
import com.empik.weather.data.repository.WeatherRepositoryMocked
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModuleMocked = module {
    single<WeatherRepositoryInterface> { WeatherRepositoryMocked() } bind WeatherRepositoryInterface::class
    single { LocalRepository(get()) }
}