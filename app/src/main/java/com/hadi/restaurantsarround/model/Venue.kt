package com.hadi.restaurantsarround.model

class Venue  {
    var id: String? = null
        private set
    var name: String? = null
        private set
    var location: Location? = null
    var bestPhoto: Photo? = null
        private set

    override fun toString(): String {
        return name.toString()
    }
}