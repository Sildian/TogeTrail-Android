package com.sildian.apps.togetrail.repositories.database.trail.trailPointOfInterest

import com.sildian.apps.togetrail.repositories.database.entities.trail.TrailPointOfInterest

interface TrailPointOfInterestRepository {
    suspend fun getAllTrailPointsOfInterest(trailId: String): List<TrailPointOfInterest>
    suspend fun addTrailPointsOfInterest(trailId: String, trailPointsOfInterest: List<TrailPointOfInterest>)
    suspend fun updateTrailPointOfInterest(trailId: String, trailPointOfInterest: TrailPointOfInterest)
}