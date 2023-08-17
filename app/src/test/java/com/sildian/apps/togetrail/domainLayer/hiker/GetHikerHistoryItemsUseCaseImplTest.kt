package com.sildian.apps.togetrail.domainLayer.hiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.dataLayer.database.entities.event.nextEvent
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Action
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.nextHiker
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Item.Type as ItemType
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.nextHikerHistoryItem
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.nextTrail
import com.sildian.apps.togetrail.dataLayer.database.event.main.EventRepository
import com.sildian.apps.togetrail.dataLayer.database.event.main.EventRepositoryFake
import com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem.HikerHistoryItemRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem.HikerHistoryItemRepositoryFake
import com.sildian.apps.togetrail.dataLayer.database.hiker.main.HikerRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.main.HikerRepositoryFake
import com.sildian.apps.togetrail.dataLayer.database.trail.main.TrailRepository
import com.sildian.apps.togetrail.dataLayer.database.trail.main.TrailRepositoryFake
import com.sildian.apps.togetrail.domainLayer.mappers.toUIModel
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.HikerInfo
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.ItemInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GetHikerHistoryItemsUseCaseImplTest {

    private fun initUseCase(
        hikerHistoryItemRepository: HikerHistoryItemRepository = HikerHistoryItemRepositoryFake(),
        hikerRepository: HikerRepository = HikerRepositoryFake(),
        trailRepository: TrailRepository = TrailRepositoryFake(),
        eventRepository: EventRepository = EventRepositoryFake(),
    ): GetHikerHistoryItemsUseCase =
        GetHikerHistoryItemsUseCaseImpl(
            hikerHistoryItemRepository = hikerHistoryItemRepository,
            hikerRepository = hikerRepository,
            trailRepository = trailRepository,
            eventRepository = eventRepository,
        )

    @Test(expected = DatabaseException::class)
    fun `GIVEN DatabaseException error from hikerHistoryItemRepository WHEN invoke THEN throws DatabaseException`() = runTest {
        // Given
        val useCase = initUseCase(
            hikerHistoryItemRepository = HikerHistoryItemRepositoryFake(error = DatabaseException.UnknownException()),
        )

        // When
        useCase(hikerId = Random.nextString())
    }

    @Test(expected = DatabaseException::class)
    fun `GIVEN DatabaseException error from hikerRepository WHEN invoke THEN throws DatabaseException`() = runTest {
        // Given
        val useCase = initUseCase(
            hikerRepository = HikerRepositoryFake(error = DatabaseException.UnknownException()),
        )

        // When
        useCase(hikerId = Random.nextString())
    }

    @Test(expected = DatabaseException::class)
    fun `GIVEN DatabaseException error from trailRepository WHEN invoke THEN throws DatabaseException`() = runTest {
        // Given
        val useCase = initUseCase(
            trailRepository = TrailRepositoryFake(error = DatabaseException.UnknownException()),
        )

        // When
        useCase(hikerId = Random.nextString())
    }

    @Test(expected = DatabaseException::class)
    fun `GIVEN DatabaseException error from eventRepository WHEN invoke THEN throws DatabaseException`() = runTest {
        // Given
        val useCase = initUseCase(
            eventRepository = EventRepositoryFake(error = DatabaseException.UnknownException()),
        )

        // When
        useCase(hikerId = Random.nextString())
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN inconsistent Hiker WHEN invoke THEN throws IllegalStateException`() = runTest {
        // Given
        val hiker = Random.nextHiker(id = null)
        val useCase = initUseCase(
            hikerRepository = HikerRepositoryFake(hikers = listOf(hiker))
        )

        // When
        useCase(hikerId = Random.nextString())
    }

    @Test
    fun `GIVEN inconsistent historyItems WHEN invoke THEN result is empty list`() = runTest {
        // Given
        val hiker = Random.nextHiker()
        val trail = Random.nextTrail(id = null)
        val event = Random.nextEvent(id = null)
        val historyItems = listOf(
            Random.nextHikerHistoryItem(itemType = null),
            Random.nextHikerHistoryItem(action = Action.TRAIL_CREATED, itemId = trail.id, itemType = ItemType.TRAIL),
            Random.nextHikerHistoryItem(action = Action.EVENT_CREATED, itemId = event.id, itemType = ItemType.EVENT),
            Random.nextHikerHistoryItem(action = Action.EVENT_ATTENDED, itemId = event.id, itemType = ItemType.EVENT),
        )
        val useCase = initUseCase(
            hikerHistoryItemRepository = HikerHistoryItemRepositoryFake(historyItems = historyItems),
            hikerRepository = HikerRepositoryFake(hikers = listOf(hiker)),
            trailRepository = TrailRepositoryFake(trails = listOf(trail)),
            eventRepository = EventRepositoryFake(events = listOf(event)),
        )

        // When
        val historyItemsUI = useCase(hikerId = Random.nextString())

        // Then
        val expectedResult = emptyList<HikerHistoryItemUI>()
        assertEquals(expectedResult, historyItemsUI)
    }

    @Test
    fun `GIVEN historyItems WHEN invoke THEN result is correct historyItemsUI`() = runTest {
        // Given
        val hiker = Random.nextHiker()
        val trail = Random.nextTrail()
        val event = Random.nextEvent()
        val historyItems = listOf(
            Random.nextHikerHistoryItem(itemType = null),
            Random.nextHikerHistoryItem(action = Action.HIKER_REGISTERED, itemId = hiker.id, itemType = ItemType.HIKER),
            Random.nextHikerHistoryItem(action = Action.TRAIL_CREATED, itemId = trail.id, itemType = ItemType.TRAIL),
            Random.nextHikerHistoryItem(action = Action.EVENT_CREATED, itemId = event.id, itemType = ItemType.EVENT),
            Random.nextHikerHistoryItem(action = Action.EVENT_ATTENDED, itemId = event.id, itemType = ItemType.EVENT),
        )
        val useCase = initUseCase(
            hikerHistoryItemRepository = HikerHistoryItemRepositoryFake(historyItems = historyItems),
            hikerRepository = HikerRepositoryFake(hikers = listOf(hiker)),
            trailRepository = TrailRepositoryFake(trails = listOf(trail)),
            eventRepository = EventRepositoryFake(events = listOf(event)),
        )

        // When
        val historyItemsUI = useCase(hikerId = Random.nextString())

        // Then
        val expectedResult = listOf(
            historyItems[1].toUIModel(
                hikerInfo = HikerInfo(id = hiker.id.orEmpty(), name = hiker.name.orEmpty(), photoUrl = hiker.photoUrl),
                itemInfo = ItemInfo(id = hiker.id.orEmpty(), name = hiker.name.orEmpty(), photoUrl = hiker.photoUrl, location = null),
            ),
            historyItems[2].toUIModel(
                hikerInfo = HikerInfo(id = hiker.id.orEmpty(), name = hiker.name.orEmpty(), photoUrl = hiker.photoUrl),
                itemInfo = ItemInfo(id = trail.id.orEmpty(), name = trail.name.orEmpty(), photoUrl = trail.mainPhotoUrl, location = trail.location),
            ),
            historyItems[3].toUIModel(
                hikerInfo = HikerInfo(id = hiker.id.orEmpty(), name = hiker.name.orEmpty(), photoUrl = hiker.photoUrl),
                itemInfo = ItemInfo(id = event.id.orEmpty(), name = event.name.orEmpty(), photoUrl = event.mainPhotoUrl, location = event.meetingLocation),
            ),
            historyItems[4].toUIModel(
                hikerInfo = HikerInfo(id = hiker.id.orEmpty(), name = hiker.name.orEmpty(), photoUrl = hiker.photoUrl),
                itemInfo = ItemInfo(id = event.id.orEmpty(), name = event.name.orEmpty(), photoUrl = event.mainPhotoUrl, location = event.meetingLocation),
            ),
        )
        assertEquals(expectedResult, historyItemsUI)
    }
}