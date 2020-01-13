package com.hadi.restaurantsarround.model

import com.google.gson.annotations.SerializedName


class ResponseVenues {
    @SerializedName("venues")
    var venues: ArrayList<Venue?>? = null


    override fun toString(): String {
        return venues.toString()
    }
}