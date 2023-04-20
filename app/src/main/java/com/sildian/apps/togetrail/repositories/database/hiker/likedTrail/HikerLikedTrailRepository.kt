package com.sildian.apps.togetrail.repositories.database.hiker.likedTrail

import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerTrail

interface HikerLikedTrailRepository {
    suspend fun getLikedTrails(hikerId: String): List<HikerTrail>
    suspend fun updateLikedTrail(hikerId: String, trail: HikerTrail)
    suspend fun deleteLikedTrail(hikerId: String, trailId: String)
}