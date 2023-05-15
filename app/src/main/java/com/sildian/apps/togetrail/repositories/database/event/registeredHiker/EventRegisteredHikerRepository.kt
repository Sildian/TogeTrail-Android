package com.sildian.apps.togetrail.repositories.database.event.registeredHiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.event.EventHiker

interface EventRegisteredHikerRepository {
    @Throws(DatabaseException::class)
    suspend fun getRegisteredHikers(eventId: String): List<EventHiker>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateRegisteredHiker(eventId: String, hiker: EventHiker)
    @Throws(DatabaseException::class)
    suspend fun deleteRegisteredHiker(eventId: String, hikerId: String)
}