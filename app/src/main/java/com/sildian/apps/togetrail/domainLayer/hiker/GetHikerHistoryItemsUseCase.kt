package com.sildian.apps.togetrail.domainLayer.hiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.event.Event
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.Hiker
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.Trail
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Item.Type as ItemType
import com.sildian.apps.togetrail.dataLayer.database.event.main.EventRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem.HikerHistoryItemRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.main.HikerRepository
import com.sildian.apps.togetrail.dataLayer.database.trail.main.TrailRepository
import com.sildian.apps.togetrail.domainLayer.mappers.toUIModel
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.HikerInfo
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.ItemInfo
import javax.inject.Inject
import kotlin.jvm.Throws

interface GetHikerHistoryItemsUseCase {
    @Throws(DatabaseException::class, IllegalStateException::class)
    suspend operator fun invoke(hikerId: String): List<HikerHistoryItemUI>
}

class GetHikerHistoryItemsUseCaseImpl @Inject constructor(
    private val hikerHistoryItemRepository: HikerHistoryItemRepository,
    private val hikerRepository: HikerRepository,
    private val trailRepository: TrailRepository,
    private val eventRepository: EventRepository,
) : GetHikerHistoryItemsUseCase {

    override suspend operator fun invoke(hikerId: String): List<HikerHistoryItemUI> {
        val historyItems = getHistoryItems(hikerId = hikerId)
        val hiker = getHiker(hikerId = hikerId)
        val trails = getTrailsRelatedToHistoryItems(historyItems = historyItems)
        val events = getEventsRelatedToHistoryItems(historyItems = historyItems)
        val hikerInfo = try {
            hiker.extractHikerInfo()
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Hiker info should be consistent")
        }
        return historyItems.map { historyItem ->
            val itemInfo = when (historyItem.item?.type) {
                null ->
                    null
                ItemType.HIKER ->
                    ItemInfo(
                        id = hikerInfo.id,
                        name = hikerInfo.name,
                        photoUrl = hikerInfo.photoUrl,
                        location = null,
                    )
                ItemType.TRAIL ->
                    try {
                        trails.find { it.id == historyItem.item.id }?.extractItemInfo()
                    } catch (e: IllegalStateException) {
                        null
                    }
                ItemType.EVENT ->
                    try {
                        events.find { it.id == historyItem.item.id }?.extractItemInfo()
                    } catch (e: IllegalStateException) {
                        null
                    }
            } ?: return@map null
            historyItem.toUIModel(hikerInfo = hikerInfo, itemInfo = itemInfo)
        }.filterNotNull()
    }

    private suspend fun getHistoryItems(hikerId: String): List<HikerHistoryItem> =
        hikerHistoryItemRepository.getLastHistoryItems(hikerId = hikerId)

    private suspend fun getHiker(hikerId: String): Hiker =
        hikerRepository.getHiker(id = hikerId)

    private suspend fun getTrailsRelatedToHistoryItems(
        historyItems: List<HikerHistoryItem>
    ): List<Trail> =
        historyItems
            .filter { it.item?.type == ItemType.TRAIL }
            .mapNotNull { it.item?.id }
            .let { trailsIds ->
                trailRepository.getTrails(ids = trailsIds)
            }

    private suspend fun getEventsRelatedToHistoryItems(
        historyItems: List<HikerHistoryItem>
    ): List<Event> =
        historyItems
            .filter { it.item?.type == ItemType.EVENT }
            .mapNotNull { it.item?.id }
            .let { eventsIds ->
                eventRepository.getEvents(ids = eventsIds)
            }

    private fun Hiker.extractHikerInfo(): HikerInfo {
        checkNotNull(id)
        checkNotNull(name)
        return HikerInfo(
            id = id,
            name = name,
            photoUrl = photoUrl,
        )
    }

    private fun Trail.extractItemInfo(): ItemInfo {
        checkNotNull(id)
        checkNotNull(name)
        return ItemInfo(
            id = id,
            name = name,
            photoUrl = mainPhotoUrl,
            location = location,
        )
    }

    private fun Event.extractItemInfo(): ItemInfo {
        checkNotNull(id)
        checkNotNull(name)
        return ItemInfo(
            id = id,
            name = name,
            photoUrl = mainPhotoUrl,
            location = meetingLocation,
        )
    }
}