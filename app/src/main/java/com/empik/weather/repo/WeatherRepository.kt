package com.empik.weather.repo

import com.empik.weather.data.api.clients.WeatherApiClient

class WeatherRepository(
    private val weatherApiClient: WeatherApiClient,
) {

}