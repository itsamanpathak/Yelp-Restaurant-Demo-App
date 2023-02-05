package com.amanpathak.yelprestaurantapp.helper

import java.text.DecimalFormat

object Utils {


    fun convertDistanceToReadableForm(meters: Double?) : String{
        if(meters == null) return ""

        val decimalFormat = DecimalFormat("0.#")

        if(meters >= 1000){
            val km =  meters.div(1000)
            return "${decimalFormat.format(km).toString()} km"
        }

        return "${decimalFormat.format(meters).toString()} m"
    }

    fun getRadiusSelectorValue(progress : Int) : Int{
        val value = ((progress/100.0) * 5000).toInt()
        return if(value < 100) return 100 else value
    }
}