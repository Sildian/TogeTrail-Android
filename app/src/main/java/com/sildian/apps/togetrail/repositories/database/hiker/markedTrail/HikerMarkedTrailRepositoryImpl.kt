package com.sildian.apps.togetrail.repositories.database.hiker.markedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HikerMarkedTrailRepositoryImpl @Inject constructor(
    private val databaseService: HikerMarkedTrailDatabaseService,
) : HikerMarkedTrailRepository {

    override suspend fun getMarkedTrails(hikerId: String): List<Trail> =
        databaseOperation {
            databaseService
                .getMarkedTrails(hikerId = hikerId)
                .get()
                .await()
                .toObjects(Trail::class.java)
        }

    override suspend fun updateMarkedTrail(hikerId: String, trail: Trail) {
        databaseOperation {
            databaseService
                .updateMarkedTrail(hikerId = hikerId, trail = trail)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }

    override suspend fun deleteMarkedTrail(hikerId: String, trailId: String) {
        databaseOperation {
            databaseService
                .deleteMarkedTrail(hikerId = hikerId, trailId = trailId)
                .await()
        }
    }
}