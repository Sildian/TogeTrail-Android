package com.sildian.apps.togetrail.repositories.database.hiker.likedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HikerLikedTrailRepositoryImpl @Inject constructor(
    private val databaseService: HikerLikedTrailDatabaseService,
) : HikerLikedTrailRepository {

    override suspend fun getLikedTrails(hikerId: String): List<Trail> =
        databaseOperation {
            databaseService
                .getLikedTrails(hikerId = hikerId)
                .get()
                .await()
                .toObjects(Trail::class.java)
        }

    override suspend fun updateLikedTrail(hikerId: String, trail: Trail) {
        databaseOperation {
            databaseService
                .updateLikedTrail(hikerId = hikerId, trail = trail)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }

    override suspend fun deleteLikedTrail(hikerId: String, trailId: String) {
        databaseOperation {
            databaseService
                .deleteLikedTrail(hikerId = hikerId, trailId = trailId)
                .await()
        }
    }
}