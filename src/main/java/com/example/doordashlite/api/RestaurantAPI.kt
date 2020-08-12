package com.example.doordashlite.api

import com.example.doordashlite.models.Restaurant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantAPI {
    @GET("/v2/restaurant/?")
    fun retrieveRestaurants(
        @Query("lat") lat: Double,
        @Query("lng") long: Double
    ): Call<List<Restaurant>>

    @GET("/v2/restaurant/{restaurant_id}/")
    fun retrieveRestaurantDetails(@Path("restaurant_id") restaurantId: Long): Call<Restaurant>
}