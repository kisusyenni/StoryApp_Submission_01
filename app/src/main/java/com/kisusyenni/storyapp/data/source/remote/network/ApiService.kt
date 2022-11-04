package com.kisusyenni.storyapp.data.source.remote.network

import com.kisusyenni.storyapp.data.source.remote.response.FileUploadResponse
import com.kisusyenni.storyapp.data.source.remote.response.LoginResponse
import com.kisusyenni.storyapp.data.source.remote.response.RegisterResponse
import com.kisusyenni.storyapp.data.source.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("/v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @GET("v1/stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ) : Call<StoriesResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
    ): Call<FileUploadResponse>
}

