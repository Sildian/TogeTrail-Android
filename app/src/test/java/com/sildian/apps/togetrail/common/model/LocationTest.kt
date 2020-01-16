package com.sildian.apps.togetrail.common.model

import org.junit.Test

import org.junit.Assert.*

class LocationTest {

    @Test
    fun given_FranceSavoieChamonix_when_getFullLocation_then_checkResult() {
        val location=Location("France", "Savoie", "Chamonix")
        val expectedResult="Chamonix, Savoie, France"
        assertEquals(expectedResult, location.getFullLocation())
    }

    @Test
    fun given_France_when_getFullLocation_then_checkResult() {
        val location=Location("France")
        val expectedResult="France"
        assertEquals(expectedResult, location.getFullLocation())
    }

    @Test
    fun given_Nothing_when_getFullLocation_then_checkResult() {
        val location=Location()
        assertNull(location.getFullLocation())
    }
}