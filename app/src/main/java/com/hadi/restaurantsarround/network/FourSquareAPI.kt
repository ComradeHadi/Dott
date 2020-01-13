package com.hadi.restaurantsarround.network

import com.hadi.restaurantsarround.model.VenueDetailResponse
import com.hadi.restaurantsarround.model.VenuesListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface FourSquareAPI {

    //MARK: search for place by name
    @GET("venues/search")
    fun getVenues(
        @Query("v") version: String?, @Query("client_id") clientID: String?, @Query(
            "client_secret"
        ) clientSecret: String?, @Query("query") placeType: String?, @Query(
            "near"
        ) near: String?
    ): Call<VenuesListResponse?>?

    //MARK: search for venue using lat and lng
    @GET("venues/search")
    fun getVenuesByLocation(
        @Query("v") version: String?, @Query(
            "client_id"
        ) clientID: String?, @Query("client_secret") clientSecret: String?, @Query(
            "categoryId"
        ) placeType: String?, @Query("ll") latLng: String?
    ): Call<VenuesListResponse?>?

    //MARK: get detail of the venue
    @GET("venues/{venue_id}")
    fun getVenueDetail(
        @Path("venue_id") venueID: String?, @Query("v") version: String?, @Query(
            "client_id"
        ) clientID: String?, @Query("client_secret") clientSecret: String?
    ): Call<VenueDetailResponse?>?
}


