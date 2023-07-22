package com.empik.weather

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.empik.weather.helper.TestHelper
import com.empik.weather.helper.getMockedForecast
import com.empik.weather.ui.TestTags
import com.empik.weather.ui.screens.city_weather.translatedDayOfWeek
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppFlowTest {

    @get:Rule
    var composeRule = createAndroidComposeRule<MainActivity>()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun appFlowWithAppBarBackTest() {
        val mockedResponseBody = getMockedForecast()

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.search_hint)),
            5000
        )

        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performTextInput("Warszawa")
        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performImeAction()

        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(2000)

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[0].getDayOfWeek())!!)),
            10000
        )
        composeRule.onNodeWithText(
            TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[0].getDayOfWeek())!!)
        ).assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()

        composeRule.mainClock.advanceTimeBy(2000)
        composeRule.mainClock.autoAdvance = true

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.search_hint)),
            5000
        )
        composeRule.onNodeWithText(TestHelper.getString(R.string.search_hint)).assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun appFlowWithSystemBackTest() {
        val mockedResponseBody = getMockedForecast()

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.search_hint)),
            5000
        )

        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performTextInput("Warszawa")
        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performImeAction()

        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(2000)

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[0].getDayOfWeek())!!)),
            10000
        )
        composeRule.onNodeWithText(
            TestHelper.getString(translatedDayOfWeek(mockedResponseBody.dailyForecasts[0].getDayOfWeek())!!)
        ).assertIsDisplayed()

        composeRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        composeRule.mainClock.advanceTimeBy(2000)
        composeRule.mainClock.autoAdvance = true

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.search_hint)),
            5000
        )
        composeRule.onNodeWithText(TestHelper.getString(R.string.search_hint)).assertIsDisplayed()
    }

}