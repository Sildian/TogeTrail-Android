package com.sildian.apps.togetrail.repositories.database.hiker.attendedEvent

import com.sildian.apps.togetrail.repositories.database.entities.event.Event

interface HikerAttendedEventRepository {
    suspend fun getAttendedEvents(hikerId: String): List<Event>
    suspend fun updateAttendedEvent(hikerId: String, event: Event)
    suspend fun deleteAttendedEvent(hikerId: String, eventId: String)
}