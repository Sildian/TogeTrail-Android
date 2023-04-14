package com.sildian.apps.togetrail.repositories.database.event.message

import com.sildian.apps.togetrail.repositories.database.entities.conversation.Message

interface EventMessageRepository {
    suspend fun getMessages(eventId: String): List<Message>
    suspend fun addOrUpdateMessage(eventId: String, message: Message)
    suspend fun deleteMessage(eventId: String, messageId: String)
}