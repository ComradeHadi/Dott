package com.hadi.restaurantsarround.model

import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("code")
    var code: Int = 0,
    @SerializedName("requestId")
    private val requestId: String? = null
) {

}
