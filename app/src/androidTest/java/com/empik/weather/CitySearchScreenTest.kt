package com.empik.weather

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.helper.TestHelper
import com.empik.weather.ui.TestTags
import com.empik.weather.ui.screens.city_search.CitySearchError
import com.empik.weather.ui.screens.city_search.CitySearchErrorType
import com.empik.weather.ui.screens.city_search.CitySearchScreenState
import com.empik.weather.ui.screens.city_search.SearchScreenContent
import com.empik.weather.ui.theme.WeatherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class CitySearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchScreenTest() {
        val uuid = UUID.randomUUID().toString()
        val cityItem = CityResponseItem(0, uuid, "", 0, "Kraków")

        val state = CitySearchScreenState(
            citySelected = null,
            fetchedCitiesNames = emptyList(),
            savedCities = listOf(cityItem),
            isLoading = false,
            error = null,
        )

        var searchQuesry = ""
        var searchForCity = ""
        var citySelected = ""

        composeRule.setContent {
            WeatherTheme {
                SearchScreenContent(
                    state = state,
                    onCitySearchByQuery = { searchQuesry = it },
                    onCitySearch = { searchForCity = it },
                    onCitySelected = { citySelected = it.key },
                    onErrorHandled = { }
                )
            }
        }

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.search_hint)),
            5000
        )
        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performTextInput("Warszawa")
        assert(searchQuesry == "Warszawa")


        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performImeAction()
        assert(searchForCity == "Warszawa")

        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performTextClearance()
        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performTextInput("Kra")

        composeRule.onNodeWithText(cityItem.localizedName).performClick()
        assert(citySelected == uuid)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchScreenErrorCityNameInvalidTest(){

        val state = CitySearchScreenState(
            citySelected = null,
            fetchedCitiesNames = emptyList(),
            savedCities = emptyList(),
            isLoading = false,
            error = CitySearchError(type = CitySearchErrorType.INVALID_CITY_NAME)
        )

        composeRule.setContent {
            WeatherTheme {
                SearchScreenContent(
                    state = state,
                    onCitySearchByQuery = { },
                    onCitySearch = { },
                    onCitySelected = { },
                    onErrorHandled = { }
                )
            }
        }

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.city_name_invalid)),
            5000
        )
        composeRule.onNodeWithText(TestHelper.getString(R.string.city_name_invalid)).assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchScreenApiErrorTest(){

        val state = CitySearchScreenState(
            citySelected = null,
            fetchedCitiesNames = emptyList(),
            savedCities = emptyList(),
            isLoading = false,
            error = CitySearchError(type = CitySearchErrorType.API_ERROR)
        )

        composeRule.setContent {
            WeatherTheme {
                SearchScreenContent(
                    state = state,
                    onCitySearchByQuery = { },
                    onCitySearch = { },
                    onCitySelected = { },
                    onErrorHandled = { }
                )
            }
        }

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.unknown_api_error)),
            5000
        )
        composeRule.onNodeWithText(TestHelper.getString(R.string.unknown_api_error)).assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchScreenApiErrorMessageTest(){
        val errorMessage = "Error message"
        val state = CitySearchScreenState(
            citySelected = null,
            fetchedCitiesNames = emptyList(),
            savedCities = emptyList(),
            isLoading = false,
            error = CitySearchError(type = CitySearchErrorType.API_ERROR, message = errorMessage)
        )

        composeRule.setContent {
            WeatherTheme {
                SearchScreenContent(
                    state = state,
                    onCitySearchByQuery = { },
                    onCitySearch = { },
                    onCitySelected = { },
                    onErrorHandled = { }
                )
            }
        }

        composeRule.waitUntilAtLeastOneExists(
            hasText(errorMessage),
            5000
        )
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}