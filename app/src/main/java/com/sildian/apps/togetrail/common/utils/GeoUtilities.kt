package com.sildian.apps.togetrail.common.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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

    @JvmStatic
    fun getDistance(pointA:LatLng, pointB:LatLng):Int{
        val earthRadius=6371.0
        val arg1:Double= sin(Math.toRadians(pointA.latitude)) * sin(Math.toRadians(pointB.latitude))
        val arg2:Double= cos(Math.toRadians(pointA.latitude)) * cos(Math.toRadians(pointB.latitude)) *
                cos(Math.toRadians(pointB.longitude-pointA.longitude))
        val distanceInKMeters:Double=earthRadius* acos(arg1+arg2)
        return (distanceInKMeters*1000).toInt()
    }

    /**
     * Gets bounds around the given origin point
     * Removes 0.5 from latitude and longitude to calculate southWest point
     * Adds 0.5 to latitude and longitude to calculate northEast point
     * @param originPoint : the origin point (center)
     * @return a bound
     */

    @JvmStatic
    fun getBoundsAroundOriginPoint(originPoint:LatLng):LatLngBounds{
        val westLng=if(originPoint.longitude<=-179.5) -180.0 else originPoint.longitude-0.5
        val eastLng=if(originPoint.longitude>=179.5) 180.0 else originPoint.longitude+0.5
        val southWest=LatLng(originPoint.latitude-0.5, westLng)
        val northEast=LatLng(originPoint.latitude+0.5, eastLng)
        return LatLngBounds.Builder()
            .include(southWest)
            .include(northEast)
            .build()
    }
}