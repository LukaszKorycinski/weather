package com.empik.weather.data.repository

import com.empik.weather.App
import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.api.models.response.ForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.koin.java.KoinJavaComponent

class WeatherRepositoryMocked: WeatherRepositoryInterface {
    companion object{
        const val FORECAST_MOCK_JSON = "forecast_mock_response.json"
        const val CITY_MOCK_JSON = "city_mock_response.json"
        const val CITIES_QUERY_MOCK_JSON = "cities_query_mock_response.json"
    }
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getCities(query: String): Flow<SafeResponse<List<CityResponseItem>>> {
        val responseBody = App.appContext.assets.open(CITY_MOCK_JSON).use {
            val json = KoinJavaComponent.get<Json>(Json::class.java)
            json.decodeFromStream<List<CityResponseItem>>(it)
        }
        return flow { emit(SafeResponse.Success(responseBody)) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getCityAutocomplete(query: String): Flow<SafeResponse<List<CityResponseItem>>> {
        val responseBody = App.appContext.assets.open(CITIES_QUERY_MOCK_JSON).use {
            val json = KoinJavaComponent.get<Json>(Json::class.java)
            json.decodeFromStream<List<CityResponseItem>>(it)
        }
        return flow { emit(SafeResponse.Success(responseBody)) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getForecast(locationKey: String): Flow<SafeResponse<ForecastResponse>> {
        val responseBody = App.appContext.assets.open(FORECAST_MOCK_JSON).use {
            val json = KoinJavaComponent.get<Json>(Json::class.java)
            json.decodeFromStream<ForecastResponse>(it)
        }
        return flow { emit(SafeResponse.Success(responseBody)) }
    }
}