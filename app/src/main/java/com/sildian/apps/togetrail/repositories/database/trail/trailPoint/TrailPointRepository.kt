package com.sildian.apps.togetrail.repositories.database.trail.trailPoint

import com.sildian.apps.togetrail.repositories.database.entities.trail.TrailPoint

interface TrailPointRepository {
    suspend fun getAllTrailPoints(trailId: String): List<TrailPoint>
    suspend fun addTrailPoints(trailId: String, trailPoints: List<TrailPoint>)
}