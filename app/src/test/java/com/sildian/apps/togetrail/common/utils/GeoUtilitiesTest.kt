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
}