package com.easybraille.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    /*** URL base del backend desplegado en Railway.
     * Es importante que la URL termine con una barra inclinada ("/").
     */
    private const val BASE_URL = "https://easybraillebackend-production.up.railway.app/"

    /**
     * Instancia única del servicio de la API (ApiService) creada de forma diferida (lazy).
     * Esto asegura que solo se cree una instancia de Retrofit en toda la aplicación,
     * lo cual es una buena práctica para el rendimiento y la gestión de recursos.
     */
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte las respuestas JSON a objetos Kotlin
            .build()

        // Crea la implementación de la interfaz ApiService
        retrofit.create(ApiService::class.java)
    }
}