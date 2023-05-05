package com.sildian.apps.togetrail.repositories.database.hiker.attendedEvent

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerEvent
import com.sildian.apps.togetrail.repositories.database.entities.hiker.nextHikerEventsList
import kotlin.random.Random

class HikerAttendedEventRepositoryFake(
    private val error: DatabaseException? = null,
    private val events: List<HikerEvent> = Random.nextHikerEventsList(),
) : HikerAttendedEventRepository {

    var updateAttendedEventSuccessCount: Int = 0 ; private set
    var deleteAttendedEventSuccessCount: Int = 0 ; private set

    override suspend fun getAttendedEvents(hikerId: String): List<HikerEvent> =
        error?.let { throw it } ?: events

    override suspend fun updateAttendedEvent(hikerId: String, event: HikerEvent) {
        error?.let { throw it } ?: updateAttendedEventSuccessCount++
    }

    override suspend fun deleteAttendedEvent(hikerId: String, eventId: String) {
        error?.let { throw it } ?: deleteAttendedEventSuccessCount++
    }
}