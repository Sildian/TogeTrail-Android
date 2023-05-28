package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.common.core.nextString
import com.sildian.apps.togetrail.features.entities.trail.nextTrailUI
import com.sildian.apps.togetrail.repositories.database.entities.trail.nextTrail
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class TrailMappersTest {

    @Test
    fun `GIVEN TrailUI WHEN invoking toDataModel THEN result is correct Trail`() {
        // Given
        val trailUI = Random.nextTrailUI()

        // When
        val trail = trailUI.toDataModel()

        // Then
        assertEquals(trailUI.id, trail.id)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN trail without id WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trail = Random.nextTrail(id = null)

        // When
        trail.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN trail without name WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trail = Random.nextTrail(name = null)

        // When
        trail.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN trail without position WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trail = Random.nextTrail(position = null)

        // When
        trail.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN trail without location WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trail = Random.nextTrail(location = null)

        // When
        trail.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN trail without description WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trail = Random.nextTrail(description = null)

        // When
        trail.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN trail without level WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val trail = Random.nextTrail(level = null)

        // When
        trail.toUIModel(currentUserId = Random.nextString())
    }

    @Test
    fun `GIVEN valid trail WHEN invoking toUIModel THEN result is correct trailUI`() {
        // Given
        val trail = Random.nextTrail()

        // When
        val trailUI = trail.toUIModel(currentUserId = Random.nextString())

        // Then
        assertEquals(trail.id, trailUI.id)
    }

    @Test
    fun `GIVEN valid Trail and not logged user WHEN invoking toDataModel THEN result is correct TrailUI with false isCurrentUserAuthor`() {
        // Given
        val trail = Random.nextTrail()

        // When
        val trailUI = trail.toUIModel(currentUserId = null)

        // Then
        assertFalse(trailUI.isCurrentUserAuthor)
    }

    @Test
    fun `GIVEN valid Trail and random user WHEN invoking toDataModel THEN result is correct TrailUI with false isCurrentUserAuthor`() {
        // Given
        val trail = Random.nextTrail()

        // When
        val trailUI = trail.toUIModel(currentUserId = Random.nextString().replace(trail.authorId.toString(), ""))

        // Then
        assertFalse(trailUI.isCurrentUserAuthor)
    }

    @Test
    fun `GIVEN valid Trail and current user WHEN invoking toDataModel THEN result is correct TrailUI with true isCurrentUserAuthor`() {
        // Given
        val trail = Random.nextTrail()

        // When
        val trailUI = trail.toUIModel(currentUserId = trail.authorId)

        // Then
        assertTrue(trailUI.isCurrentUserAuthor)
    }
}