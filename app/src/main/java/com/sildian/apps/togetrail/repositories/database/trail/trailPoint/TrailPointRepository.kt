package com.sildian.apps.togetrail.repositories.database.trail.trailPoint

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.trail.TrailPoint

interface TrailPointRepository {
    @Throws(DatabaseException::class)
    suspend fun getAllTrailPoints(trailId: String): List<TrailPoint>
    @Throws(DatabaseException::class)
    suspend fun addTrailPoints(trailId: String, trailPoints: List<TrailPoint>)
}