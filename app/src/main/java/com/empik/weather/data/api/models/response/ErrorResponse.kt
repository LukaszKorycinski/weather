package com.empik.weather.data.api.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("Code")
    val code: String,

    @SerialName("Message")
    val message: String,
)
