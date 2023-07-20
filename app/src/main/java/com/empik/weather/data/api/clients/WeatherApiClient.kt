package com.empik.weather.data.api.clients

import com.empik.weather.BuildConfig
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.api.models.response.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApiClient {
    @GET("locations/v1/cities/search")
    suspend fun getCities(
        @Query("apikey") apiKey: String = BuildConfig.API_KEY,
        @Query("q") query: String,
        @Query("language") language: String = "pl-pl",
    ): Response<List<CityResponseItem>>

    @GET("locations/v1/cities/autocomplete")
    suspend fun getCityAutocomplete(
        @Query("apikey") apiKey: String = BuildConfig.API_KEY,
        @Query("q") query: String,
        @Query("language") language: String = "pl-pl",
    ): Response<List<CityResponseItem>>

    @GET("forecasts/v1/daily/5day/{locationKey}")
    suspend fun getForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = "pl-pl",
    ): Response<ForecastResponse>
}