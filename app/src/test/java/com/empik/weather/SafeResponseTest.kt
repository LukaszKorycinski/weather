package com.empik.weather

import com.empik.weather.data.api.SafeResponse
import com.empik.weather.data.api.safeApiCall
import com.empik.weather.di.networkModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTestRule
import retrofit2.Response

class SafeResponseTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(networkModule)
    }

    @Test
    fun safeApiCallTest() {
        runBlocking{
            val response = safeApiCall{
                getSomething()
            }

            assert(response is SafeResponse.Success)

            val errorResponse = safeApiCall{
                getError()
            }
            assert(errorResponse is SafeResponse.Error)
            assert((errorResponse as SafeResponse.Error).message == "Api Authorization failed")
        }
    }

    private suspend fun getSomething(): Response<MockResponse> {
        delay(200)
        return Response.success(MockResponse("data"))
    }

    private suspend fun getError(): Response<MockResponse> {
        delay(200)
        return Response.error(500, "{\"Code\":\"Unauthorized\",\"Message\":\"Api Authorization failed\"}".toResponseBody())
    }

    data class MockResponse(
        val data: String,
    )
}

