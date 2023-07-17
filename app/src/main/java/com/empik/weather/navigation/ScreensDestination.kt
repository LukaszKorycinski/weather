package com.empik.weather.navigation

sealed interface ScreensDestination {

    object SearchScreen : ScreensDestination

    object CityWeatherScreen : ScreensDestination
}