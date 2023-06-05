package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.features.entities.hiker.nextHikerUI
import com.sildian.apps.togetrail.repositories.database.entities.hiker.nextHiker
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class HikerMappersTest {

    @Test
    fun `GIVEN HikerUI WHEN invoking toDataModel THEN result is correct Hiker`() {
        // Given
        val hikerUI = Random.nextHikerUI()

        // When
        val hiker = hikerUI.toDataModel()

        // Then
        assertEquals(hikerUI.id, hiker.id)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Hiker without id WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hiker = Random.nextHiker(id = null)

        // When
        hiker.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Hiker without email WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hiker = Random.nextHiker(email = null)

        // When
        hiker.toUIModel(currentUserId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN Hiker without name WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hiker = Random.nextHiker(name = null)

        // When
        hiker.toUIModel(currentUserId = Random.nextString())
    }

    @Test
    fun `GIVEN valid Hiker WHEN invoking toUIModel THEN result is correct HikerUI`() {
        // Given
        val hiker = Random.nextHiker()

        // When
        val hikerUI = hiker.toUIModel(currentUserId = Random.nextString())

        // Then
        assertEquals(hiker.id, hikerUI.id)
    }

    @Test
    fun `GIVEN valid Hiker and not logged user WHEN invoking toUIModel THEN result is correct HikerUI with false isCurrentUser`() {
        // Given
        val hiker = Random.nextHiker()

        // When
        val hikerUI = hiker.toUIModel(currentUserId = null)

        // Then
        assertFalse(hikerUI.isCurrentUser)
    }

    @Test
    fun `GIVEN valid Hiker and random user WHEN invoking toUIModel THEN result is correct HikerUI with false isCurrentUser`() {
        // Given
        val hiker = Random.nextHiker()

        // When
        val hikerUI = hiker.toUIModel(currentUserId = Random.nextString().replace(hiker.id.toString(), ""))

        // Then
        assertFalse(hikerUI.isCurrentUser)
    }

    @Test
    fun `GIVEN valid Hiker and current user WHEN invoking toUIModel THEN result is correct HikerUI with true isCurrentUser`() {
        // Given
        val hiker = Random.nextHiker()

        // When
        val hikerUI = hiker.toUIModel(currentUserId = hiker.id.toString())

        // Then
        assertTrue(hikerUI.isCurrentUser)
    }
}