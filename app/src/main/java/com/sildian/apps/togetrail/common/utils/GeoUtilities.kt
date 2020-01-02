package com.sildian.apps.togetrail.common.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/*************************************************************************************************
 * Provides with some functions related to geographical data
 ************************************************************************************************/

object GeoUtilities {

    /**
     * Gets the distance between two geographical points (in meters)
     * @param pointA : the first point
     * @param pointB : the second point
     * @return the distance (in meters)
     */

    fun getDistance(pointA:LatLng, pointB:LatLng):Int{
        val earthRadius=6371.0
        val arg1:Double= sin(Math.toRadians(pointA.latitude)) * sin(Math.toRadians(pointB.latitude))
        val arg2:Double= cos(Math.toRadians(pointA.latitude)) * cos(Math.toRadians(pointB.latitude)) *
                cos(Math.toRadians(pointB.longitude-pointA.longitude))
        val distanceInKMeters:Double=earthRadius* acos(arg1+arg2)
        return (distanceInKMeters*1000).toInt()
    }
}