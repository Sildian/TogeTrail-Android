package com.sildian.apps.togetrail.common.core.geo

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class DistanceTest {

    @Test
    fun `GIVEN two distances WHEN invoking plus operator THEN result is sum of the two distances`() {
        //Given
        val distanceA = Random.nextDistance()
        val distanceB = Random.nextDistance()

        //When
        val result = distanceA + distanceB

        //Then
        val expectedResult = Distance(meters = distanceA.meters + distanceB.meters)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN two distances WHEN invoking minus operator THEN result is difference between the two distances`() {
        //Given
        val distanceA = Random.nextDistance()
        val distanceB = Random.nextDistance()

        //When
        val result = distanceA - distanceB

        //Then
        val expectedResult = Distance(meters = distanceA.meters - distanceB.meters)
        assertEquals(expectedResult, result)
    }
}