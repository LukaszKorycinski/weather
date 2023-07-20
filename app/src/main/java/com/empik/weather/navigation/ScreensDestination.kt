package com.empik.weather.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface ScreensDestination : Parcelable {

    @Parcelize
    object SearchScreen : ScreensDestination

    @Parcelize
    class CityWeatherScreen(val cityKey: String, val cityName: String) : ScreensDestination
}