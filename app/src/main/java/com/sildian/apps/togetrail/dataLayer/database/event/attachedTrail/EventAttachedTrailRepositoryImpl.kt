package com.sildian.apps.togetrail.dataLayer.database.event.attachedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.dataLayer.database.entities.event.EventTrail
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventAttachedTrailRepositoryImpl @Inject constructor(
    private val databaseService: EventAttachedTrailDatabaseService,
) : EventAttachedTrailRepository {

    override suspend fun getAttachedTrails(eventId: String): List<EventTrail> =
        databaseOperation {
            databaseService
                .getAttachedTrails(eventId = eventId)
                .get()
                .await()
                .toObjects(EventTrail::class.java)
        }

    override suspend fun addOrUpdateAttachedTrail(eventId: String, trail: EventTrail) {
        databaseOperation {
            databaseService
                .addOrUpdateAttachedTrail(eventId = eventId, trail = trail)
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