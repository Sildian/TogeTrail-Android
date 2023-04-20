package com.sildian.apps.togetrail.repositories.database.hiker.attendedEvent

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerEvent
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HikerAttendedEventRepositoryImpl @Inject constructor(
    private val databaseService: HikerAttendedEventDatabaseService,
) : HikerAttendedEventRepository {

    override suspend fun getAttendedEvents(hikerId: String): List<HikerEvent> =
        databaseOperation {
            databaseService
                .getAttendedEvents(hikerId = hikerId)
                .get()
                .await()
                .toObjects(HikerEvent::class.java)
        }

    override suspend fun updateAttendedEvent(hikerId: String, event: HikerEvent) {
        databaseOperation {
            databaseService
                .updateAttendedEvent(hikerId = hikerId, event = event)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }

    override suspend fun deleteAttendedEvent(hikerId: String, eventId: String) {
        databaseOperation {
            databaseService
                .deleteAttendedEvent(hikerId = hikerId, eventId = eventId)
                .await()
        }
    }
}