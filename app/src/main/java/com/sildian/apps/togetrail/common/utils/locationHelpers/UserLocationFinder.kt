package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/*************************************************************************************************
 * Allows to find the user's last location
 ************************************************************************************************/

/***************************************Definition***********************************************/

interface UserLocationFinder {

    /**
     * Finds the user's last location
     * @return the user's last know location
     * @throws UserLocationException if the request fails
     */

    suspend fun findLastLocation(): Location
}

/************************************Injection module********************************************/

@Module
@InstallIn(SingletonComponent::class)
object UserLocationFinderModule {

    @Singleton
    @Provides
    fun provideRealUserLocationFinder(locationProviderClient: FusedLocationProviderClient): UserLocationFinder =
        RealUserLocationFinder(locationProviderClient)
}

/*********************************Real implementation*******************************************/

class RealUserLocationFinder @Inject constructor(
    private val locationProviderClient: FusedLocationProviderClient
    )
    : UserLocationFinder
{

    @Throws(UserLocationException::class)
    override suspend fun findLastLocation(): Location {
        val userLocation = try {
            locationProviderClient.lastLocation.await()
        } catch (e: SecurityException) {
            throw UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
        } catch (e: Exception) {
            null
        }
        when {
            userLocation != null ->
                return userLocation
            !DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext) ->
                throw UserLocationException(UserLocationException.ErrorCode.GPS_UNAVAILABLE)
            else ->
                throw UserLocationException(UserLocationException.ErrorCode.ERROR_UNKNOWN)
        }
    }
}