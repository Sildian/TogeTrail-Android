package com.sildian.apps.togetrail.common.utils

import com.google.android.gms.maps.model.LatLng
import org.junit.Test

import org.junit.Assert.*

class GeoUtilitiesTest {

    @Test
    fun given_2points_when_getDistance_then_checkResultIs199872() {
        val pointA= LatLng(40.0, -5.0)
        val pointB= LatLng(41.261388, -3.3125)
        val distance=GeoUtilities.getDistance(pointA, pointB)
        assertEquals(199872, distance)
    }

    @Test
    fun given_lat50lng50_when_getBoundsAroundOriginPoint_then_checkResultIsInTheCorrectRange(){
        val originPoint=LatLng(50.0, 50.0)
        val bounds=GeoUtilities.getBoundsAroundOriginPoint(originPoint)
        assertEquals(LatLng(49.5, 49.5), bounds.southwest)
        assertEquals(LatLng(50.5, 50.5), bounds.northeast)
    }

    @Test
    fun given_lat89p8lng50_when_getBoundsAroundOriginPoint_then_checkResultIsInTheCorrectRange(){
        val originPoint=LatLng(89.8, 50.0)
        val bounds=GeoUtilities.getBoundsAroundOriginPoint(originPoint)
        assertEquals(LatLng(89.3, 49.5), bounds.southwest)
        assertEquals(LatLng(90.0, 50.5), bounds.northeast)
    }

    @Test
    fun given_lat50lngm179p8_when_getBoundsAroundOriginPoint_then_checkResultIsInTheCorrectRange(){
        val originPoint=LatLng(50.0, -179.8)
        val bounds=GeoUtilities.getBoundsAroundOriginPoint(originPoint)
        assertEquals(LatLng(49.5, -180.0), bounds.southwest)
        assertEquals(LatLng(50.5, -179.3), bounds.northeast)
    }
}