package com.sildian.apps.togetrail.dataLayer.database.event.main

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.event.Event
import com.sildian.apps.togetrail.dataLayer.database.entities.event.nextEventsList
import kotlin.random.Random

class EventRepositoryFake(
    private val error: DatabaseException? = null,
    private val events: List<Event> = Random.nextEventsList(),
) : EventRepository {

    var addEventSuccessCount: Int = 0 ; private set
    var updateEventSuccessCount: Int = 0 ; private set

    override suspend fun getEvent(id: String): Event =
        error?.let { throw it } ?: events.random()

    override suspend fun getEvents(ids: List<String>): List<Event> =
        error?.let { throw it } ?: events

    override suspend fun getNextEvents(): List<Event> =
        error?.let { throw it } ?: events

    override suspend fun getEventsFromAuthor(authorId: String): List<Event> =
        error?.let { throw it } ?: events

    override suspend fun getEventsNearbyLocation(location: Location): List<Event> =
        error?.let { throw it } ?: events

    override suspend fun getEventsAroundPosition(
        position: GeoLocation,
        radiusMeters: Double
    ): List<Event> =
        error?.let { throw it } ?: events

    override suspend fun addEvent(event: Event) {
        error?.let { throw it } ?: addEventSuccessCount++
    }

    override suspend fun updateEvent(event: Event) {
        error?.let { throw it } ?: updateEventSuccessCount++
    }
}