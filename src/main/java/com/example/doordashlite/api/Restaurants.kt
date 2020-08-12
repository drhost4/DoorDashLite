package com.example.doordashlite.api

import com.example.doordashlite.models.Location
import com.example.doordashlite.models.Restaurant
import okhttp3.OkHttpClient
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class Restaurants {
    private val apiInterface: RestaurantAPI

    companion object {
        const val BASE_URL = "https://api.doordash.com"
    }

    init {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiInterface = retrofit.create(RestaurantAPI::class.java)
    }

    fun getRestaurants(location: Location, callback: Callback<List<Restaurant>>) {
        val call = apiInterface.retrieveRestaurants(location.latitude, location.longitude)
        call.enqueue(callback)
    }

    fun getDetails(restaurantId: Long, callback: Callback<Restaurant>) {
        val call = apiInterface.retrieveRestaurantDetails(restaurantId)
        call.enqueue(callback)
    }
}