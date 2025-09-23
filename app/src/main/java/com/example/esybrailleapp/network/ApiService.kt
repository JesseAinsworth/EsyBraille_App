package com.example.esybrailleapp.network

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(@Field("email") email: String, @Field("password") password: String): Call<String>

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<String>

    @FormUrlEncoded
    @POST("translate")
    fun translateToBraille(@Field("text") text: String): Call<String>
}
