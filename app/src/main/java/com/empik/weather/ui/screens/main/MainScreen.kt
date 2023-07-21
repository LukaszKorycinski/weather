package com.empik.weather.ui.screens.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.empik.weather.navigation.ScreensDestination
import com.empik.weather.ui.screens.city_search.SearchScreen
import com.empik.weather.ui.screens.city_weather.CityWeatherScreen
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController<ScreensDestination>(startDestination = ScreensDestination.SearchScreen)
    NavBackHandler(navController)

    AnimatedNavHost(controller = navController) { destination ->
        when (destination) {
            ScreensDestination.SearchScreen -> {
                SearchScreen(
                    onCitySelected = { navController.navigate(ScreensDestination.CityWeatherScreen(it.key, it.localizedName)) }
                )
            }
            is ScreensDestination.CityWeatherScreen ->
                CityWeatherScreen(
                    locationKey = destination.cityKey,
                    cityName = destination.cityName,
                    onBack = {
                        Timber.e("sdfgsd "+navController.backstack.entries.size)
                        if (navController.backstack.entries.size > 1) {
                        navController.pop()
                    } }
                )
        }
    }
}