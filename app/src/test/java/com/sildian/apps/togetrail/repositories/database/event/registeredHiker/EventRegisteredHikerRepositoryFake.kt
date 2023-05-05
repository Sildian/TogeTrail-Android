package com.sildian.apps.togetrail.repositories.database.event.registeredHiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.event.EventHiker
import com.sildian.apps.togetrail.repositories.database.entities.event.nextEventHikersList
import kotlin.random.Random

class EventRegisteredHikerRepositoryFake(
    private val error: DatabaseException? = null,
    private val hikers: List<EventHiker> = Random.nextEventHikersList(),
) : EventRegisteredHikerRepository {

    var addOrUpdateRegisteredHikerSuccessCount: Int = 0 ; private set
    var deleteRegisteredHikerSuccessCount: Int = 0 ; private set

    override suspend fun getRegisteredHikers(eventId: String): List<EventHiker> =
        error?.let { throw it } ?: hikers

    override suspend fun addOrUpdateRegisteredHiker(eventId: String, hiker: EventHiker) {
        error?.let { throw it } ?: addOrUpdateRegisteredHikerSuccessCount++
    }

    override suspend fun deleteRegisteredHiker(eventId: String, hikerId: String) {
        error?.let { throw it } ?: deleteRegisteredHikerSuccessCount++
    }
}