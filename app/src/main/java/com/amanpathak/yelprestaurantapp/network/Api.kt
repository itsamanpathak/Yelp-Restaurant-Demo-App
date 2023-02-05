package com.amanpathak.yelprestaurantapp.network

import com.amanpathak.yelprestaurantapp.network.models.BusinessResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface Api {

    @GET("search")
    @Headers("content-type: application/json")
    fun getBusinessAsPerLocation(
        @Query("radius") radius: String,
        @Query("sort_by") sortBy: String = "distance",
        @Query("latitude") lat: String?,
        @Query("longitude") lon: String?,
        @Query("limit") limit : String?,
        @Query("offset") offset : String?
    ): Call<BusinessResponse>


}