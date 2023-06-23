package com.sildian.apps.togetrail.dataLayer.database.event.registeredHiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.dataLayer.database.entities.event.EventHiker
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRegisteredHikerRepositoryImpl @Inject constructor(
    private val databaseService: EventRegisteredHikerDatabaseService,
) : EventRegisteredHikerRepository {

    override suspend fun getRegisteredHikers(eventId: String): List<EventHiker> =
        databaseOperation {
            databaseService
                .getRegisteredHikers(eventId = eventId)
                .get()
                .await()
                .toObjects(EventHiker::class.java)
        }

    override suspend fun addOrUpdateRegisteredHiker(eventId: String, hiker: EventHiker) {
        databaseOperation {
            databaseService
                .addOrUpdateRegisteredHiker(eventId = eventId, hiker = hiker)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }

    override suspend fun deleteRegisteredHiker(eventId: String, hikerId: String) {
        databaseOperation {
            databaseService
                .deleteRegisteredHiker(eventId = eventId, hikerId = hikerId)
                .await()
        }
    }
}