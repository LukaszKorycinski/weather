package com.empik.weather.data.data_store

import android.content.Context
import com.empik.weather.data.api.models.response.CityResponseItem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import timber.log.Timber

class DataStore(
    private val context: Context,
    private val json: Json
) {

    companion object {
        const val CITIES_FILE = "cities.json"
    }

    var cities: List<CityResponseItem> = loadCities()
        set(value) {
            field = value
            saveCities()
        }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadCities(): List<CityResponseItem> {
        return try {
            context.openFileInput(CITIES_FILE).use {
                json.decodeFromStream(it)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun saveCities() {
        try {
            context.openFileOutput(CITIES_FILE, Context.MODE_PRIVATE).use {
                json.encodeToStream(cities, it)
            }
        } catch (e: Exception) {
            Timber.e(e, "error saving user file")
        }
    }
}