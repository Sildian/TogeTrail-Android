package com.sildian.apps.togetrail.repositories.database.event.registeredHiker

import com.sildian.apps.togetrail.repositories.database.entities.hiker.Hiker

interface EventRegisteredHikerRepository {
    suspend fun getRegisteredHikers(eventId: String): List<Hiker>
    suspend fun updateRegisteredHiker(eventId: String, hiker: Hiker)
    suspend fun deleteRegisteredHiker(eventId: String, hikerId: String)
}