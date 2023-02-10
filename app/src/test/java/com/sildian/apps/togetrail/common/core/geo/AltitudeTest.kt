package com.sildian.apps.togetrail.common.core.geo

import org.junit.Assert.*
import org.junit.Test
import java.text.NumberFormat
import kotlin.random.Random

class AltitudeTest {

    @Test
    fun `GIVEN altitude WHEN invoking toString THEN result is formatted string with meters`() {
        //Given
        val altitude = Random.nextAltitude()

        //When
        val display = altitude.toString()

        //Then
        val expectedResult = NumberFormat.getInstance().format(altitude.meters) + " m"
        assertEquals(expectedResult, display)
    }

    @Test
    fun `GIVEN altitudes A and B WHEN invoking derivationTo THEN result is derivation from A to B`() {
        //Given
        val altitudeA = Random.nextAltitude()
        val altitudeB = Random.nextAltitude()

        //When
        val derivation = altitudeA.derivationTo(altitude = altitudeB)

        //Then
        val expectedResult = Derivation(meters = altitudeB.meters - altitudeA.meters)
        assertEquals(expectedResult, derivation)
    }

    @Test
    fun `GIVEN altitudes A and B WHEN invoking derivationFrom THEN result is derivation from B to A`() {
        //Given
        val altitudeA = Random.nextAltitude()
        val altitudeB = Random.nextAltitude()

        //When
        val derivation = altitudeA.derivationFrom(altitude = altitudeB)

        //Then
        val expectedResult = Derivation(meters = altitudeA.meters - altitudeB.meters)
        assertEquals(expectedResult, derivation)
    }
}