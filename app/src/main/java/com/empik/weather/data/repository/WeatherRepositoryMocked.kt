package com.empik.weather.data.repository

import com.empik.weather.App
import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.data_store.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.koin.java.KoinJavaComponent

class WeatherRepositoryMocked: WeatherRepositoryInterface {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getCities(query: String): Flow<SafeResponse<List<CityResponseItem>>> {
        val responseBudy = App.appContext.assets.open(DataStore.CITY_MOCK_JSON).use {
            val json = KoinJavaComponent.get<Json>(Json::class.java)
            json.decodeFromStream<List<CityResponseItem>>(it)
        }
        return flow { emit(SafeResponse.Success(responseBudy)) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getCityAutocomplete(query: String): Flow<SafeResponse<List<CityResponseItem>>> {
        val responseBudy = App.appContext.assets.open(DataStore.CITIES_QUERY_MOCK_JSON).use {
            val json = KoinJavaComponent.get<Json>(Json::class.java)
            json.decodeFromStream<List<CityResponseItem>>(it)
        }
        return flow { emit(SafeResponse.Success(responseBudy)) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getForecast(locationKey: String): Flow<SafeResponse<ForecastResponse>> {
        val responseBudy = App.appContext.assets.open(DataStore.FORECAST_MOCK_JSON).use {
            val json = KoinJavaComponent.get<Json>(Json::class.java)
            json.decodeFromStream<ForecastResponse>(it)
        }
        return flow { emit(SafeResponse.Success(responseBudy)) }
    }
}