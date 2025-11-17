package com.easybraille.network

import com.google.gson.annotations.SerializedName

/**
 * Representa la respuesta del servidor con el texto traducido.
 * La clave "braille" debe coincidir con la que envía tu API.
 */
data class TranslationResponse(
    @SerializedName("braille") // O "translation", o como se llame la clave en tu API
    val translatedText: String
)