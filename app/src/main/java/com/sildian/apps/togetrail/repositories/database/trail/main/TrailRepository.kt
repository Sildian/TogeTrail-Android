package com.sildian.apps.togetrail.repositories.database.trail.main

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail

interface TrailRepository {
    @Throws(DatabaseException::class)
    suspend fun getTrail(id: String): Trail
    @Throws(DatabaseException::class)
    suspend fun getTrails(ids: List<String>): List<Trail>
    @Throws(DatabaseException::class)
    suspend fun getLastTrails(): List<Trail>
    @Throws(DatabaseException::class)
    suspend fun getTrailsFromAuthor(authorId: String): List<Trail>
    @Throws(DatabaseException::class)
    suspend fun getTrailsNearbyLocation(location: Location): List<Trail>
    @Throws(DatabaseException::class)
    suspend fun getTrailsAroundPosition(position: GeoLocation, radiusMeters: Double): List<Trail>
    @Throws(DatabaseException::class)
    suspend fun addTrail(trail: Trail)
    @Throws(DatabaseException::class)
    suspend fun updateTrail(trail: Trail)
}