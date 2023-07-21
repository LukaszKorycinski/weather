package com.empik.weather.helper

val testMode : Boolean by lazy {
    try {
        Class.forName("androidx.test.espresso.Espresso")
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}