package com.sildian.apps.togetrail.repositories.database.event.main

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.event.Event
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val databaseService: EventDatabaseService,
) : EventRepository {

    override suspend fun getEvent(id: String): Event =
        databaseOperation {
            databaseService
                .getEvent(id = id)
                .get()
                .await()
                .toObject(Event::class.java)
                ?: throw DatabaseException.UnknownException()
        }

    override suspend fun getNextEvents(): List<Event> =
        databaseOperation {
            databaseService
                .getNextEvents()
                .get()
                .await()
                .toObjects(Event::class.java)
        }

    override suspend fun getEventsFromAuthor(authorId: String): List<Event> =
        databaseOperation {
            databaseService
                .getEventsFromAuthor(authorId = authorId)
                .get()
                .await()
                .toObjects(Event::class.java)
        }

    override suspend fun getEventsNearbyLocation(location: Location): List<Event> =
        databaseOperation {
            databaseService
                .getEventsNearbyLocation(location = location)
                ?.get()
                ?.await()
                ?.toObjects(Event::class.java)
                ?: throw DatabaseException.UnknownException()
        }

    override suspend fun getEventsAroundPosition(
        position: GeoLocation,
        radiusMeters: Double
    ): List<Event> =
        databaseOperation {
            databaseService
                .getEventsAroundPosition(position = position, radiusMeters = radiusMeters)
                .map { it.get().await().toObjects(Event::class.java) }
                .flatten()
                .filter {
                    it.position != null &&
                    GeoFireUtils.getDistanceBetween(it.position, position) <= radiusMeters
                }
        }

    override suspend fun addEvent(event: Event) {
        databaseOperation {
            databaseService
                .addEvent(event = event)
                .await()
        }
    }

    override suspend fun updateEvent(event: Event) {
        databaseOperation {
            databaseService
                .updateEvent(event = event)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }
}