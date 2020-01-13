package com.hadi.restaurantsarround.model


import com.google.gson.annotations.SerializedName

open class Photo  {
    @SerializedName("prefix")
    var prefix: String? = null
    @SerializedName("suffix")
    var suffix: String? = null


    fun getImageBySize(size: String): String {
        return prefix + size + suffix
    }

}