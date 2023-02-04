package com.amanpathak.yelprestaurantapp.views

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.amanpathak.yelprestaurantapp.models.Restaurant
import com.amanpathak.yelprestaurantapp.network.Api
import com.amanpathak.yelprestaurantapp.network.ApiClient
import com.amanpathak.yelprestaurantapp.network.models.Business
import com.amanpathak.yelprestaurantapp.network.models.BusinessResponse
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class RestaurantListRepo(private val apiClient: Api) {
     val businessList = MutableLiveData<List<Restaurant>>()



     fun loadRestaurant(location : Location, limit : String, offset : String){

         apiClient.
        getBusinessAsPerLocation(lat = "${location.latitude}", lon = "${location.longitude}", limit = limit, offset = offset)
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