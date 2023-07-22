package com.empik.weather

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.data_store.DataStore
import com.empik.weather.data.repository.WeatherRepository
import com.empik.weather.data.repository.WeatherRepositoryMocked
import com.empik.weather.helper.TestHelper
import com.empik.weather.helper.getMockedForecast
import com.empik.weather.ui.TestTags
import com.empik.weather.ui.screens.city_weather.CityWeatherError
import com.empik.weather.ui.screens.city_weather.CityWeatherScreenContainer
import com.empik.weather.ui.screens.city_weather.CityWeatherState
import com.empik.weather.ui.screens.city_weather.translatedDayOfWeek
import com.empik.weather.ui.theme.WeatherTheme
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.internal.wait
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent
import java.io.InputStream
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class CityWeatherScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cityWeatherScreenTest() {
        val mockedResponseBody = getMockedForecast()

        val state = CityWeatherState(
            forecast = mockedResponseBody,
            isLoading = false,
            error = CityWeatherError(false),
        )

        var backClicked = false
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            WeatherTheme {
                CityWeatherScreenContainer(
                    state = state,
                    cityName = "Warszawa",
                    onBack = { backClicked = true },
                    onRetry = {},
                )
            }
        }

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[1].getDayOfWeek())!!)),
            3000
        )

        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()
        assert(backClicked)

        composeRule
            .onNodeWithText("Warszawa")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[0].getDayOfWeek())!!))
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(mockedResponseBody.dailyForecasts[0].temperature.maximum.getCelsius()
                .roundToInt().toString()+"\u2103")
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText(mockedResponseBody.dailyForecasts[0].day.iconPhrase)[0]
            .assertIsDisplayed()

        for (i in 1..4) {
            composeRule.onNodeWithTag(TestTags.DAYS_PAGER).performTouchInput{ swipeLeft() }

            composeRule
                .onNodeWithText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[i].getDayOfWeek())!!))
                .assertIsDisplayed()

            composeRule
                .onNodeWithTag(TestTags.DAY_PAGE+i)
                .onChildren()
                .filter(hasText(mockedResponseBody.dailyForecasts[i].temperature.maximum.getCelsius().roundToInt().toString()+"\u2103"))
                .onFirst()
                .assertIsDisplayed()
            composeRule
                .onNodeWithTag(TestTags.DAY_PAGE+i)
                .onChildren()
                .filter(hasText(mockedResponseBody.dailyForecasts[i].day.iconPhrase))
                .onFirst()
                .assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cityWeatherScreenSwipeByButtonsTest(){
        val mockedResponseBody = getMockedForecast()

        val state = CityWeatherState(
            forecast = mockedResponseBody,
            isLoading = false,
            error = CityWeatherError(false),
        )

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            WeatherTheme {
                CityWeatherScreenContainer(
                    state = state,
                    cityName = "Warszawa",
                    onBack = { },
                    onRetry = {},
                )
            }
        }

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[0].getDayOfWeek())!!)),
            3000
        )

        composeRule.onNodeWithTag(TestTags.NEXT_DAY_BUTTON).performClick()
        composeRule.mainClock.advanceTimeBy(1000)

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[1].getDayOfWeek())!!)),
            3000
        )

        composeRule
            .onNodeWithText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[1].getDayOfWeek())!!))
            .assertIsDisplayed()

        composeRule.onNodeWithTag(TestTags.PREVIOUS_DAY_BUTTON).performClick()
        composeRule.mainClock.advanceTimeBy(1000)

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[0].getDayOfWeek())!!)),
            3000
        )
        composeRule
            .onNodeWithText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[0].getDayOfWeek())!!))
            .assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cityWeatherScreenErrorsTest() {
        val state = CityWeatherState(
            forecast = null,
            isLoading = false,
            error = CityWeatherError(true, "Error message"),
        )
        var onRetryClicked = false

        composeRule.setContent {
            WeatherTheme {
                CityWeatherScreenContainer(
                    state = state,
                    cityName = "Warszawa",
                    onBack = {},
                    onRetry = { onRetryClicked = true },
                )
            }
        }

        composeRule.waitUntilAtLeastOneExists(
            hasTestTag(TestTags.RETRY_BUTTON),
            3000
        )

        composeRule.onNodeWithTag(TestTags.RETRY_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.RETRY_BUTTON).performClick()
        assert(onRetryClicked)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cityWeatherScreenLoadingTest() {
        val state = CityWeatherState(
            forecast = null,
            isLoading = true,
            error = CityWeatherError(false),
        )

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            WeatherTheme {
                CityWeatherScreenContainer(
                    state = state,
                    cityName = "Warszawa",
                    onBack = { },
                    onRetry = { },
                )
            }
        }

        composeRule.waitUntilAtLeastOneExists(
            hasTestTag(TestTags.LOADING_SCREEN),
            3000
        )

        composeRule.onNodeWithTag(TestTags.LOADING_SCREEN).assertIsDisplayed()
    }
}