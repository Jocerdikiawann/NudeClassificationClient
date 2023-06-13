package com.example.nudeclassification.data

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RetrofitInterface {
    @Multipart
    @POST("predict")
    suspend fun checkIsNude(
        @Part image: MultipartBody.Part
    ) : Response<IsNudesModel>
}