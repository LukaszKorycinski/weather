package com.empik.weather.data.api.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.Locale

@Serializable
data class ForecastResponse(
    @SerialName("Headline") val headline: Headline,
    @SerialName("DailyForecasts") val dailyForecasts: List<DailyForecast>
)

@Serializable
data class Headline(
    @SerialName("EffectiveDate") val effectiveDate: String,
    @SerialName("EffectiveEpochDate") val effectiveEpochDate: Long,
    @SerialName("Severity") val severity: Int,
    @SerialName("Text") val text: String,
    @SerialName("Category") val category: String,
    @SerialName("EndDate") val endDate: String? = null,
    @SerialName("EndEpochDate") val endEpochDate: Long? = null,
    @SerialName("MobileLink") val mobileLink: String,
    @SerialName("Link") val link: String
)

@Serializable
data class DailyForecast(
    @SerialName("Date") val date: String,
    @SerialName("EpochDate") val epochDate: Long,
    @SerialName("Temperature") val temperature: Temperature,
    @SerialName("Day") val day: Day,
    @SerialName("Night") val night: Night,
    @SerialName("Sources") val sources: List<String>,
    @SerialName("MobileLink") val mobileLink: String,
    @SerialName("Link") val link: String
){
    fun getDayOfWeek(): String {
        val dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        return dateTime.dayOfWeek.getDisplayName(
            TextStyle.FULL,
            Locale.ENGLISH
        )
    }

}

@Serializable
data class Temperature(
    @SerialName("Minimum") val minimum: TemperatureValue,
    @SerialName("Maximum") val maximum: TemperatureValue
)

@Serializable
data class TemperatureValue(
    @SerialName("Value") val value: Double,
    @SerialName("Unit") val unit: String,
    @SerialName("UnitType") val unitType: Int
){
    fun getCelsius(): Double = if( unit=="F" ) (value - 32.0) * 5.0 / 9.0 else value
}

@Serializable
data class Day(
    @SerialName("Icon") val icon: Int,
    @SerialName("IconPhrase") val iconPhrase: String,
    @SerialName("HasPrecipitation") val hasPrecipitation: Boolean
)

@Serializable
data class Night(
    @SerialName("Icon") val icon: Int,
    @SerialName("IconPhrase") val iconPhrase: String,
    @SerialName("HasPrecipitation") val hasPrecipitation: Boolean
)
