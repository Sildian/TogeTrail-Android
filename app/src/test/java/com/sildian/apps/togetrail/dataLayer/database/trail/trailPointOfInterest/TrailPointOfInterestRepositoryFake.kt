package com.sildian.apps.togetrail.dataLayer.database.trail.trailPointOfInterest

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.TrailPointOfInterest
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.nextTrailPointsOfInterestList
import kotlin.random.Random

class TrailPointOfInterestRepositoryFake(
    private val error: DatabaseException? = null,
    private val trailPointsOfInterest: List<TrailPointOfInterest> = Random.nextTrailPointsOfInterestList(),
) : TrailPointOfInterestRepository {

    var addTrailPointsOfInterestSuccessCount: Int = 0 ; private set
    var updateTrailPointOfInterestSuccessCount: Int = 0 ; private set

    override suspend fun getAllTrailPointsOfInterest(trailId: String): List<TrailPointOfInterest> =
        error?.let { throw it } ?: trailPointsOfInterest

    override suspend fun addTrailPointsOfInterest(
        trailId: String,
        trailPointsOfInterest: List<TrailPointOfInterest>
    ) {
        error?.let { throw it } ?: addTrailPointsOfInterestSuccessCount++
    }

    override suspend fun updateTrailPointOfInterest(
        trailId: String,
        trailPointOfInterest: TrailPointOfInterest
    ) {
        error?.let { throw it } ?: updateTrailPointOfInterestSuccessCount++
    }
}