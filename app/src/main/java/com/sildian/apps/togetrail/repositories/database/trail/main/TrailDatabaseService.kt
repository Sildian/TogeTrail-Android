package com.sildian.apps.togetrail.repositories.database.trail.main

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail
import javax.inject.Inject

class TrailDatabaseService @Inject constructor(
    firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo = DatabaseCollectionInfo.TrailCollection.Main
    private val collection = firebaseFirestore.collection(collectionInfo.collectionName)

    fun getTrail(id: String): DocumentReference =
        collection.document(id)

    fun getTrails(ids: List<String>): Query =
        collection.whereIn(FieldPath.documentId(), ids)

    fun getLastTrails(): Query =
        collection
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .limit(20)

    fun getTrailsFromAuthor(authorId: String): Query =
        collection
            .whereEqualTo("authorId", authorId)
            .orderBy("creationDate", Query.Direction.DESCENDING)

    fun getTrailsNearbyLocation(location: Location): Query? =
        when {
            location.country != null && location.region != null -> {
                collection
                    .whereEqualTo(FieldPath.of("location", "country", "code"), location.country.code)
                    .whereEqualTo(FieldPath.of("location", "region", "code"), location.region.code)
                    .orderBy("creationDate", Query.Direction.DESCENDING)
            }
            location.country != null -> {
                collection
                    .whereEqualTo(FieldPath.of("location", "country", "code"), location.country.code)
                    .orderBy("creationDate", Query.Direction.DESCENDING)
            }
            else -> null
        }

    fun getTrailsAroundPosition(position: GeoLocation, radiusMeters: Double): List<Query> =
        GeoFireUtils.getGeoHashQueryBounds(position, radiusMeters).map {
            collection
                .orderBy("positionHash")
                .startAt(it.startHash)
                .endAt(it.endHash)
                .orderBy("creationDate", Query.Direction.DESCENDING)
        }

    fun addTrail(trail: Trail): Task<DocumentReference> =
        collection.add(trail)

    fun updateTrail(trail: Trail): Task<Void>? =
        trail.id?.let { trailId ->
            collection.document(trailId).set(trail)
        }
}