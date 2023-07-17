package com.empik.weather.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.empik.weather.navigation.ScreensDestination
import com.empik.weather.ui.screens.city_search.SearchScreen
import com.empik.weather.ui.screens.city_weather.CityWeatherScreen
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.rememberNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {

    val navController = rememberNavController<ScreensDestination>(startDestination = ScreensDestination.SearchScreen)
    NavBackHandler(navController)

    AnimatedNavHost(controller = navController) { destination ->
        when (destination) {
            ScreensDestination.SearchScreen -> SearchScreen()
            ScreensDestination.CityWeatherScreen -> CityWeatherScreen()

        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}