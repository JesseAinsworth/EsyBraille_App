package com.easybraille.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Endpoint para el login de usuario
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    // Endpoint para el registro de usuario
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    // Endpoint para guardar la traducción
    @POST("translate")
    fun saveTranslation(@Body request: TranslationRequest): Call<TranslationResponse>
}