package com.easybraille.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("api/translations")
    fun saveTranslation(@Body request: TranslationRequest): Call<TranslationResponse>

    @GET("api/translations/history")
    fun getTranslationHistory(@Query("userId") userId: String): Call<HistoryResponse>
}
