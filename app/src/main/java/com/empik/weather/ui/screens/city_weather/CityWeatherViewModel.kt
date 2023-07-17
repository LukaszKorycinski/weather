package com.empik.weather.ui.screens.city_weather

import com.empik.weather.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CityWeatherViewModel(
    private val weatherRepository: WeatherRepository,
) {
    private val _state = MutableStateFlow(CityWeatherState())
    val state = _state.asStateFlow()

}

data class CityWeatherState(
    val weather: String = "",
)