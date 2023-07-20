package com.empik.weather.data.repository

import com.empik.weather.data.api.models.response.CityResponseItem
import com.empik.weather.data.data_store.DataStore

class LocalRepository(
    private val dataStore: DataStore,
) {
    val cities
        get() = dataStore.cities

    fun saveCity(city: CityResponseItem){
        val savedCities = cities.toMutableList()
        savedCities.removeAll { it.key == city.key }
        savedCities.add(0, city)
        dataStore.cities = savedCities
    }
}