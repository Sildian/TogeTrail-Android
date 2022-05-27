package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/*************************************************************************************************
 * Allows to continuously update the user location
 ************************************************************************************************/

/***************************************Definition***********************************************/

interface UserLocationContinuousFinder {

    /**
     * Starts location updates
     * @throws UserLocationException if the request fails
     */

    fun startLocationUpdates(intervalMillis: Long, callback: LocationCallback)

    /**
     * Stops location updates
     */

    fun stopLocationUpdates(callback: LocationCallback)
}

/************************************Injection module********************************************/

@Module
@InstallIn(SingletonComponent::class)
object UserLocationContinuousFinderModule {

    @Singleton
    @Provides
    fun provideRealUserLocationContinuousFinder(locationProviderClient: FusedLocationProviderClient): UserLocationContinuousFinder =
        RealUserLocationContinuousFinder(locationProviderClient)
}

/*********************************Real implementation*******************************************/

class RealUserLocationContinuousFinder @Inject constructor(
    private val locationProviderClient: FusedLocationProviderClient
    )
    : UserLocationContinuousFinder
{

    @Throws(UserLocationException::class)
    override fun startLocationUpdates(intervalMillis: Long, callback: LocationCallback) {
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

    override fun stopLocationUpdates(callback: LocationCallback) {
        this.locationProviderClient.removeLocationUpdates(callback)
    }
}