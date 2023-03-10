package com.amanpathak.yelprestaurantapp.network

import android.content.Context
import com.amanpathak.yelprestaurantapp.BuildConfig
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient (private val context: Context) {
    private var retrofit: Retrofit? = null

    val api: Api
        get() {

            val client = OkHttpClient.Builder()
                .addInterceptor {
                    val requestBuilder: Request.Builder = it.request().newBuilder()
                    requestBuilder.header("Authorization",  BuildConfig.YelpApiKey)
                    it.proceed(requestBuilder.build())
                }
                .addInterceptor(ChuckerInterceptor(context))
                .build()

            val BASE_URL = "https://api.yelp.com/v3/businesses/"
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!.create(Api::class.java)
        }
}