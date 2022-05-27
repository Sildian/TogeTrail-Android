package com.sildian.apps.togetrail.userLocationTestSupport

import android.Manifest
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationFinder
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsHelper

/*************************************************************************************************
 * Fake UserLocationFinder for tests
 ************************************************************************************************/

class FakeUserLocationFinder(private val locationProviderClient: FusedLocationProviderClient) : UserLocationFinder {

    override suspend fun findLastLocation(): Location {
        println("FAKE UserLocationHelper : Get last user location")
        when {
            !(PermissionsHelper.isPermissionGranted(locationProviderClient.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) ->
                throw UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
            !DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext) ->
                throw UserLocationException(UserLocationException.ErrorCode.GPS_UNAVAILABLE)
            UserLocationSimulator.lastLocation != null ->
                return UserLocationSimulator.lastLocation!!
            else ->
                throw UserLocationException(UserLocationException.ErrorCode.ERROR_UNKNOWN)
        }
    }
}