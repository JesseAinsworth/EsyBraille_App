package com.easybraille.network

import com.google.gson.annotations.SerializedName

/**
 * Representa el cuerpo de la solicitud para traducir texto.
 * La clave "text" debe coincidir con lo que espera la API.
 */
data class TranslationRequest(
    @SerializedName("text")
    val text: String
)