package com.sildian.apps.togetrail.common.core.location

import junit.framework.TestCase.*
import org.junit.Test
import kotlin.random.Random

class LocationTest {

    @Test
    fun `GIVEN location with country and region WHEN invoking toString THEN result is region and country names`() {
        // Given
        val country = Random.nextCountry()
        val region = Random.nextRegion()
        val location = Random.nextLocation(country = country, region = region)

        // When
        val display = location.toString()

        // Then
        val expectedResult = "${region.name}, ${country.name}"
        assertEquals(expectedResult, display)
    }

    @Test
    fun `GIVEN location with country WHEN invoking toString THEN result is country name`() {
        // Given
        val country = Random.nextCountry()
        val location = Random.nextLocation(country = country, region = null)

        // When
        val display = location.toString()

        // Then
        val expectedResult = country.name
        assertEquals(expectedResult, display)
    }

    @Test
    fun `GIVEN location without country WHEN invoking toString THEN result is fullAddress`() {
        // Given
        val location = Random.nextLocation(country = null)

        // When
        val display = location.toString()

        // Then
        val expectedResult = location.fullAddress
        assertEquals(expectedResult, display)
    }

    @Test
    fun `GIVEN two countries with different codes WHEN equals THEN countries are not equals`() {
        // Given
        val countryA = Random.nextCountry(code = "A")
        val countryB = Random.nextCountry(code = "B")

        // When Then
        assertFalse(countryA == countryB)
    }

    @Test
    fun `GIVEN two countries with same code WHEN equals THEN countries are equals`() {
        // Given
        val countryA = Random.nextCountry(code = "C")
        val countryB = Random.nextCountry(code = "C")

        // When Then
        assertTrue(countryA == countryB)
    }

    @Test
    fun `GIVEN two regions with different codes WHEN equals THEN regions are not equals`() {
        // Given
        val regionA = Random.nextRegion(code = "A")
        val regionB = Random.nextRegion(code = "B")

        // When Then
        assertFalse(regionA == regionB)
    }

    @Test
    fun `GIVEN two regions with same code WHEN equals THEN regions are equals`() {
        // Given
        val regionA = Random.nextRegion(code = "C")
        val regionB = Random.nextRegion(code = "C")

        // When Then
        assertTrue(regionA == regionB)
    }
}