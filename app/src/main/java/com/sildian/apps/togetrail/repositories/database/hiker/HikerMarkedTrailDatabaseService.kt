package com.sildian.apps.togetrail.repositories.database.hiker

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail
import javax.inject.Inject

class HikerMarkedTrailDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .HikerCollection
            .SubCollections
            .MarkedTrailSubCollection

    private fun collection(hikerId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(hikerId)
            .collection(collectionInfo.subCollectionName)

    fun getMarkedTrails(hikerId: String): Query =
        collection(hikerId = hikerId)
            .orderBy("creationDate", Query.Direction.DESCENDING)

    fun updateMarkedTrail(hikerId: String, trail: Trail): Task<Void>? =
        trail.id?.let { trailId ->
            collection(hikerId = hikerId)
                .document(trailId)
                .set(trail)
        }

    fun deleteMarkedTrail(hikerId: String, trailId: String): Task<Void> =
        collection(hikerId = hikerId)
            .document(trailId)
            .delete()
}