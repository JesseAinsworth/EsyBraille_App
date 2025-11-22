package com.easybraille.network

data class AuthResponse(
    val user: UserData,
    val token: String
)

data class UserData(
    val userId: String,
    val name: String,
    val email: String
)
