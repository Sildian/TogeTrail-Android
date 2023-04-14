package com.sildian.apps.togetrail.repositories.database.hiker.likedTrail

import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail

interface HikerLikedTrailRepository {
    suspend fun getLikedTrails(hikerId: String): List<Trail>
    suspend fun updateLikedTrail(hikerId: String, trail: Trail)
    suspend fun deleteLikedTrail(hikerId: String, trailId: String)
}