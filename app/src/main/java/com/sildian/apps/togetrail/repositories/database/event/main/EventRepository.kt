package com.sildian.apps.togetrail.repositories.database.event.main

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.event.Event

interface EventRepository {
    @Throws(DatabaseException::class)
    suspend fun getEvent(id: String): Event
    @Throws(DatabaseException::class)
    suspend fun getEvents(ids: List<String>): List<Event>
    @Throws(DatabaseException::class)
    suspend fun getNextEvents(): List<Event>
    @Throws(DatabaseException::class)
    suspend fun getEventsFromAuthor(authorId: String): List<Event>
    @Throws(DatabaseException::class)
    suspend fun getEventsNearbyLocation(location: Location): List<Event>
    @Throws(DatabaseException::class)
    suspend fun getEventsAroundPosition(position: GeoLocation, radiusMeters: Double): List<Event>
    @Throws(DatabaseException::class)
    suspend fun addEvent(event: Event)
    @Throws(DatabaseException::class)
    suspend fun updateEvent(event: Event)
}