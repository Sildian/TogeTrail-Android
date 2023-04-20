package com.sildian.apps.togetrail.repositories.database.event.registeredHiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.event.EventHiker
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

    override suspend fun updateRegisteredHiker(eventId: String, hiker: EventHiker) {
        databaseOperation {
            databaseService
                .updateRegisteredHiker(eventId = eventId, hiker = hiker)
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