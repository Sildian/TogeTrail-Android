package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import javax.inject.Inject
import javax.inject.Singleton

/*************************************************************************************************
 * Allows to continuously update the user location
 ************************************************************************************************/

@Singleton
open class UserLocationContinuousFinder @Inject constructor(
    protected val locationProviderClient: FusedLocationProviderClient)
{

    @Throws(UserLocationException::class)
    open fun startLocationUpdates(intervalMillis: Long, callback: LocationCallback) {
        if (DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext)) {
            val locationRequest = LocationRequest.create().apply {
                interval = intervalMillis
                fastestInterval = intervalMillis
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            try {
                this.locationProviderClient.requestLocationUpdates(
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

    open fun stopLocationUpdates(callback: LocationCallback) {
        this.locationProviderClient.removeLocationUpdates(callback)
    }
}