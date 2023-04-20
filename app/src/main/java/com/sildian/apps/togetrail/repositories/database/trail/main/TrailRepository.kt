package com.sildian.apps.togetrail.repositories.database.trail.main

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail

interface TrailRepository {
    suspend fun getTrail(id: String): Trail
    suspend fun getTrails(ids: List<String>): List<Trail>
    suspend fun getLastTrails(): List<Trail>
    suspend fun getTrailsFromAuthor(authorId: String): List<Trail>
    suspend fun getTrailsNearbyLocation(location: Location): List<Trail>
    suspend fun getTrailsAroundPosition(position: GeoLocation, radiusMeters: Double): List<Trail>
    suspend fun addTrail(trail: Trail)
    suspend fun updateTrail(trail: Trail)
}