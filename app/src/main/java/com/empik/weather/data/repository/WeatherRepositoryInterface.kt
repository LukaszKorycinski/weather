package com.empik.weather.data.repository

import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.api.models.response.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepositoryInterface {

    suspend fun getCities(query: String): Flow<SafeResponse<List<CityResponseItem>>>

    suspend fun getCityAutocomplete(query: String): Flow<SafeResponse<List<CityResponseItem>>>

    suspend fun getForecast(locationKey: String): Flow<SafeResponse<ForecastResponse>>
}