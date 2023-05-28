package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.features.entities.trail.nextTrailPointOfInterestUI
import com.sildian.apps.togetrail.repositories.database.entities.trail.nextTrailPointOfInterest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class TrailPointOfInterestMappersTest {

    @Test
    fun `GIVEN TrailPointOfInterestUI WHEN invoking toDataModel THEN result is correct TrailPointOfInterest`() {
        // Given
        val trailPointOfInterestUI = Random.nextTrailPointOfInterestUI()

        // When
        val trailPointOfInterest = trailPointOfInterestUI.toDataModel()

        // Then
        assertEquals(trailPointOfInterestUI.number, trailPointOfInterest.number)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN TrailPointOfInterest without number WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trailPointOfInterest = Random.nextTrailPointOfInterest(number = null)

        // When
        trailPointOfInterest.toUIModel()
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN TrailPointOfInterest without position WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trailPointOfInterest = Random.nextTrailPointOfInterest(position = null)

        // When
        trailPointOfInterest.toUIModel()
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN TrailPointOfInterest without name WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trailPointOfInterest = Random.nextTrailPointOfInterest(name = null)

        // When
        trailPointOfInterest.toUIModel()
    }

    @Test
    fun `GIVEN valid TrailPointOfInterest WHEN invoking toUIModel THEN result is correct TrailPointOfInterestUI`() {
        // Given
        val trailPointOfInterest = Random.nextTrailPointOfInterest()

        // When
        val trailPointOfInterestUI = trailPointOfInterest.toUIModel()

        // Then
        assertEquals(trailPointOfInterest.number, trailPointOfInterestUI.number)
    }
}