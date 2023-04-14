package com.sildian.apps.togetrail.repositories.database.event.attachedTrail

import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail

interface EventAttachedTrailRepository {
    suspend fun getAttachedTrails(eventId: String): List<Trail>
    suspend fun updateAttachedTrail(eventId: String, trail: Trail)
    suspend fun deleteAttachedTrail(eventId: String, trailId: String)
}