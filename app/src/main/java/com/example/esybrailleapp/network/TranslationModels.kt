package com.example.esybrailleapp.network

data class TranslationRequest(
    val spanishText: String,
    val brailleText: String,
    val userId: String? = null
)

data class TranslationResponse(
    val id: String,
    val message: String,
    val success: Boolean
)