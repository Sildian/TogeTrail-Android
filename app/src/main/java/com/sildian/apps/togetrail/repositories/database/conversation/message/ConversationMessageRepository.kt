package com.sildian.apps.togetrail.repositories.database.conversation.message

import com.sildian.apps.togetrail.repositories.database.entities.conversation.Message

interface ConversationMessageRepository {
    suspend fun getMessages(conversationId: String): List<Message>
    suspend fun addOrUpdateMessage(conversationId: String, message: Message)
    suspend fun deleteMessage(conversationId: String, messageId: String)
}