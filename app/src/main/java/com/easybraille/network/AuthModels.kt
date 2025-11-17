package com.easybraille.network

// Este archivo es la ÚNICA fuente para los modelos de datos de autenticación.

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val name: String, // Estandarizado a "name"
    val email: String
)

data class RegisterRequest(
    val name: String, // Estandarizado a "name"
    val email: String,
    val password: String
)
