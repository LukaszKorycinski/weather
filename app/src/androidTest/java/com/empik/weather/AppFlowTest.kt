package com.empik.weather

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.empik.weather.helper.TestHelper
import com.empik.weather.ui.TestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppFlowTest {


    @get:Rule
    var composeRule = createAndroidComposeRule<MainActivity>()

    @OptIn(ExperimentalTestApi::class, ExperimentalMaterial3Api::class)
    @Test
    fun appFlowTest() {
        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.search_hint)),
            5000
        )

        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performTextInput("Warszawa")
        composeRule.onNodeWithTag(TestTags.SEARCH).onChildAt(0).performImeAction()

        composeRule.waitUntilAtLeastOneExists(
            hasText(TestHelper.getString(R.string.search_hint)),
            5000
        )

        composeRule.waitUntilAtLeastOneExists(
            hasText("Warszawa"),
            3000
        )
    }

}