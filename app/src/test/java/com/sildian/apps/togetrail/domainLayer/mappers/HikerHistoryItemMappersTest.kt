package com.sildian.apps.togetrail.domainLayer.mappers

import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.nextHikerHistoryItem
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Action
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Item
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Item.Type as ItemType
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextEventAttendedHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextEventCreatedHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerHistoryItemUIHikerInfo
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerHistoryItemUIItemInfo
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerRegisteredHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextTrailCreatedHistoryItemUI
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class HikerHistoryItemMappersTest {

    @Test
    fun `GIVEN HikerRegistered HistoryItemUI WHEN invoking toDataModel THEN result is HikerHistoryItem with HikerRegistered action`() {
        // Given
        val hikerHistoryItemUI = Random.nextHikerRegisteredHistoryItemUI()

        // When
        val hikerHistoryItem = hikerHistoryItemUI.toDataModel()

        // Then
        assertEquals(Action.HIKER_REGISTERED, hikerHistoryItem.action)
        assertEquals(Item.Type.HIKER, hikerHistoryItem.item?.type)
    }

    @Test
    fun `GIVEN TrailCreated HistoryItemUI WHEN invoking toDataModel THEN result is HikerHistoryItem with TrailCreated action`() {
        // Given
        val hikerHistoryItemUI = Random.nextTrailCreatedHistoryItemUI()

        // When
        val hikerHistoryItem = hikerHistoryItemUI.toDataModel()

        // Then
        assertEquals(Action.TRAIL_CREATED, hikerHistoryItem.action)
        assertEquals(Item.Type.TRAIL, hikerHistoryItem.item?.type)
    }

    @Test
    fun `GIVEN EventCreated HistoryItemUI WHEN invoking toDataModel THEN result is HikerHistoryItem with EventCreated action`() {
        // Given
        val hikerHistoryItemUI = Random.nextEventCreatedHistoryItemUI()

        // When
        val hikerHistoryItem = hikerHistoryItemUI.toDataModel()

        // Then
        assertEquals(Action.EVENT_CREATED, hikerHistoryItem.action)
        assertEquals(Item.Type.EVENT, hikerHistoryItem.item?.type)
    }

    @Test
    fun `GIVEN EventAttended HistoryItemUI WHEN invoking toDataModel THEN result is HikerHistoryItem with EventAttended action`() {
        // Given
        val hikerHistoryItemUI = Random.nextEventAttendedHistoryItemUI()

        // When
        val hikerHistoryItem = hikerHistoryItemUI.toDataModel()

        // Then
        assertEquals(Action.EVENT_ATTENDED, hikerHistoryItem.action)
        assertEquals(Item.Type.EVENT, hikerHistoryItem.item?.type)
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem without date WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(date = null)

        // When
        hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem without action WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(action = null)

        // When
        hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem without itemId WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(itemId = null)

        // When
        hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem without itemType WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(itemType = null)

        // When
        hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN HikerHistoryItem with inconsistent action and item WHEN invoking toUIModel THEN throws IllegalStateException`() {
        // Given
        val action = HikerHistoryItem.Action.values().random()
        val itemType = when (action) {
            Action.HIKER_REGISTERED -> listOf(ItemType.TRAIL, ItemType.EVENT).random()
            Action.TRAIL_CREATED -> listOf(ItemType.HIKER, ItemType.EVENT).random()
            Action.EVENT_CREATED -> listOf(ItemType.HIKER, ItemType.TRAIL).random()
            Action.EVENT_ATTENDED -> listOf(ItemType.HIKER, ItemType.TRAIL).random()
        }
        val hikerHistoryItem = Random.nextHikerHistoryItem(action = action, itemType = itemType)

        // When
        hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )
    }

    @Test
    fun `GIVEN HikerHistoryItem with HikerRegistered action WHEN invoking toUIModel THEN result is HikerRegistered HistoryItemUI`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(
            action = Action.HIKER_REGISTERED,
            itemType = Item.Type.HIKER,
        )

        // When
        val hikerHistoryItemUI = hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )

        // Then
        assertTrue(hikerHistoryItemUI is HikerHistoryItemUI.HikerRegistered)
    }

    @Test
    fun `GIVEN HikerHistoryItem with TrailCreated action WHEN invoking toUIModel THEN result is TrailCreated HistoryItemUI`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(
            action = Action.TRAIL_CREATED,
            itemType = Item.Type.TRAIL,
        )

        // When
        val hikerHistoryItemUI = hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )

        // Then
        assertTrue(hikerHistoryItemUI is HikerHistoryItemUI.TrailCreated)
    }

    @Test
    fun `GIVEN HikerHistoryItem with EventCreated action WHEN invoking toUIModel THEN result is EventCreated HistoryItemUI`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(
            action = Action.EVENT_CREATED,
            itemType = Item.Type.EVENT,
        )

        // When
        val hikerHistoryItemUI = hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )

        // Then
        assertTrue(hikerHistoryItemUI is HikerHistoryItemUI.EventCreated)
    }

    @Test
    fun `GIVEN HikerHistoryItem with EventAttended action WHEN invoking toUIModel THEN result is EventAttended HistoryItemUI`() {
        // Given
        val hikerHistoryItem = Random.nextHikerHistoryItem(
            action = Action.EVENT_ATTENDED,
            itemType = Item.Type.EVENT,
        )

        // When
        val hikerHistoryItemUI = hikerHistoryItem.toUIModel(
            hikerInfo = Random.nextHikerHistoryItemUIHikerInfo(),
            itemInfo = Random.nextHikerHistoryItemUIItemInfo(),
        )

        // Then
        assertTrue(hikerHistoryItemUI is HikerHistoryItemUI.EventAttended)
    }
}