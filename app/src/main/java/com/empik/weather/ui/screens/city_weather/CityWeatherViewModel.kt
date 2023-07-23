package com.empik.weather.ui.screens.city_weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.repository.WeatherRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CityWeatherViewModel(
    private val locationKey: String,
    private val weatherRepository: WeatherRepositoryInterface,
) : ViewModel() {
    private val _state = MutableStateFlow<CityWeatherState>(CityWeatherState.LOADING)
    val state = _state.asStateFlow()

    init {
        getForecast()
    }

    fun getForecast() {
        viewModelScope.launch {
            weatherRepository.getForecast(locationKey).collect {
                _state.value = when (it) {
                    is SafeResponse.Loading -> CityWeatherState.LOADING
                    is SafeResponse.Error -> CityWeatherState.ERROR(true, it.message  ?: it.throwable?.localizedMessage)
                    is SafeResponse.Success -> CityWeatherState.CONTENT(it.data)
                }
            }
        }
    }
}

sealed interface CityWeatherState {
    class CONTENT(val forecast: ForecastResponse? = null) : CityWeatherState
    object LOADING : CityWeatherState
    class ERROR(val isError: Boolean = true, val message: String? = null) : CityWeatherState
}