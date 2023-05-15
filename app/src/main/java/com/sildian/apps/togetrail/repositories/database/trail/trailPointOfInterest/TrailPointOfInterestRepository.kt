package com.sildian.apps.togetrail.repositories.database.trail.trailPointOfInterest

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.trail.TrailPointOfInterest

interface TrailPointOfInterestRepository {
    @Throws(DatabaseException::class)
    suspend fun getAllTrailPointsOfInterest(trailId: String): List<TrailPointOfInterest>
    @Throws(DatabaseException::class)
    suspend fun addTrailPointsOfInterest(trailId: String, trailPointsOfInterest: List<TrailPointOfInterest>)
    @Throws(DatabaseException::class)
    suspend fun updateTrailPointOfInterest(trailId: String, trailPointOfInterest: TrailPointOfInterest)
}