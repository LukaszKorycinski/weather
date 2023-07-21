package com.empik.weather

import com.empik.weather.data.api.models.response.DailyForecast
import com.empik.weather.data.api.models.response.Day
import com.empik.weather.data.api.models.response.Temperature
import com.empik.weather.ui.screens.city_weather.translatedDayOfWeek
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetDayOfWeekTest {
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getDayOfWeekTest() {
        val temperature: Temperature = mockk(relaxed = true)
        val day: Day = mockk(relaxed = true)

        var dailyForecast = DailyForecast("", 1, temperature, day)

        dailyForecast = dailyForecast.copy(date = "2023-07-20T07:00:00+02:00")
        print(dailyForecast.getDayOfWeek())
        assertTrue(dailyForecast.getDayOfWeek(), dailyForecast.getDayOfWeek() == "Thursday")
        assertTrue(translatedDayOfWeek(dailyForecast.getDayOfWeek()) == R.string.thursday)

        dailyForecast = dailyForecast.copy(date = "2023-07-21T07:00:00+02:00")
        assertTrue(dailyForecast.getDayOfWeek() == "Friday")
        assertTrue(translatedDayOfWeek(dailyForecast.getDayOfWeek()) == R.string.friday)

        dailyForecast = dailyForecast.copy(date = "2023-07-22T07:00:00+02:00")
        assertTrue(dailyForecast.getDayOfWeek() == "Saturday")
        assertTrue(translatedDayOfWeek(dailyForecast.getDayOfWeek()) == R.string.saturday)

        dailyForecast = dailyForecast.copy(date = "2023-07-23T07:00:00+02:00")
        assertTrue(dailyForecast.getDayOfWeek() == "Sunday")
        assertTrue(translatedDayOfWeek(dailyForecast.getDayOfWeek()) == R.string.sunday)

        dailyForecast = dailyForecast.copy(date = "2023-07-24T07:00:00+02:00")
        assertTrue(dailyForecast.getDayOfWeek() == "Monday")
        assertTrue(translatedDayOfWeek(dailyForecast.getDayOfWeek()) == R.string.monday)

        dailyForecast = dailyForecast.copy(date = "2023-07-25T07:00:00+02:00")
        assertTrue(dailyForecast.getDayOfWeek() == "Tuesday")
        assertTrue(translatedDayOfWeek(dailyForecast.getDayOfWeek()) == R.string.tuesday)

        dailyForecast = dailyForecast.copy(date = "2023-07-26T07:00:00+02:00")
        assertTrue(dailyForecast.getDayOfWeek() == "Wednesday")
        assertTrue(translatedDayOfWeek(dailyForecast.getDayOfWeek()) == R.string.wednesday)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}