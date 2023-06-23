package com.sildian.apps.togetrail.dataLayer.database.trail.trailPoint

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.TrailPoint
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.nextTrailPointsList
import kotlin.random.Random

class TrailPointRepositoryFake(
    private val error: DatabaseException? = null,
    private val trailPoints: List<TrailPoint> = Random.nextTrailPointsList(),
) : TrailPointRepository {

    var addTrailPointsSuccessCount: Int = 0 ; private set

    override suspend fun getAllTrailPoints(trailId: String): List<TrailPoint> =
        error?.let { throw it } ?: trailPoints

    override suspend fun addTrailPoints(trailId: String, trailPoints: List<TrailPoint>) {
        error?.let { throw it } ?: addTrailPointsSuccessCount++
    }
}