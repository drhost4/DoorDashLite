package com.example.doordashlite.models

import com.google.gson.annotations.SerializedName

data class Restaurant(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("cover_img_url") val coverImageUrl: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("delivery_fee") val deliveryFee: Double = 0.0
)