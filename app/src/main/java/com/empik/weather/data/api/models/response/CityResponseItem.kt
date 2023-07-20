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
//    @SerialName("PrimaryPostalCode") val primaryPostalCode: String,
//    @SerialName("Region") val region: Region,
//    @SerialName("Country") val country: Country,
//    @SerialName("AdministrativeArea") val administrativeArea: AdministrativeArea,
//    @SerialName("TimeZone") val timeZone: TimeZone,
//    @SerialName("GeoPosition") val geoPosition: GeoPosition,
//    @SerialName("IsAlias") val isAlias: Boolean,
//    @SerialName("ParentCity") val parentCity: ParentCity,
//    @SerialName("SupplementalAdminAreas") val supplementalAdminAreas: List<SupplementalAdminArea>,
//    @SerialName("DataSets") val dataSets: List<String>
)

//@Serializable
//data class Region(
//    @SerialName("ID") val id: String,
//    @SerialName("LocalizedName") val localizedName: String,
//)
//
//@Serializable
//data class Country(
//    @SerialName("ID") val id: String,
//    @SerialName("LocalizedName") val localizedName: String,
//)

//@Serializable
//data class AdministrativeArea(
//    @SerialName("ID") val id: String,
//    @SerialName("LocalizedName") val localizedName: String,
//    @SerialName("Level") val level: Int,
//    @SerialName("LocalizedType") val localizedType: String,
//    @SerialName("CountryID") val countryId: String
//)

//@Serializable
//data class TimeZone(
//    @SerialName("Code") val code: String,
//    @SerialName("Name") val name: String,
//    @SerialName("IsDaylightSaving") val isDaylightSaving: Boolean,
//    @SerialName("NextOffsetChange") val nextOffsetChange: String
//)

//@Serializable
//data class GeoPosition(
//    @SerialName("Latitude") val latitude: Double,
//    @SerialName("Longitude") val longitude: Double,
////    @SerialName("Elevation") val elevation: Elevation
//)

//@Serializable
//data class Elevation(
//    @SerialName("Metric") val metric: Measurement,
//    @SerialName("Imperial") val imperial: Measurement
//)
//
//@Serializable
//data class Measurement(
//    @SerialName("Value") val value: Int,
//    @SerialName("Unit") val unit: String,
//    @SerialName("UnitType") val unitType: Int
//)

//@Serializable
//data class ParentCity(
//    @SerialName("Key") val key: String,
//    @SerialName("LocalizedName") val localizedName: String,
//)

//@Serializable
//data class SupplementalAdminArea(
//    @SerialName("Level") val level: Int,
//    @SerialName("LocalizedName") val localizedName: String,
//    @SerialName("EnglishName") val englishName: String
//)