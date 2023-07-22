package com.empik.weather.helper

import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.repository.WeatherRepositoryInterface
import com.empik.weather.data.repository.WeatherRepositoryMocked
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent

internal fun getMockedForecast(): ForecastResponse {
    val weatherRepository =  KoinJavaComponent.get<WeatherRepositoryInterface>(WeatherRepositoryInterface::class.java)
            as WeatherRepositoryMocked

    var mockedResponse: ForecastResponse? = null
    runBlocking {
        weatherRepository.getForecast("").collect {
            mockedResponse = (it as SafeResponse.Success<ForecastResponse>).data
        }
    }
    return mockedResponse!!
}