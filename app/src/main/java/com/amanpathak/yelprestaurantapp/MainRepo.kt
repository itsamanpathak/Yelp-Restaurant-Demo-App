package com.amanpathak.yelprestaurantapp

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.amanpathak.yelprestaurantapp.helper.NewYorkLatLon
import com.amanpathak.yelprestaurantapp.models.Restaurant
import com.amanpathak.yelprestaurantapp.network.Api
import com.amanpathak.yelprestaurantapp.network.models.Business
import com.amanpathak.yelprestaurantapp.network.models.BusinessResponse
import retrofit2.Call
import retrofit2.Response

class MainRepo(private val apiClient: Api) {

     val businessList = MutableLiveData<List<Restaurant>>()


     fun loadRestaurant(location : Location, radius : String, limit : String, offset : String, useDefaultLocation : Boolean){
         var lat = 0.0
         var lon = 0.0
         if(useDefaultLocation){
             lat = NewYorkLatLon.lat
             lon = NewYorkLatLon.lon
         }
         else {
             lat = location.latitude
             lon = location.longitude
         }


        apiClient.
        getBusinessAsPerLocation(lat = "$lat", lon = "$lon", radius = radius, limit = limit, offset = offset)
            .enqueue(object : retrofit2.Callback<BusinessResponse> {
                override fun onResponse(
                    call: Call<BusinessResponse>,
                    response: Response<BusinessResponse>
                ) {
                    if (response.isSuccessful) {
                        mapRemoteToLocaleModels(response)
                    }
                }

                override fun onFailure(call: Call<BusinessResponse>, t: Throwable) {

                }
            })
    }
    private fun mapRemoteToLocaleModels(response: Response<BusinessResponse>) {

        if(response.body() == null || response.body()!!.businessList == null){
            return
        }

        val tempRestaurantList = ArrayList<Restaurant>()

        val list : List<Business> = response.body()!!.businessList!!

        for (business in list){

            val restaurant = Restaurant(
                business.id,
                business.name,
                business.distance,
                business.imageUrl,
                business.location?.address1,
                business.isClosed,
                business.rating
            )

            tempRestaurantList.add(restaurant)

        }


        businessList.value = tempRestaurantList

    }


}