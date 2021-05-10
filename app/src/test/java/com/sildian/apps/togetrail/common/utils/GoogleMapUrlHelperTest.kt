package com.sildian.apps.togetrail.common.utils

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Test

class GoogleMapUrlHelperTest {

    @Test
    fun given_LatLng_when_generateWithLatLng_then_checkResult() {
        val latLng = LatLng(45.786, -118.964)
        val expectedResult = "https://www.google.com/maps/search/?api=1&query=45.786,-118.964"
        assertEquals(expectedResult, GoogleMapUrlHelper.generateWithLatLng(latLng))
    }

    @Test
    fun given_address_when_generateWithFullAddress_then_checkResult() {
        val address = "Rue de Rivoli, Paris, France"
        val expectedResult = "https://www.google.com/maps/search/?api=1&query=Rue+de+Rivoli+Paris+France"
        assertEquals(expectedResult, GoogleMapUrlHelper.generateWithFullAddress(address))
    }
}