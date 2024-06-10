package com.dicoding.mystoryapp.api

import com.dicoding.mystoryapp.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun registUser(@Body requestRegister: RegisterUserData): Call<ResponsePostStory>

    @POST("login")
    fun loginUser(@Body requestLogin: LoginUserData): Call<ResponseLogin>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String,
        ): Call<ResponseStory>

    @Multipart
    @POST("stories")
    fun postNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponsePostStory>
}