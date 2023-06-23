package com.sildian.apps.togetrail.dataLayer.database.event.attachedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.event.EventTrail
import com.sildian.apps.togetrail.dataLayer.database.entities.event.nextEventTrailsList
import kotlin.random.Random

class EventAttachedTrailRepositoryFake(
    private val error: DatabaseException? = null,
    private val trails: List<EventTrail> = Random.nextEventTrailsList(),
) : EventAttachedTrailRepository {

    var addOrUpdateAttachedTrailSuccessCount: Int = 0 ; private set
    var deleteAttachedTrailSuccessCount: Int = 0 ; private set

    override suspend fun getAttachedTrails(eventId: String): List<EventTrail> =
        error?.let { throw it } ?: trails

    override suspend fun addOrUpdateAttachedTrail(eventId: String, trail: EventTrail) {
        error?.let { throw it } ?: addOrUpdateAttachedTrailSuccessCount++
    }

    override suspend fun deleteAttachedTrail(eventId: String, trailId: String) {
        error?.let { throw it } ?: deleteAttachedTrailSuccessCount++
    }
}