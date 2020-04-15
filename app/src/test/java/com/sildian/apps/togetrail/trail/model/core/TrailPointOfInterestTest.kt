package com.sildian.apps.togetrail.trail.model.core

import org.junit.Test

import org.junit.Assert.*

class TrailPointOfInterestTest {

    @Test
    fun given_validTrailPoi_when_isDataValid_then_checkResultIsTrue() {
        val trailPointOfInterest=TrailPointOfInterest(
            latitude = 50.0,
            longitude = 50.0,
            name="Beautiful view"
        )
        assertTrue(trailPointOfInterest.isDataValid())
    }

    @Test
    fun given_NonValidTrailPoi_when_isDataValid_then_checkResultIsFalse() {
        val trailPointOfInterest=TrailPointOfInterest(
            latitude = 50.0,
            longitude = 50.0
        )
        assertFalse(trailPointOfInterest.isDataValid())
    }
}