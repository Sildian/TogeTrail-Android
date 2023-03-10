package com.sildian.apps.togetrail.repositories.database.event

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.event.Event
import java.util.*
import javax.inject.Inject

class EventDatabaseService @Inject constructor(
    firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo = DatabaseCollectionInfo.EventCollection.Main
    private val collection = firebaseFirestore.collection(collectionInfo.collectionName)

    fun getEvent(id: String): DocumentReference =
        collection.document(id)

    fun getNextEvents(): Query =
        collection
            .whereGreaterThan("startDate", Date())
            .whereEqualTo("canceled", false)
            .orderBy("startDate", Query.Direction.ASCENDING)

    fun getEventsFromAuthor(authorId: String): Query =
        collection
            .whereEqualTo(FieldPath.of("author", "id"), authorId)
            .whereGreaterThan("startDate", Date())
            .orderBy("startDate", Query.Direction.ASCENDING)

    fun getEventsNearbyLocation(location: Location): Query? =
        when {
            location.country != null && location.region != null -> {
                collection
                    .whereEqualTo(FieldPath.of("meetingLocation", "country", "code"), location.country.code)
                    .whereEqualTo(FieldPath.of("meetingLocation", "region", "code"), location.region.code)
                    .whereGreaterThan("startDate", Date())
                    .whereEqualTo("canceled", false)
                    .orderBy("startDate", Query.Direction.ASCENDING)
            }
            location.country != null -> {
                collection
                    .whereEqualTo(FieldPath.of("meetingLocation", "country", "code"), location.country.code)
                    .whereGreaterThan("startDate", Date())
                    .whereEqualTo("canceled", false)
                    .orderBy("startDate", Query.Direction.ASCENDING)
            }
            else -> null
        }

    fun getEventsAroundPosition(position: GeoLocation, radiusMeters: Double): List<Query> =
        GeoFireUtils.getGeoHashQueryBounds(position, radiusMeters).map {
            collection
                .orderBy("positionHash")
                .startAt(it.startHash)
                .endAt(it.endHash)
                .whereGreaterThan("startDate", Date())
                .whereEqualTo("canceled", false)
                .orderBy("startDate", Query.Direction.ASCENDING)
        }

    fun addEvent(event: Event): Task<DocumentReference> =
        collection.add(event)

    fun updateEvent(event: Event): Task<Void>? =
        event.id?.let { eventId ->
            collection.document(eventId).set(event)
        }
}