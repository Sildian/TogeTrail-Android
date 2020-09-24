package com.sildian.apps.togetrail.common.utils

import android.content.Context
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat

/*************************************************************************************************
 * Provides with some functions allowing to manipulate the device
 ************************************************************************************************/

object DeviceUtilities {

    /**
     * Check if Gps is available
     * @param context : the context
     * @return true if the gps is activated, false otherwise
     */

    @JvmStatic
    fun isGpsAvailable(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }
}