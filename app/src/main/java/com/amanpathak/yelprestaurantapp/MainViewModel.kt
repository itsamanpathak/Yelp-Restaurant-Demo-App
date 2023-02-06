package com.amanpathak.yelprestaurantapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.amanpathak.yelprestaurantapp.helper.SingleEvent
import com.amanpathak.yelprestaurantapp.helper.Utils
import com.amanpathak.yelprestaurantapp.models.Restaurant
import com.amanpathak.yelprestaurantapp.network.ApiClient
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val appContext : Application) : AndroidViewModel(appContext) {
    private val repo = MainRepo(ApiClient(appContext).api)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_REQUEST_CODE = 1
    private var limit = 15
    private var offset = 0;

    val currentLocation = MutableLiveData<Location>()
    val radiusInMeters = MutableLiveData<Int>(1000)
    val restaurantList = MutableLiveData<List<Restaurant>>(listOf())
    val showPaginationProgress = MutableLiveData<Boolean>(false)
    var event = SingleEvent<Event>()

    var locationName = MutableLiveData<String>(appContext.getString(R.string.based_on_your_location))
    var shouldUseDefaultLocation = MutableLiveData<Boolean>(false)
    var showLoadingMessage = MutableLiveData<Boolean>(true)
    var loadingMessage = MutableLiveData<String>(appContext.getString(R.string.fetching_location))

    lateinit var permissionResultLauncher : ActivityResultLauncher<Array<String>>
    val isPagination get() = offset != 0

    private val businessObserver = Observer<List<Restaurant>> {
        showPaginationProgress.value = false
        showLoadingMessage.value = false
        if(it != null && it.isNotEmpty()){
            sortAsPerDistance(it)
        }
        else if(it != null && it.isEmpty() && restaurantList.value?.isEmpty() == true){
            showLoadingMessage.value = true
            loadingMessage.value = appContext.getString(R.string.default_location_message)
            locationName.value = appContext.getString(R.string.newyork)
            shouldUseDefaultLocation.value = true
            fetchRestaurantAsPerLocation()
        }
    }

    private fun sortAsPerDistance(newList : List<Restaurant>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                restaurantList.postValue(newList.sortedBy { it.distanceInMeter })
            }

        }

    }

    private fun reset(){
        limit = 15
        offset = 0
    }


    init {
        showLoadingMessage.value = true
        loadingMessage.value = appContext.getString(R.string.fetching_location)
        fetchCurrentLocation()
        listeners()
    }

    private fun listeners() {
        repo.businessList.observeForever(businessObserver)
    }


    fun fetchRestaurantAsPerLocation(){
        repo.loadRestaurant(currentLocation.value!!, limit = limit.toString(), offset = offset.toString(), radius = radiusInMeters.value.toString(), useDefaultLocation = shouldUseDefaultLocation.value == true)
    }

    fun fetchCurrentLocation() {
        if(checkLocationPermission()){
            return fetchLocation()
        }

        requestPermission(listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), REQUEST_LOCATION_REQUEST_CODE)
    }

    private fun requestPermission(permissionList : List<String>, requestCode : Int) {
        event.value = Event.RequestPermission(permissionList, requestCode)
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
                    stopLocationUpdates()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    private fun stopLocationUpdates() {

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
        fetchRestaurantAsPerLocation()
    }

    fun onPagination(){
        showPaginationProgress.value = true
        offset += 15
        fetchRestaurantAsPerLocation()
    }

    fun onRefresh(){
        showPaginationProgress.value = false
        offset = 0
        fetchRestaurantAsPerLocation()
    }

    sealed class Event {
        data class RequestPermission(val permissions : List<String>, val requestCode : Int) : Event()
    }


}