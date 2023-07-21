package com.empik.weather.data.repository

import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.clients.WeatherApiClient
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.api.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val weatherApiClient: WeatherApiClient,
): WeatherRepositoryInterface {
    override suspend fun getCities(query: String): Flow<SafeResponse<List<CityResponseItem>>> =
        flow {
            emit(safeApiCall {
                weatherApiClient.getCities(
                    query = query,
                )
            })
    }

    override suspend fun getCityAutocomplete(query: String): Flow<SafeResponse<List<CityResponseItem>>> =
        flow {
            emit(safeApiCall {
                weatherApiClient.getCityAutocomplete(
                    query = query,
                )
            })
        }

    override suspend fun getForecast(locationKey: String): Flow<SafeResponse<ForecastResponse>> =
        flow {
            emit(safeApiCall {
                weatherApiClient.getForecast(
                    locationKey = locationKey,
                )
            })
    }
}