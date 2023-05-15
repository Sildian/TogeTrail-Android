package com.sildian.apps.togetrail.repositories.database.hiker.main

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.hiker.Hiker

interface HikerRepository {
    @Throws(DatabaseException::class)
    suspend fun getHiker(id: String): Hiker
    @Throws(DatabaseException::class)
    suspend fun getHikers(ids: List<String>): List<Hiker>
    @Throws(DatabaseException::class)
    suspend fun getHikersWithNameContainingText(text: String, hikerNameToExclude: String = ""): List<Hiker>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateHiker(hiker: Hiker)
    @Throws(DatabaseException::class)
    suspend fun deleteHiker(hikerId: String)
}