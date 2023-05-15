package com.sildian.apps.togetrail.repositories.database.event.attachedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.event.EventTrail

interface EventAttachedTrailRepository {
    @Throws(DatabaseException::class)
    suspend fun getAttachedTrails(eventId: String): List<EventTrail>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateAttachedTrail(eventId: String, trail: EventTrail)
    @Throws(DatabaseException::class)
    suspend fun deleteAttachedTrail(eventId: String, trailId: String)
}