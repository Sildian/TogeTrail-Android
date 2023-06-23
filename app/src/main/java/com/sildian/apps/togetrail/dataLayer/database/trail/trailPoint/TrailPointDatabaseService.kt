package com.sildian.apps.togetrail.dataLayer.database.trail.trailPoint

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.utils.split
import com.sildian.apps.togetrail.dataLayer.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.TrailPoint
import javax.inject.Inject

private const val BATCH_MAX_SIZE = 500

class TrailPointDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo.TrailCollection.SubCollections.TrailPointSubCollection

    private fun collection(trailId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(trailId)
            .collection(DatabaseCollectionInfo.TrailCollection.SubCollections.TrailPointSubCollection.subCollectionName)

    fun getAllTrailPoints(trailId: String) =
        collection(trailId = trailId)
            .orderBy("number", Query.Direction.ASCENDING)

    fun addTrailPoints(trailId: String, trailPoints: List<TrailPoint>): List<Task<Void>> {
        val batchesContents = trailPoints.split(maxSize = BATCH_MAX_SIZE)
        return batchesContents.map { batchContent ->
            firebaseFirestore.runBatch { batch ->
                batchContent.forEach { trailPoint ->
                    batch.set(
                        collection(trailId = trailId).document(trailPoint.number.toString()),
                        trailPoint,
                    )
                }
            }
        }
    }
}