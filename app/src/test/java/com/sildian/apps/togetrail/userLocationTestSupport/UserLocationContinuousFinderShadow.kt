package com.sildian.apps.togetrail.userLocationTestSupport

import android.Manifest
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationContinuousFinder
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsHelper

/*************************************************************************************************
 * Fake UserLocationContinuousFinder for tests
 ************************************************************************************************/

class UserLocationContinuousFinderShadow(locationProviderClient: FusedLocationProviderClient)
    : UserLocationContinuousFinder(locationProviderClient)
{

    var lastUserLocation: Location? = null

    override fun startLocationUpdates(intervalMillis: Long, callback: LocationCallback) {
        println("FAKE UserLocationContinuousFinder : Start location updates")
        when {
            !(PermissionsHelper.isPermissionGranted(locationProviderClient.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) ->
                throw UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
            !DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext) ->
                throw UserLocationException(UserLocationException.ErrorCode.GPS_UNAVAILABLE)
            else ->
                callback.onLocationResult(LocationResult.create(listOf(lastUserLocation)))
        }
    }

    override fun stopLocationUpdates(callback: LocationCallback) {
        println("FAKE UserLocationContinuousFinder : Stop location updates")
    }
}