package com.sildian.apps.togetrail.repositories.database.event.attachedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventAttachedTrailRepositoryImpl @Inject constructor(
    private val databaseService: EventAttachedTrailDatabaseService,
) : EventAttachedTrailRepository {

    override suspend fun getAttachedTrails(eventId: String): List<Trail> =
        databaseOperation {
            databaseService
                .getAttachedTrails(eventId = eventId)
                .get()
                .await()
                .toObjects(Trail::class.java)
        }

    override suspend fun updateAttachedTrail(eventId: String, trail: Trail) {
        databaseOperation {
            databaseService
                .updateAttachedTrail(eventId = eventId, trail = trail)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }

    override suspend fun deleteAttachedTrail(eventId: String, trailId: String) {
        databaseOperation {
            databaseService
                .deleteAttachedTrail(eventId = eventId, trailId = trailId)
                .await()
        }
    }
}