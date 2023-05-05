package com.sildian.apps.togetrail.repositories.database.event.attachedTrail

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.event.EventTrail
import javax.inject.Inject

class EventAttachedTrailDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .EventCollection
            .SubCollections
            .AttachedTrailSubCollection

    private fun collection(eventId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(eventId)
            .collection(collectionInfo.subCollectionName)

    fun getAttachedTrails(eventId: String): Query =
        collection(eventId = eventId)

    fun addOrUpdateAttachedTrail(eventId: String, trail: EventTrail): Task<Void>? =
        trail.id?.let { trailId ->
            collection(eventId = eventId)
                .document(trailId)
                .set(trail)
        }

    fun deleteAttachedTrail(eventId: String, trailId: String): Task<Void> =
        collection(eventId = eventId)
            .document(trailId)
            .delete()
}