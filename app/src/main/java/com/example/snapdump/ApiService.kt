package com.example.snapdump

import retrofit2.Call
import retrofit2.http.GET
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("api.php")
    fun getEntities(): Call<List<Entity>>

    @Multipart
    @POST("api.php")
    fun createEntity(
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<Entity>
}