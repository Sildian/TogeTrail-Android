package com.sildian.apps.togetrail.repositories.database.trail.main

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrailRepositoryImpl @Inject constructor(
    private val databaseService: TrailDatabaseService,
) : TrailRepository {

    override suspend fun getTrail(id: String): Trail =
        databaseOperation {
            databaseService
                .getTrail(id = id)
                .get()
                .await()
                .toObject(Trail::class.java)
                ?: throw DatabaseException.UnknownException()
        }

    override suspend fun getLastTrails(): List<Trail> =
        databaseOperation {
            databaseService
                .getLastTrails()
                .get()
                .await()
                .toObjects(Trail::class.java)
        }

    override suspend fun getTrailsFromAuthor(authorId: String): List<Trail> =
        databaseOperation {
            databaseService
                .getTrailsFromAuthor(authorId = authorId)
                .get()
                .await()
                .toObjects(Trail::class.java)
        }

    override suspend fun getTrailsNearbyLocation(location: Location): List<Trail> =
        databaseOperation {
            databaseService
                .getTrailsNearbyLocation(location = location)
                ?.get()
                ?.await()
                ?.toObjects(Trail::class.java)
                ?: throw DatabaseException.UnknownException()
        }

    override suspend fun getTrailsAroundPosition(
        position: GeoLocation,
        radiusMeters: Double)
    : List<Trail> =
        databaseOperation {
            databaseService
                .getTrailsAroundPosition(position = position, radiusMeters = radiusMeters)
                .map { it.get().await().toObjects(Trail::class.java) }
                .flatten()
                .filter {
                    it.position != null &&
                    GeoFireUtils.getDistanceBetween(it.position, position) <= radiusMeters
                }
        }

    override suspend fun addTrail(trail: Trail) {
        databaseOperation {
            databaseService
                .addTrail(trail = trail)
                .await()
        }
    }

    override suspend fun updateTrail(trail: Trail) {
        databaseOperation {
            databaseService
                .updateTrail(trail = trail)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }
}