package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/*************************************************************************************************
 * Allows to find the user's last location
 ************************************************************************************************/

@Singleton
open class UserLocationFinder @Inject constructor(
    protected val locationProviderClient: FusedLocationProviderClient)
{

    @Throws(UserLocationException::class)
    open suspend fun findLastLocation(): Location {
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