package com.amanpathak.yelprestaurantapp

import android.Manifest
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
import com.amanpathak.yelprestaurantapp.models.Restaurant
import com.amanpathak.yelprestaurantapp.network.ApiClient
import com.amanpathak.yelprestaurantapp.views.RestaurantListRepo
import com.google.android.gms.location.*

class MainViewModel(val appContext : Application) : AndroidViewModel(appContext) {
    val repo = RestaurantListRepo(ApiClient(appContext).api)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val currentLocation = MutableLiveData<Location>()
    val restaurantList = repo.businessList
    private val limit = 15
    private val offset = 0;


    init {
        fetchCurrentLocation()
    }


    fun getRestaurantAsPerLocation(){
        repo.loadRestaurant(currentLocation.value!!, limit = limit.toString(), offset = offset.toString())
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


}