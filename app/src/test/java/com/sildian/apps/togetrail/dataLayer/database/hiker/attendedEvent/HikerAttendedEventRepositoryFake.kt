package com.sildian.apps.togetrail.dataLayer.database.hiker.attendedEvent

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerEvent
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.nextHikerEventsList
import kotlin.random.Random

class HikerAttendedEventRepositoryFake(
    private val error: DatabaseException? = null,
    private val events: List<HikerEvent> = Random.nextHikerEventsList(),
) : HikerAttendedEventRepository {

    var addOrUpdateAttendedEventSuccessCount: Int = 0 ; private set
    var deleteAttendedEventSuccessCount: Int = 0 ; private set

    override suspend fun getAttendedEvents(hikerId: String): List<HikerEvent> =
        error?.let { throw it } ?: events

    override suspend fun addOrUpdateAttendedEvent(hikerId: String, event: HikerEvent) {
        error?.let { throw it } ?: addOrUpdateAttendedEventSuccessCount++
    }

    override suspend fun deleteAttendedEvent(hikerId: String, eventId: String) {
        error?.let { throw it } ?: deleteAttendedEventSuccessCount++
    }
}