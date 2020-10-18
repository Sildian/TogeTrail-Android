package com.sildian.apps.togetrail.trail.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.common.exceptions.UserLocationException
import com.sildian.apps.togetrail.common.utils.GeoUtilities
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationHelper
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import kotlinx.coroutines.*
import java.util.*

/*************************************************************************************************
 * Runs a loop recording a trail using the user location
 * @param context : the context
 ************************************************************************************************/

class TrailRecordExecutor(private val context: Context) {

    companion object {

        /**Logs**/
        private const val TAG = "TrailRecordExecutor"

        /**Record data**/
        //The time interval between each point record (in milliseconds)
        private const val VALUE_RECORD_TIME_INTERVAL = 5000
        //The minimum required distance between two points to check before register (in meters)
        private const val VALUE_RECORD_MIN_DISTANCE = 2
    }

    /*******************************Location provider*******************************************/

    private val userLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    /*******************************Data to be updated*******************************************/

    val trailPointsRegistered = arrayListOf<TrailPoint>()
    var userLocationFailure: UserLocationException? = null ; private set

    /****************************LiveData to be observed*****************************************/
    
    val trailPointsRegisteredLiveData = MutableLiveData<List<TrailPoint>>()
    val userLocationFailureLiveData = MutableLiveData<UserLocationException?>()

    /***********************************Running job**********************************************/

    private var job: Job? = null

    /***********************************Job monitoring*******************************************/

    suspend fun start() {
        withContext(Dispatchers.Main) {
            job = launch { recordTrail() }
        }
    }

    suspend fun stop() {
        withContext(Dispatchers.Main) {
            job?.cancel()
        }
    }

    fun isRecording(): Boolean =
        this.job != null && this.job!!.isActive

    /***********************************Trail recording*******************************************/

    private suspend fun recordTrail() {
        withContext(Dispatchers.IO) {
            while (true) {
                fetchUserLocation()
                var remainingTime= VALUE_RECORD_TIME_INTERVAL
                while(remainingTime > 0){
                    delay(1000)
                    remainingTime -= 1000
                }
            }
        }
    }

    suspend fun fetchUserLocation() {
        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                val userLocation = async {
                    try {
                        UserLocationHelper.getLastUserLocation(userLocationProvider)
                    }
                    catch (e: UserLocationException) {
                        e.printStackTrace()
                        Log.e(TAG, "User location cannot be reached : ${e.message}")
                        userLocationFailure = e
                        userLocationFailureLiveData.postValue(userLocationFailure)
                        null
                    }
                }.await()

                if (userLocation != null) {
                    handleUserLocation(userLocation)
                }
            }
            else {
                userLocationFailure = UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
            }
        }
    }

    private fun handleUserLocation(userLocation: Location) {
        if (checkMinDistanceToPreviousPointIsFulfilled(userLocation)) {
            Log.d(TAG, "Point registered at lat ${userLocation.latitude} lng ${userLocation.longitude}")
            val trailPoint = TrailPoint(
                userLocation.latitude,
                userLocation.longitude,
                userLocation.altitude.toInt(),
                Date()
            )
            trailPointsRegistered.add(trailPoint)
            trailPointsRegisteredLiveData.postValue(trailPointsRegistered)
        } else {
            Log.d(TAG, "Point not registered, too closed to the previous point")
        }
        userLocationFailure = null
        userLocationFailureLiveData.postValue(userLocationFailure)
    }

    private fun checkMinDistanceToPreviousPointIsFulfilled(location: Location): Boolean {
        return if (!this.trailPointsRegistered.isNullOrEmpty()) {
            val previousPoint = this.trailPointsRegistered.lastOrNull()
            if (previousPoint != null) {
                val pointA = LatLng(location.latitude, location.longitude)
                val pointB = LatLng(previousPoint.latitude, previousPoint.longitude)
                GeoUtilities.getDistance(pointA, pointB) >= VALUE_RECORD_MIN_DISTANCE
            } else {
                true
            }
        } else {
            true
        }
    }
}