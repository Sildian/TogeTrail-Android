package com.sildian.apps.togetrail.dataLayer.database.hiker.markedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerTrail

interface HikerMarkedTrailRepository {
    @Throws(DatabaseException::class)
    suspend fun getMarkedTrails(hikerId: String): List<HikerTrail>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateMarkedTrail(hikerId: String, trail: HikerTrail)
    @Throws(DatabaseException::class)
    suspend fun deleteMarkedTrail(hikerId: String, trailId: String)
}