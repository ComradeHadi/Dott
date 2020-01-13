package com.hadi.restaurantsarround.model

import com.google.gson.annotations.SerializedName


data class Location(
    @SerializedName("lat")
    var lat: Double = 0.0,
    @SerializedName("lng")
    var lng: Double = 0.0,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("country")
    var country: String? = null,
    @SerializedName("city")
    var city: String? = null,
    @SerializedName("state")
    var state: String? = null
) {

    val readableLoc: String?
        get() {
            if (state != null && city != null) return "$state/$city"
            return if (state != null) state else country
        }
}