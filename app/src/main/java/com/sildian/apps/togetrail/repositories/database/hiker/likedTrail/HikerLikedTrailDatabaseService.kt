package com.sildian.apps.togetrail.repositories.database.hiker.likedTrail

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerTrail
import javax.inject.Inject

class HikerLikedTrailDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .HikerCollection
            .SubCollections
            .LikedTrailSubCollection

    private fun collection(hikerId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(hikerId)
            .collection(collectionInfo.subCollectionName)

    fun getLikedTrails(hikerId: String): Query =
        collection(hikerId = hikerId)
            .orderBy("creationDate", Query.Direction.DESCENDING)

    fun updateLikedTrail(hikerId: String, trail: HikerTrail): Task<Void>? =
        trail.id?.let { trailId ->
            collection(hikerId = hikerId)
                .document(trailId)
                .set(trail)
        }

    fun deleteLikedTrail(hikerId: String, trailId: String): Task<Void> =
        collection(hikerId = hikerId)
            .document(trailId)
            .delete()
}