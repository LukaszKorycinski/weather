package com.empik.weather.data.repository

import com.empik.weather.App
import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.clients.WeatherApiClient
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.api.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.io.InputStream


class WeatherRepository(
    private val weatherApiClient: WeatherApiClient,
) {
    suspend fun getCities(query: String): Flow<SafeResponse<List<CityResponseItem>>> = flow {
        emit(safeApiCall {
            weatherApiClient.getCities(
                query = query,
            )
        })
    }

    suspend fun getCityAutocomplete(query: String): Flow<SafeResponse<List<CityResponseItem>>> =
        flow {
            emit(safeApiCall {
                weatherApiClient.getCityAutocomplete(
                    query = query,
                )
            })
        }

    suspend fun getForecast(locationKey: String): Flow<SafeResponse<ForecastResponse>> = flow {
        emit(safeApiCall {
            weatherApiClient.getForecast(
                locationKey = locationKey,
            )
        })
    }


    private val jsonSerializer = Json { ignoreUnknownKeys = true }

    suspend fun getMockedForecast(locationKey: String): Flow<SafeResponse<ForecastResponse>> = flow {
        emit(
            run {
                val inputStream: InputStream = App.appContext.assets.open("forecast_mock_response.json")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                val json = String(buffer, charset("UTF-8"))
                val responseBody = jsonSerializer.decodeFromString<ForecastResponse>(json)
                SafeResponse.Success(data = responseBody)
            }
        )
    }
}