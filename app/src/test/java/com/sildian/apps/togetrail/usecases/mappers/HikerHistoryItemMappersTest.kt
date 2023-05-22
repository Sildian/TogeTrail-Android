package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.features.entities.hiker.nextHikerHistoryItemUI
import com.sildian.apps.togetrail.repositories.database.entities.hiker.nextHikerHistoryItem
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class HikerHistoryItemMappersTest {

    @Test
    fun `GIVEN HikerHistoryItemUI WHEN invoking toDataModel THEN result is correct HikerHistoryItem`() {
        // Given
        val hikerHistoryItemUI = Random.nextHikerHistoryItemUI()

        // When
        val hikerHistoryItem = hikerHistoryItemUI.toDataModel()

        // Then
        assertEquals(hikerHistoryItemUI.item.id, hikerHistoryItem.item!!.id)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem without date WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(date = null)

        // When
        hikerHistoryItem.toUIModel()
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem without action WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(action = null)

        // When
        hikerHistoryItem.toUIModel()
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem without itemId WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(itemId = null)

        // When
        hikerHistoryItem.toUIModel()
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem without itemType WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(itemType = null)

        // When
        hikerHistoryItem.toUIModel()
    }

    @Test
    fun `GIVEN valid HikerHistoryItem WHEN invoking toUIModel THEN result is correct HikerHistoryItemUI`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem()

        // When
        val hikerHistoryItemUI = hikerHistoryItem.toUIModel()

        // Then
        assertEquals(hikerHistoryItem.item!!.id, hikerHistoryItemUI.item.id)
    }
}