package com.empik.weather.ui.screens.city_weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.repository.WeatherRepository
import com.empik.weather.ui.screens.city_search.CitySearchError
import com.empik.weather.ui.screens.city_search.CitySearchErrorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CityWeatherViewModel(
    private val locationKey: String,
    private val weatherRepository: WeatherRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CityWeatherState())
    val state = _state.asStateFlow()

    init {
        getForecast(locationKey)
    }

    private fun getForecast(query: String) {
        viewModelScope.launch {
            weatherRepository.getForecast(locationKey).collect {
                when (it) {
                    is SafeResponse.Loading -> {
                        _state.value = _state.value.copy(
                            forecast = null,
                            isLoading = true,
                            error = null
                        ) }

                    is SafeResponse.Error -> {
                        _state.value = _state.value.copy(
                            forecast = null,
                            isLoading = false,
                            error = CitySearchError(
                                CitySearchErrorType.API_ERROR,
                                it.message
                            )
                        )
                    }

                    is SafeResponse.Success -> {
                        _state.value = _state.value.copy(
                            forecast = it.data,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
            }
        }
    }
}

data class CityWeatherState(
    val forecast: ForecastResponse? = null,
    val isLoading: Boolean = false,
    val error: CitySearchError? = null,
)