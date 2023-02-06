package com.amanpathak.yelprestaurantapp.helper

import java.text.DecimalFormat

object Utils {
    private const val MINIMUM_DISTANCE = 100
    private const val MAXIMUM_DISTANCE = 5000
    private const val ONE_KILO_METER = 1000


    fun convertDistanceToReadableForm(meters: Double?) : String{
        if(meters == null) return ""

        val decimalFormat = DecimalFormat("0.#")

        if(meters >= ONE_KILO_METER){
            val km =  meters.div(ONE_KILO_METER)
            return "${decimalFormat.format(km).toString()} km"
        }

        return "${decimalFormat.format(meters).toString()} m"
    }

    fun getRadiusSelectorValue(progress : Int) : Int{
        val value = ((progress/100.0) * MAXIMUM_DISTANCE).toInt()
        return if(value < MINIMUM_DISTANCE) return MINIMUM_DISTANCE else value
    }
}