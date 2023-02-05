package com.amanpathak.yelprestaurantapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.amanpathak.yelprestaurantapp.helper.Utils
import com.amanpathak.yelprestaurantapp.models.Restaurant
import com.amanpathak.yelprestaurantapp.network.ApiClient
import com.google.android.gms.location.*

class MainViewModel(val appContext : Application) : AndroidViewModel(appContext) {
    val repo = MainRepo(ApiClient(appContext).api)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val currentLocation = MutableLiveData<Location>()
    private var limit = 15
    private var offset = 0;
    val radiusInMeters = MutableLiveData<Int>(1000)
    val restaurantList = MutableLiveData<List<Restaurant>>()
    val showPaginationProgress = MutableLiveData<Boolean>(false)

    val isPagination get() = offset != 0

    private val businessObserver = Observer<List<Restaurant>> {
        showPaginationProgress.value = false
        if(it != null && it.isNotEmpty()){
            sortAsPerDistance(it)
        }
    }

    private fun sortAsPerDistance(newlist : List<Restaurant>) {
        restaurantList.value = newlist.sortedBy { it.distanceInMeter }
    }

    private fun reset(){
        limit = 15
        offset = 0
    }


    init {
        fetchCurrentLocation()
        listeners()
    }

    private fun listeners() {
        repo.businessList.observeForever(businessObserver)
    }


    fun getRestaurantAsPerLocation(){
        repo.loadRestaurant(currentLocation.value!!, limit = limit.toString(), offset = offset.toString(), radius = radiusInMeters.value.toString())
    }

    private fun fetchCurrentLocation() {
        if(checkLocationPermission()){
            fetchLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    requestLocation()
                    return@addOnSuccessListener
                }
                currentLocation.value = location
                repo
            }
    }


    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,5000).apply {
            setWaitForAccurateLocation(true)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                for (location in p0.locations) {
                    currentLocation.value = location
                    //stopLocationUpdates()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }


    private fun checkLocationPermission() : Boolean {
        val locationPermissionGranted = checkSelfPermission(appContext, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(appContext, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if(locationPermissionGranted) {
            return true
        }

        return false
    }

    fun onRadiusSeekBarChange(progress : Int){
        radiusInMeters.value = Utils.getRadiusSelectorValue(progress)
        reset()
        getRestaurantAsPerLocation()
    }

    fun onPagination(){
        showPaginationProgress.value = true
        offset += 15
        getRestaurantAsPerLocation()
    }

    fun onRefresh(){
        showPaginationProgress.value = false
        offset = 0
        getRestaurantAsPerLocation()
    }


}