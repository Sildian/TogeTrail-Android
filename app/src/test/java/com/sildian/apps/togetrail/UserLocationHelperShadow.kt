package com.sildian.apps.togetrail

import android.Manifest
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationHelper
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsHelper
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(UserLocationHelper::class)
class UserLocationHelperShadow {

    companion object {

        var lastUserLocation: Location? = null

        @Implementation
        @JvmStatic
        suspend fun getLastUserLocation(locationProviderClient: FusedLocationProviderClient): Location {
            println("FAKE UserLocationHelper : Get last user location")
            when {
                !(PermissionsHelper.isPermissionGranted(locationProviderClient.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) ->
                    throw UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
                !DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext) ->
                    throw UserLocationException(UserLocationException.ErrorCode.GPS_UNAVAILABLE)
                lastUserLocation != null ->
                    return lastUserLocation!!
                else ->
                    throw UserLocationException(UserLocationException.ErrorCode.ERROR_UNKNOWN)
            }
        }

        @Implementation
        @JvmStatic
        fun startUserLocationUpdates(
            locationProviderClient: FusedLocationProviderClient,
            intervalMillis: Long, callback: LocationCallback
        ) {
            println("FAKE UserLocationHelper : Start user location updates")
            when {
                !(PermissionsHelper.isPermissionGranted(locationProviderClient.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) ->
                    throw UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
                !DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext) ->
                    throw UserLocationException(UserLocationException.ErrorCode.GPS_UNAVAILABLE)
                else ->
                    callback.onLocationResult(LocationResult.create(listOf(lastUserLocation)))
            }
        }

        @Implementation
        @JvmStatic
        fun stopUserLocationUpdates(locationProviderClient: FusedLocationProviderClient, callback: LocationCallback) {
            println("FAKE UserLocationHelper : Stop user location updates")
        }
    }
}