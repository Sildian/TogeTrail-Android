package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.features.entities.trail.nextTrailPointUI
import com.sildian.apps.togetrail.repositories.database.entities.trail.nextTrailPoint
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class TrailPointMappersTest {

    @Test
    fun `GIVEN TrailPointUI WHEN invoking toDataModel THEN result is correct TrailPoint`() {
        // Given
        val trailPointUI = Random.nextTrailPointUI()

        // When
        val trailPoint = trailPointUI.toDataModel()

        // Then
        assertEquals(trailPointUI.number, trailPoint.number)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN TrailPoint without number WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trailPoint = Random.nextTrailPoint(number = null)

        // When
        trailPoint.toUIModel()
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN TrailPoint without position WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trailPoint = Random.nextTrailPoint(position = null)

        // When
        trailPoint.toUIModel()
    }

    @Test
    fun `GIVEN valid TrailPoint WHEN invoking toUIModel THEN result is correct TrailPointUI`() {
        // Given
        val trailPoint = Random.nextTrailPoint()

        // When
        val trailPointUI = trailPoint.toUIModel()

        // Then
        assertEquals(trailPoint.number, trailPointUI.number)
    }
}