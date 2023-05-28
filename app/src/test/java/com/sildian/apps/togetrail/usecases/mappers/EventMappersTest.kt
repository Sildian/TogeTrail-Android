package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.common.core.nextString
import com.sildian.apps.togetrail.features.entities.event.nextEventUI
import com.sildian.apps.togetrail.repositories.database.entities.event.nextEvent
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class EventMappersTest {

    @Test
    fun `GIVEN EventUI WHEN invoking toDataModel THEN result is correct Event`() {
        // Given
        val eventUI = Random.nextEventUI()

        // When
        val event = eventUI.toDataModel()

        // Then
        assertEquals(eventUI.id, event.id)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Event without id WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val event = Random.nextEvent(id = null)

        // When
        event.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Event without name WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val event = Random.nextEvent(name = null)

        // When
        event.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Event without position WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val event = Random.nextEvent(position = null)

        // When
        event.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Event without meetingLocation WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val event = Random.nextEvent(meetingLocation = null)

        // When
        event.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Event without startDate WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val event = Random.nextEvent(startDate = null)

        // When
        event.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Event without endDate WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val event = Random.nextEvent(endDate = null)

        // When
        event.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Event without description WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val event = Random.nextEvent(description = null)

        // When
        event.toUIModel(currentUserId = Random.nextString())
    }

    @Test
    fun `GIVEN valid Event WHEN invoking toDataModel THEN result is correct EventUI`() {
        // Given
        val event = Random.nextEvent()

        // When
        val eventUI = event.toUIModel(currentUserId = Random.nextString())

        // Then
        assertEquals(event.id, eventUI.id)
    }

    @Test
    fun `GIVEN valid Event and not logged user WHEN invoking toDataModel THEN result is correct EventUI with false isCurrentUserAuthor`() {
        // Given
        val event = Random.nextEvent()

        // When
        val eventUI = event.toUIModel(currentUserId = null)

        // Then
        assertFalse(eventUI.isCurrentUserAuthor)
    }

    @Test
    fun `GIVEN valid Event and random user WHEN invoking toDataModel THEN result is correct EventUI with false isCurrentUserAuthor`() {
        // Given
        val event = Random.nextEvent()

        // When
        val eventUI = event.toUIModel(currentUserId = Random.nextString().replace(event.authorId.toString(), ""))

        // Then
        assertFalse(eventUI.isCurrentUserAuthor)
    }

    @Test
    fun `GIVEN valid Event and current user WHEN invoking toDataModel THEN result is correct EventUI with true isCurrentUserAuthor`() {
        // Given
        val event = Random.nextEvent()

        // When
        val eventUI = event.toUIModel(currentUserId = event.authorId)

        // Then
        assertTrue(eventUI.isCurrentUserAuthor)
    }
}