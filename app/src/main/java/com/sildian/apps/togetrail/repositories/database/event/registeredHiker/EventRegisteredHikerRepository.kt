package com.sildian.apps.togetrail.repositories.database.event.registeredHiker

import com.sildian.apps.togetrail.repositories.database.entities.event.EventHiker

interface EventRegisteredHikerRepository {
    suspend fun getRegisteredHikers(eventId: String): List<EventHiker>
    suspend fun addOrUpdateRegisteredHiker(eventId: String, hiker: EventHiker)
    suspend fun deleteRegisteredHiker(eventId: String, hikerId: String)
}