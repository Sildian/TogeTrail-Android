package com.sildian.apps.togetrail.repositories.database.event.main

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.repositories.database.entities.event.Event

interface EventRepository {
    suspend fun getEvent(id: String): Event
    suspend fun getNextEvents(): List<Event>
    suspend fun getEventsFromAuthor(authorId: String): List<Event>
    suspend fun getEventsNearbyLocation(location: Location): List<Event>
    suspend fun getEventsAroundPosition(position: GeoLocation, radiusMeters: Double): List<Event>
    suspend fun addEvent(event: Event)
    suspend fun updateEvent(event: Event)
}