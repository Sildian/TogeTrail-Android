package com.sildian.apps.togetrail.repositories.database.hiker.markedTrail

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerTrail
import com.sildian.apps.togetrail.repositories.database.entities.hiker.nextHikerTrailsList
import kotlin.random.Random

class HikerMarkedTrailRepositoryFake(
    private val error: DatabaseException? = null,
    private val trails: List<HikerTrail> = Random.nextHikerTrailsList(),
) : HikerMarkedTrailRepository {

    var updateMarkedTrailSuccessCount: Int = 0 ; private set
    var deleteMarkedTrailSuccessCount: Int = 0 ; private set

    override suspend fun getMarkedTrails(hikerId: String): List<HikerTrail> =
        error?.let { throw it } ?: trails

    override suspend fun updateMarkedTrail(hikerId: String, trail: HikerTrail) {
        error?.let { throw it } ?: updateMarkedTrailSuccessCount++
    }

    override suspend fun deleteMarkedTrail(hikerId: String, trailId: String) {
        error?.let { throw it } ?: deleteMarkedTrailSuccessCount++
    }
}