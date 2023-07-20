package com.empik.weather.ui.screens.city_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.CityAutocompleteResponseItem
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.repository.LocalRepository
import com.empik.weather.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CitySearchViewModel(
    private val weatherRepository: WeatherRepository,
    private val localRepository: LocalRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CitySearchScreenState())
    val state = _state.asStateFlow()

    companion object {
        private val cityRegex = "^([a-zA-Z\\u0080-\\u024F]+(?:. |-| |'))*[a-zA-Z\\u0080-\\u024F]*\$".toRegex()
    }

    init {
        getSavedCities()
    }

    private fun getSavedCities(){
        _state.value = _state.value.copy(
            savedCities = localRepository.cities,
        )
    }

    fun saveCity(city: CityResponseItem){
        viewModelScope.launch {
            localRepository.saveCity(city)
            getSavedCities()
        }
    }

    fun validateCityName(cityName: String): Boolean {
        return cityRegex.matches(cityName)
    }

    fun getCityByName(cityName: String){
        if (validateCityName(cityName)) {
            viewModelScope.launch {
                weatherRepository.getCities(cityName).collect {
                    when (it) {
                        is SafeResponse.Loading -> { _state.value = _state.value.copy(
                            isLoading = true,
                            error = null
                        ) }

                        is SafeResponse.Error -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = CitySearchError(
                                    CitySearchErrorType.API_ERROR,
                                    it.message
                                )
                            )
                        }

                        is SafeResponse.Success -> {
                            _state.value = _state.value.copy(
                                citySelected = it.data.first(),
                                isLoading = false,
                                error = null,
                            )
                        }
                    }
                }
            }
        } else {
            _state.value = _state.value.copy(error = CitySearchError(CitySearchErrorType.INVALID_CITY_NAME))
        }
    }
    fun getCitiesByQuery(query: String){
        viewModelScope.launch {
            weatherRepository.getCityAutocomplete(query).collect {
                when (it) {
                    is SafeResponse.Loading -> {
                        _state.value = _state.value.copy(
                        fetchedCitiesNames = emptyList(),
                        isLoading = true,
                        error = null
                    ) }

                    is SafeResponse.Error -> {
                        _state.value = _state.value.copy(
                            fetchedCitiesNames = emptyList(),
                            isLoading = false,
                            error = CitySearchError(
                                CitySearchErrorType.API_ERROR,
                                it.message
                            )
                        )
                    }

                    is SafeResponse.Success -> {
                        _state.value = _state.value.copy(
                            fetchedCitiesNames = it.data,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
            }
        }
    }
}

data class CitySearchScreenState(
    val citySelected: CityResponseItem? = null,
    val fetchedCitiesNames: List<CityResponseItem> = emptyList(),
    val savedCities: List<CityResponseItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: CitySearchError? = null,
)

data class CitySearchError(val type: CitySearchErrorType, val message: String? = null)
enum class CitySearchErrorType {
    INVALID_CITY_NAME,
    API_ERROR,
}