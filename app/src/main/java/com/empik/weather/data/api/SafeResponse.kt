package com.empik.weather.data.api

import com.empik.weather.data.api.models.response.ErrorResponse
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.get
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

sealed interface SafeResponse<T> {
    data class Success<T>(val data: T) : SafeResponse<T>
    class Error<T>(val errorType: ErrorType, val throwable: Throwable? = null, val message: String? = null) : SafeResponse<T>
    class Loading<T>: SafeResponse<T>
}

enum class ErrorType {
    NETWORK, DATA
}

suspend fun <T> safeApiCall(block: suspend () -> Response<T>): SafeResponse<T> {
    return try {
        val response = block()
        val body = response.body()

        if (body != null) {
            SafeResponse.Success(body)
        } else {
            Timber.w("error API call - missing data body")

            val errorBody = response.errorBody()?.string()
            errorBody?.let {
                val jsonSerializer = get<Json>(Json::class.java)
                val errorResponseBody = jsonSerializer.decodeFromString<ErrorResponse>(it)
                SafeResponse.Error(errorType = ErrorType.DATA, message = errorResponseBody.message)
            } ?: SafeResponse.Error(errorType = ErrorType.DATA)
        }
    } catch (e: Throwable) {
        if (e is IOException) {
            Timber.w(e, "error API call - network issue")
            SafeResponse.Error(errorType = ErrorType.NETWORK)
        } else {
            Timber.w(e, "error API call - incorrect data/other")
            SafeResponse.Error(errorType = ErrorType.DATA)
        }
    }

}