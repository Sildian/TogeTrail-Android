package com.sildian.apps.togetrail.repositories.database.hiker.markedTrail

import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerTrail

interface HikerMarkedTrailRepository {
    suspend fun getMarkedTrails(hikerId: String): List<HikerTrail>
    suspend fun addOrUpdateMarkedTrail(hikerId: String, trail: HikerTrail)
    suspend fun deleteMarkedTrail(hikerId: String, trailId: String)
}