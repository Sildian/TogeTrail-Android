package com.sildian.apps.togetrail

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.sildian.apps.togetrail.common.exceptions.UserLocationException
import com.sildian.apps.togetrail.common.utils.DeviceUtilities
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationHelper
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(UserLocationHelper::class)
class UserLocationHelperShadow {

    companion object {
        var lastUserLocation: Location? = null
    }

    @Implementation
    suspend fun getLastUserLocation(locationProviderClient: FusedLocationProviderClient): Location {
        println("FAKE UserLocationHelper : Get last user location")
        when {
            lastUserLocation != null ->
                return lastUserLocation!!
            !(Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(locationProviderClient.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ->
                throw UserLocationException(UserLocationException.ErrorCode.ACCESS_NOT_GRANTED)
            !DeviceUtilities.isGpsAvailable(locationProviderClient.applicationContext) ->
                throw UserLocationException(UserLocationException.ErrorCode.GPS_UNAVAILABLE)
            else ->
                throw UserLocationException(UserLocationException.ErrorCode.ERROR_UNKNOWN)
        }
    }
}