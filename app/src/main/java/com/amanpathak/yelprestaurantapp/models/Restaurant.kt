package com.amanpathak.yelprestaurantapp.models

data class Restaurant(
    val id : String?,
    val name : String?,
    val distanceInMeter : Double?,
    val imageUrl : String?,
    val address : String?,
    val isClosed : Boolean?,
    val rating : Float?
    )