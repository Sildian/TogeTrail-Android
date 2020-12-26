package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Provides with methods allowing to query user location
 ************************************************************************************************/

object UserLocationHelper {

    /**
     * Gets the last known user location
     * @param locationProviderClient : the location provider
     * @return the last known location
     * @throws UserLocationException if the user location is unreachable
     */

    @JvmStatic
    @Throws(UserLocationException::class)
    suspend fun getLastUserLocation(locationProviderClient: FusedLocationProviderClient): Location {
        return withContext(Dispatchers.IO) {
            val userLocation = try {
                locationProviderClient.lastLocation.await()
            }
            catch (e: SecurityException) {
                throw UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
            }
            catch (e: Exception) {
                null
            }
            when {
                userLocation != null ->
                    return@withContext userLocation
                !DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext) ->
                    throw UserLocationException(UserLocationException.ErrorCode.GPS_UNAVAILABLE)
                else ->
                    throw UserLocationException(UserLocationException.ErrorCode.ERROR_UNKNOWN)
            }
        }
    }

    /**
     * Requests periodic user location updates
     * @param locationProviderClient : the location provider
     * @param intervalMillis : the interval between each update in millis
     * @param callback : the callback to handle location updates
     * @throws UserLocationException if the user location is unreachable
     */

    @JvmStatic
    @Throws(UserLocationException::class)
    fun startUserLocationUpdates(locationProviderClient: FusedLocationProviderClient,
                                 intervalMillis: Long, callback: LocationCallback)
    {
        if (DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext)) {
            val locationRequest = LocationRequest.create()?.apply {
                interval = intervalMillis
                fastestInterval = intervalMillis
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            try {
                locationProviderClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )
            }
            catch (e: SecurityException) {
                throw UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
            }
            catch (e: Exception) {
                throw UserLocationException(UserLocationException.ErrorCode.ERROR_UNKNOWN)
            }
        }
        else {
            throw UserLocationException(UserLocationException.ErrorCode.GPS_UNAVAILABLE)
        }
    }

    /**
     * Stops periodic user location updates
     * @param locationProviderClient : the location provider
     * @param callback : the callback handling location updates
     */

    @JvmStatic
    fun stopUserLocationUpdates(locationProviderClient: FusedLocationProviderClient, callback: LocationCallback) {
        locationProviderClient.removeLocationUpdates(callback)
    }
}