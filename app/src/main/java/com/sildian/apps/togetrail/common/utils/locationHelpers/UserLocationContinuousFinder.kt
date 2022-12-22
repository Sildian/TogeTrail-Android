package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
interface UserLocationContinuousFinderModule {

    @Singleton
    @Binds
    fun bindRealUserLocationContinuousFinder(
        userLocationContinuousFinder: RealUserLocationContinuousFinder
    ): UserLocationContinuousFinder
}

/*********************************Real implementation*******************************************/

class RealUserLocationContinuousFinder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationProviderClient: FusedLocationProviderClient
    )
    : UserLocationContinuousFinder
{

    @Throws(UserLocationException::class)
    override fun startLocationUpdates(intervalMillis: Long, callback: LocationCallback) {
        if (DeviceUtilities.isGpsAvailable(context)) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                intervalMillis
            )
                .setMinUpdateIntervalMillis(intervalMillis)
                .build()
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