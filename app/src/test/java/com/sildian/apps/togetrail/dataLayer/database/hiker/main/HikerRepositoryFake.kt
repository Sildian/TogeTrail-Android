package com.sildian.apps.togetrail.dataLayer.database.hiker.main

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.Hiker
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.nextHikersList
import kotlin.random.Random

class HikerRepositoryFake(
    private val error: DatabaseException? = null,
    private val hikers: List<Hiker> = Random.nextHikersList(),
) : HikerRepository {

    var addOrUpdateHikerSuccessCount: Int = 0 ; private set
    var deleteHikerSuccessCount: Int = 0 ; private set

    override suspend fun getHiker(id: String): Hiker =
        error?.let { throw it } ?: hikers.first()

    override suspend fun getHikers(ids: List<String>): List<Hiker> =
        error?.let { throw it } ?: hikers

    override suspend fun getHikersWithNameContainingText(
        text: String,
        hikerNameToExclude: String
    ): List<Hiker> =
        error?.let { throw it } ?: hikers

    override suspend fun addOrUpdateHiker(hiker: Hiker) {
        error?.let { throw it } ?: addOrUpdateHikerSuccessCount++
    }

    override suspend fun deleteHiker(hikerId: String) {
        error?.let { throw it } ?: deleteHikerSuccessCount++
    }
}