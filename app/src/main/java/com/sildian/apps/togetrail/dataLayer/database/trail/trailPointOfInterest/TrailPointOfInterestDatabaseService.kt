package com.sildian.apps.togetrail.dataLayer.database.trail.trailPointOfInterest

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.utils.split
import com.sildian.apps.togetrail.dataLayer.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.TrailPointOfInterest
import javax.inject.Inject

private const val BATCH_MAX_SIZE = 500

class TrailPointOfInterestDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo.TrailCollection.SubCollections.TrailPointOfInterestSubCollection

    private fun collection(trailId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(trailId)
            .collection(DatabaseCollectionInfo.TrailCollection.SubCollections.TrailPointOfInterestSubCollection.subCollectionName)

    fun getAllTrailPointsOfInterest(trailId: String) =
        collection(trailId = trailId)
            .orderBy("number", Query.Direction.ASCENDING)

    fun addTrailPointsOfInterest(
        trailId: String,
        trailPointsOfInterest: List<TrailPointOfInterest>
    ): List<Task<Void>> {
        val batchesContents = trailPointsOfInterest.split(maxSize = BATCH_MAX_SIZE)
        return batchesContents.map { batchContent ->
            firebaseFirestore.runBatch { batch ->
                batchContent.forEach { trailPointOfInterest ->
                    batch.set(
                        collection(trailId = trailId).document(trailPointOfInterest.number.toString()),
                        trailPointOfInterest,
                    )
                }
            }
        }
    }

    fun updateTrailPointOfInterest(
        trailId: String,
        trailPointOfInterest: TrailPointOfInterest
    ): Task<Void>? =
        trailPointOfInterest.number?.let { poiNumber ->
            collection(trailId = trailId)
                .document(poiNumber.toString())
                .set(trailPointOfInterest)
        }
}