package com.sildian.apps.togetrail.repositories.database.event.attachedTrail

import com.sildian.apps.togetrail.repositories.database.entities.event.EventTrail

interface EventAttachedTrailRepository {
    suspend fun getAttachedTrails(eventId: String): List<EventTrail>
    suspend fun updateAttachedTrail(eventId: String, trail: EventTrail)
    suspend fun deleteAttachedTrail(eventId: String, trailId: String)
}