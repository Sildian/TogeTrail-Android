package com.sildian.apps.togetrail.trail.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.common.utils.GeoUtilities
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationContinuousFinder
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import java.util.*
import javax.inject.Inject

/*************************************************************************************************
 * Records a Trail in real time using the user location
 ************************************************************************************************/

class TrailRecordExecutor @Inject constructor(
    private val userLocationContinuousFinder: UserLocationContinuousFinder
)
    : LocationCallback()
{

    companion object {

        /**Logs**/
        private const val TAG = "TrailRecordExecutor"

        /**Record data**/
        //The time interval between each point record (in milliseconds)
        private const val VALUE_RECORD_TIME_INTERVAL = 5000
        //The minimum required distance between two points to check before register (in meters)
        private const val VALUE_RECORD_MIN_DISTANCE = 2
    }

    /*************************************Data**************************************************/

    var isRecording = false ; private set

    /*******************************Data to be updated*******************************************/

    val trailPointsRegistered = arrayListOf<TrailPoint>()
    var userLocationFailure: UserLocationException? = null ; private set

    /****************************LiveData to be observed*****************************************/
    
    val trailPointsRegisteredLiveData = MutableLiveData<List<TrailPoint>>()
    val userLocationFailureLiveData = MutableLiveData<UserLocationException?>()

    /***********************************Job monitoring*******************************************/

    fun start() {
        try {
            isRecording = true
            this.userLocationContinuousFinder.startLocationUpdates(VALUE_RECORD_TIME_INTERVAL.toLong(), this)
        }
        catch (e: UserLocationException) {
            stop()
            e.printStackTrace()
            Log.e(TAG, "User location cannot be reached : ${e.message}")
            userLocationFailure = e
            userLocationFailureLiveData.postValue(userLocationFailure)
        }
    }

    fun stop() {
        isRecording = false
        this.userLocationContinuousFinder.stopLocationUpdates(this)
    }

    /***********************************Trail recording*******************************************/

    override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult != null) {
            for (location in locationResult.locations) {
                if (location != null) {
                    handleUserLocation(location)
                }
                else {
                    userLocationFailure = UserLocationException(UserLocationException.ErrorCode.ERROR_UNKNOWN)
                    userLocationFailureLiveData.postValue(userLocationFailure)
                }
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