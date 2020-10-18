package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.sildian.apps.togetrail.common.exceptions.UserLocationException
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Repository for Location
 ************************************************************************************************/

object UserLocationHelper {

    /**
     * Gets the last known user location
     * @param locationProviderClient : the location provider
     * @return the last known location
     * @throws UserLocationException if the user location is unreachable
     */

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
}