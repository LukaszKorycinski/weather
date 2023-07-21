package com.empik.weather.helper

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry

class TestHelper {
    companion object{
        fun getString(stringId: Int): String {
            val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
            return context.resources.getString(stringId)
        }
    }
}