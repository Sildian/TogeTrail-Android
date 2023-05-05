package com.sildian.apps.togetrail.repositories.database.hiker.attendedEvent

import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerEvent

interface HikerAttendedEventRepository {
    suspend fun getAttendedEvents(hikerId: String): List<HikerEvent>
    suspend fun addOrUpdateAttendedEvent(hikerId: String, event: HikerEvent)
    suspend fun deleteAttendedEvent(hikerId: String, eventId: String)
}