package com.example.esybrailleapp.network.TranslationModels

data class TranslationRequest(
    val originalText: String,
    val translatedBraille: String
)

data class TranslationResponse(
    val id: String,
    val status: String
)
