package com.sildian.apps.togetrail.dataLayer.database.hiker.likedTrail

import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerTrail
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HikerLikedTrailRepositoryImpl @Inject constructor(
    private val databaseService: HikerLikedTrailDatabaseService,
) : HikerLikedTrailRepository {

    override suspend fun getLikedTrails(hikerId: String): List<HikerTrail> =
        databaseOperation {
            databaseService
                .getLikedTrails(hikerId = hikerId)
                .get()
                .await()
                .toObjects(HikerTrail::class.java)
        }

    override suspend fun addOrUpdateLikedTrail(hikerId: String, trail: HikerTrail) {
        databaseOperation {
            databaseService
                .addOrUpdateLikedTrail(hikerId = hikerId, trail = trail)
                ?.await()
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