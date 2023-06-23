package com.sildian.apps.togetrail.dataLayer.database.trail.main

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.Trail
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.nextTrailsList
import kotlin.random.Random

class TrailRepositoryFake(
    private val error: DatabaseException? = null,
    private val trails: List<Trail> = Random.nextTrailsList(),
) : TrailRepository {

    var addTrailSuccessCount: Int = 0 ; private set
    var updateTrailSuccessCount: Int = 0 ; private set

    override suspend fun getTrail(id: String): Trail =
        error?.let { throw it } ?: trails.first()

    override suspend fun getTrails(ids: List<String>): List<Trail> =
        error?.let { throw it } ?: trails

    override suspend fun getLastTrails(): List<Trail> =
        error?.let { throw it } ?: trails

    override suspend fun getTrailsFromAuthor(authorId: String): List<Trail> =
        error?.let { throw it } ?: trails

    override suspend fun getTrailsNearbyLocation(location: Location): List<Trail> =
        error?.let { throw it } ?: trails

    override suspend fun getTrailsAroundPosition(
        position: GeoLocation,
        radiusMeters: Double
    ): List<Trail> =
        error?.let { throw it } ?: trails

    override suspend fun addTrail(trail: Trail) {
        error?.let { throw it } ?: addTrailSuccessCount++
    }

    override suspend fun updateTrail(trail: Trail) {
        error?.let { throw it } ?: updateTrailSuccessCount++
    }
}