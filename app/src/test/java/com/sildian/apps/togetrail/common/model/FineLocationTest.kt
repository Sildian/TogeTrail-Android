package com.sildian.apps.togetrail.common.model

import org.junit.Test

import org.junit.Assert.*

class FineLocationTest {

    @Test
    fun given_FranceSavoieChamonix1rueDeLaMontagne_when_getFullLocation_then_checkResult() {
        val location=FineLocation("France", "Savoie", "Chamonix", "1 rue de la Montagne")
        val expectedResult="1 rue de la Montagne\nChamonix\nSavoie\nFrance"
        assertEquals(expectedResult, location.getFullLocation())
    }

    @Test
    fun given_Nothing_when_getFullLocation_then_checkResult() {
        val location=FineLocation()
        assertNull(location.getFullLocation())
    }
}