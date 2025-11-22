package com.easybraille.network

data class TranslationRequest(
    val userId: String,
    val originalText: String,
    val brailleText: String
)



data class TranslationResponse(
    val message: String // Tu backend devuelve "message", no "id" o "status"
)

// 👇 NUEVO: Modelo para la respuesta del historial
data class HistoryItem(
    val originalText: String,
    val brailleText: String,
    val createdAt: String
)

data class HistoryResponse(
    val history: List<HistoryItem>
)