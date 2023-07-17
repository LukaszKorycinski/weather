package com.empik.weather.ui.screens.city_search

import com.empik.weather.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CitySearchViewModel(
    private val weatherRepository: WeatherRepository,
) {
    private val _state = MutableStateFlow(CitySearchScreenState())
    val state = _state.asStateFlow()

}

data class CitySearchScreenState(
    val city: String = "",
)