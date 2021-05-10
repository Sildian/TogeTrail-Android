package com.sildian.apps.togetrail.common.utils

import com.google.android.gms.maps.model.LatLng

/*************************************************************************************************
 * Generates URL allowing to launch Google Map and point to specific destination
 ************************************************************************************************/

object GoogleMapUrlHelper {

    private const val URL_BASE = "https://www.google.com/maps"
    private const val URL_SEARCH = "/search/?api=1"
    private const val URL_PARAM_NAME_QUERY = "query"

    /**
     * Point to a destination defined by latitude / longitude
     * @param destination : the latitude / longitude
     * @return a string containing the URL
     */

    @JvmStatic
    fun generateWithLatLng(destination: LatLng): String {
        val formattedDestination = "${destination.latitude},${destination.longitude}"
        return "$URL_BASE$URL_SEARCH&$URL_PARAM_NAME_QUERY=$formattedDestination"
    }

    /**
     * Point to a destination defined by a full address
     * @param destination : the full address
     * @return a string containing the URL
     */

    @JvmStatic
    fun generateWithFullAddress(destination: String): String {
        val formattedDestination = destination
            .replace(",", "")
            .replace(" ", "+")
        return "$URL_BASE$URL_SEARCH&$URL_PARAM_NAME_QUERY=$formattedDestination"
    }
}