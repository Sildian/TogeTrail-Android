package com.sildian.apps.togetrail.repositories.database.event.registeredHiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.hiker.Hiker
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRegisteredHikerRepositoryImpl @Inject constructor(
    private val databaseService: EventRegisteredHikerDatabaseService,
) : EventRegisteredHikerRepository {

    override suspend fun getRegisteredHikers(eventId: String): List<Hiker> =
        databaseOperation {
            databaseService
                .getRegisteredHikers(eventId = eventId)
                .get()
                .await()
                .toObjects(Hiker::class.java)
        }

    override suspend fun updateRegisteredHiker(eventId: String, hiker: Hiker) {
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