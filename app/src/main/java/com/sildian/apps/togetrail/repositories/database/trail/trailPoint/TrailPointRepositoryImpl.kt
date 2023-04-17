package com.sildian.apps.togetrail.repositories.database.trail.trailPoint

import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.trail.TrailPoint
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrailPointRepositoryImpl @Inject constructor(
    private val databaseService: TrailPointDatabaseService,
) : TrailPointRepository {

    override suspend fun getAllTrailPoints(trailId: String): List<TrailPoint> =
        databaseOperation {
            databaseService
                .getAllTrailPoints(trailId = trailId)
                .get()
                .await()
                .toObjects(TrailPoint::class.java)
        }

    override suspend fun addTrailPoints(trailId: String, trailPoints: List<TrailPoint>) {
        databaseOperation {
            databaseService
                .addTrailPoints(trailId = trailId, trailPoints = trailPoints)
                .forEach { it.await() }
        }
    }
}