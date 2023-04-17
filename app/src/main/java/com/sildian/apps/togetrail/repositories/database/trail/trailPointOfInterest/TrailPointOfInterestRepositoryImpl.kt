package com.sildian.apps.togetrail.repositories.database.trail.trailPointOfInterest

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.trail.TrailPointOfInterest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrailPointOfInterestRepositoryImpl @Inject constructor(
    private val databaseService: TrailPointOfInterestDatabaseService,
) : TrailPointOfInterestRepository {

    override suspend fun getAllTrailPointsOfInterest(trailId: String): List<TrailPointOfInterest> =
        databaseOperation {
            databaseService
                .getAllTrailPointsOfInterest(trailId = trailId)
                .get()
                .await()
                .toObjects(TrailPointOfInterest::class.java)
        }

    override suspend fun addTrailPointsOfInterest(
        trailId: String,
        trailPointsOfInterest: List<TrailPointOfInterest>
    ) {
        databaseOperation {
            databaseService
                .addTrailPointsOfInterest(
                    trailId = trailId,
                    trailPointsOfInterest = trailPointsOfInterest
                ).forEach { it.await() }
        }
    }

    override suspend fun updateTrailPointOfInterest(
        trailId: String,
        trailPointOfInterest: TrailPointOfInterest
    ) {
        databaseOperation {
            databaseService
                .updateTrailPointOfInterest(
                    trailId = trailId,
                    trailPointOfInterest = trailPointOfInterest
                )?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }
}