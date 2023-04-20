package com.sildian.apps.togetrail.repositories.database.hiker.main

import com.sildian.apps.togetrail.repositories.database.entities.hiker.Hiker

interface HikerRepository {
    suspend fun getHiker(id: String): Hiker
    suspend fun getHikers(ids: List<String>): List<Hiker>
    suspend fun getHikersWithNameContainingText(text: String, hikerNameToExclude: String = ""): List<Hiker>
    suspend fun addOrUpdateHiker(hiker: Hiker)
    suspend fun deleteHiker(hikerId: String)
}