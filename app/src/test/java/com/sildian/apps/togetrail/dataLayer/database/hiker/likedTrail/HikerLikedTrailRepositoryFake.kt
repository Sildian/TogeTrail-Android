package com.sildian.apps.togetrail.dataLayer.database.hiker.likedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerTrail
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.nextHikerTrailsList
import kotlin.random.Random

class HikerLikedTrailRepositoryFake(
    private val error: DatabaseException? = null,
    private val trails: List<HikerTrail> = Random.nextHikerTrailsList(),
) : HikerLikedTrailRepository {

    var addOrUpdateLikedTrailSuccessCount: Int = 0 ; private set
    var deleteLikedTrailSuccessCount: Int = 0 ; private set

    override suspend fun getLikedTrails(hikerId: String): List<HikerTrail> =
        error?.let { throw it } ?: trails

    override suspend fun addOrUpdateLikedTrail(hikerId: String, trail: HikerTrail) {
        error?.let { throw it } ?: addOrUpdateLikedTrailSuccessCount++
    }

    override suspend fun deleteLikedTrail(hikerId: String, trailId: String) {
        error?.let { throw it } ?: deleteLikedTrailSuccessCount++
    }
}