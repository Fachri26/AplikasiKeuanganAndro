package com.example.aplikasikeuangan.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class ExchangeRateResponse(
    val success: Boolean,
    val source: String,
    val quotes: Map<String, Double>
)


interface ExchangeRateApiService {
    @GET("live")
    suspend fun getRates(
        @Query("access_key") accessKey: String,
        @Query("source") source: String,
        @Query("currencies") currencies: String
    ): Response<ExchangeRateResponse>
}


