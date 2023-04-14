package com.sildian.apps.togetrail.repositories.database.hiker.markedTrail

import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail

interface HikerMarkedTrailRepository {
    suspend fun getMarkedTrails(hikerId: String): List<Trail>
    suspend fun updateMarkedTrail(hikerId: String, trail: Trail)
    suspend fun deleteMarkedTrail(hikerId: String, trailId: String)
}