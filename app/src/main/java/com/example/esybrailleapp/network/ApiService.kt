package com.example.esybrailleapp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {


    @FormUrlEncoded
    @POST("api/login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<String>

    @POST("api/register")
    fun register(@Body request: RegisterRequest): Call<String>

    @POST("api/translations")
    fun saveTranslation(@Body request: TranslationRequest): Call<TranslationResponse>
}