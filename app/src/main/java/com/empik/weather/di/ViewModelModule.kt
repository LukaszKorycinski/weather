package com.empik.weather.di

import com.empik.weather.ui.screens.city_search.CitySearchViewModel
import com.empik.weather.ui.screens.city_weather.CityWeatherViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { CitySearchViewModel(get()) }
    factory { CityWeatherViewModel(get()) }
}