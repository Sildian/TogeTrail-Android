package com.sildian.apps.togetrail.dataLayer.database.hiker.attendedEvent

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerEvent

interface HikerAttendedEventRepository {
    @Throws(DatabaseException::class)
    suspend fun getAttendedEvents(hikerId: String): List<HikerEvent>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateAttendedEvent(hikerId: String, event: HikerEvent)
    @Throws(DatabaseException::class)
    suspend fun deleteAttendedEvent(hikerId: String, eventId: String)
}