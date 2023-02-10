package com.sildian.apps.togetrail.common.core.geo

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.random.Random

class DistanceTest {

     @Test
     fun `GIVEN distance below 1000 m WHEN invoking toString THEN result is formatted string with meters`() {
         //Given
         val distance = Distance(meters = Random.nextInt(from = 0, until = 1000))

         //When
         val display = distance.toString()

         //Then
         val expectedResult = NumberFormat.getInstance().format(distance.meters) + " m"
         assertEquals(expectedResult, display)
     }

    @Test
    fun `GIVEN distance above 1000 m WHEN invoking toString THEN result is formatted string with kilometers`() {
        //Given
        val distance = Distance(meters = Random.nextInt(from = 1000, until = 10000))

        //When
        val display = distance.toString()

        //Then
        val expectedResult =
            DecimalFormat.getInstance().apply {
                maximumFractionDigits = 1
            }.format(distance.kilometers) + " km"
        assertEquals(expectedResult, display)
    }

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