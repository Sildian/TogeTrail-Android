package com.sildian.apps.togetrail.common.core.location

import junit.framework.TestCase.*
import org.junit.Test
import kotlin.random.Random

class LocationTest {

    @Test
    fun `GIVEN two countries with difference codes WHEN equals THEN countries are not equals`() {
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
    fun `GIVEN two regions with difference codes WHEN equals THEN regions are not equals`() {
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