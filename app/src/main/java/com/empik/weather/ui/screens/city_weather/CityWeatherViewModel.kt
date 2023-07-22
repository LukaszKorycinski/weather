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
    private val _state = MutableStateFlow(CityWeatherState())
    val state = _state.asStateFlow()

    init {
        getForecast()
    }

    fun getForecast() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                forecast = null,
                isLoading = true,
                error = CityWeatherError(false),
            )
            weatherRepository.getForecast(locationKey).collect {
                when (it) {
                    is SafeResponse.Loading -> {
                        _state.value = _state.value.copy(
                            forecast = null,
                            isLoading = true,
                            error = CityWeatherError(false),
                        )
                    }

                    is SafeResponse.Error -> {
                        _state.value = _state.value.copy(
                            forecast = null,
                            isLoading = false,
                            error = CityWeatherError(true, it.message  ?: it.throwable?.localizedMessage),
                        )
                    }

                    is SafeResponse.Success -> {
                        _state.value = _state.value.copy(
                            forecast = it.data,
                            isLoading = false,
                            error = CityWeatherError(false),
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
    val error: CityWeatherError = CityWeatherError(false),
)

data class CityWeatherError(val isError: Boolean = true, val message: String? = null)