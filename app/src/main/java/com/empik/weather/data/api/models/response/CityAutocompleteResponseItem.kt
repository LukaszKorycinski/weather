package com.empik.weather.data.api.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityAutocompleteResponseItem(
    @SerialName("Version") val version: Int,
    @SerialName("Key") val key: String,
    @SerialName("Type") val type: String,
    @SerialName("Rank") val rank: Int,
    @SerialName("LocalizedName") val localizedName: String,
//    @SerialName("Country") val country: Country,
//    @SerialName("AdministrativeArea") val administrativeArea: AdministrativeArea
)

//@Serializable
//data class Country(
//    @SerialName("ID") val id: String,
//    @SerialName("LocalizedName") val localizedName: String
//)
//
//@Serializable
//data class AdministrativeArea(
//    @SerialName("ID") val id: String,
//    @SerialName("LocalizedName") val localizedName: String
//)