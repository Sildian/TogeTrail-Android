package com.sildian.apps.togetrail.dataLayer.database.hiker.markedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerTrail
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HikerMarkedTrailRepositoryImpl @Inject constructor(
    private val databaseService: HikerMarkedTrailDatabaseService,
) : HikerMarkedTrailRepository {

    override suspend fun getMarkedTrails(hikerId: String): List<HikerTrail> =
        databaseOperation {
            databaseService
                .getMarkedTrails(hikerId = hikerId)
                .get()
                .await()
                .toObjects(HikerTrail::class.java)
        }

    override suspend fun addOrUpdateMarkedTrail(hikerId: String, trail: HikerTrail) {
        databaseOperation {
            databaseService
                .addOrUpdateMarkedTrail(hikerId = hikerId, trail = trail)
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