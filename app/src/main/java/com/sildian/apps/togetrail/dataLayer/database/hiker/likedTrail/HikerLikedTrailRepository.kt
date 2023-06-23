package com.sildian.apps.togetrail.dataLayer.database.hiker.likedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerTrail

interface HikerLikedTrailRepository {
    @Throws(DatabaseException::class)
    suspend fun getLikedTrails(hikerId: String): List<HikerTrail>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateLikedTrail(hikerId: String, trail: HikerTrail)
    @Throws(DatabaseException::class)
    suspend fun deleteLikedTrail(hikerId: String, trailId: String)
}