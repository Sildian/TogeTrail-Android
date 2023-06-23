package com.sildian.apps.togetrail.dataLayer.database.event.message

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.Message

interface EventMessageRepository {
    @Throws(DatabaseException::class)
    suspend fun getMessages(eventId: String): List<Message>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateMessage(eventId: String, message: Message)
    @Throws(DatabaseException::class)
    suspend fun deleteMessage(eventId: String, messageId: String)
}