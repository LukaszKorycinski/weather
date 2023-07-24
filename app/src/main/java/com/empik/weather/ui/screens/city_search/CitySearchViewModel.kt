package com.empik.weather.ui.screens.city_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.repository.LocalRepository
import com.empik.weather.data.repository.WeatherRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CitySearchViewModel(
    private val weatherRepository: WeatherRepositoryInterface,
    private val localRepository: LocalRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<CitySearchScreenState>(CitySearchScreenState.CONTENT())
    val state = _state.asStateFlow()

    private val _citySelectedState = MutableStateFlow<CityResponseItem?>(null)
    val citySelectedState = _citySelectedState.asStateFlow()

    private val _savedCitiesState = MutableStateFlow<List<CityResponseItem>>(emptyList())
    val savedCitiesState = _savedCitiesState.asStateFlow()
    companion object {
        private val cityRegex = "^([a-zA-Z\\u0080-\\u024F]+(?:. |-| |'))*[a-zA-Z\\u0080-\\u024F]*\$".toRegex()
    }

    init {
        fetchSavedCities()
    }

    private fun fetchSavedCities(){
        _savedCitiesState.value = localRepository.cities
    }

    fun saveCity(city: CityResponseItem){
        viewModelScope.launch {
            localRepository.saveCity(city)
            fetchSavedCities()
        }
    }

    fun validateCityName(cityName: String): Boolean {
        return cityRegex.matches(cityName)
    }

    fun getCitiesByQuery(query: String, shouldAutoSelect: Boolean = false){
        if (validateCityName(query)) {
            viewModelScope.launch {
                weatherRepository.getCityAutocomplete(query).collect {
                    when (it) {
                        is SafeResponse.Loading -> CitySearchScreenState.LOADING

                        is SafeResponse.Error -> {
                            _state.value = CitySearchScreenState.ERROR(
                                    CitySearchErrorType.API_ERROR,
                                    it.message ?: it.throwable?.localizedMessage
                                )
                        }

                        is SafeResponse.Success -> {
                            if(shouldAutoSelect) {
                                _citySelectedState.value = it.data.first()
                            }else{
                                _state.value = CitySearchScreenState.CONTENT(it.data)
                            }
                        }
                    }
                }
            }
        } else {
            _state.value = CitySearchScreenState.ERROR(CitySearchErrorType.INVALID_CITY_NAME)
        }
    }

    fun errorHandled() {
        clearQuery()
    }

    fun clearQuery() {
        _state.value = CitySearchScreenState.CONTENT()
    }

    fun clearCitySelected() {
        _citySelectedState.value = null
    }
}

sealed interface CitySearchScreenState {
    class CONTENT(val fetchedCitiesNames: List<CityResponseItem> = emptyList()) : CitySearchScreenState
    object LOADING : CitySearchScreenState
    class ERROR(val type: CitySearchErrorType, val message: String? = null) : CitySearchScreenState
}

enum class CitySearchErrorType {
    INVALID_CITY_NAME,
    API_ERROR,
}