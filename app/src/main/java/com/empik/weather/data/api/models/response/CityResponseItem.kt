package com.empik.weather.data.api.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityResponseItem(
    @SerialName("Version") val version: Int,
    @SerialName("Key") val key: String,
    @SerialName("Type") val type: String,
    @SerialName("Rank") val rank: Int,
    @SerialName("LocalizedName") val localizedName: String,
)