package com.sildian.apps.togetrail.repositories.database.conversation.message

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Message

interface ConversationMessageRepository {
    @Throws(DatabaseException::class)
    suspend fun getMessages(conversationId: String): List<Message>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateMessage(conversationId: String, message: Message)
    @Throws(DatabaseException::class)
    suspend fun deleteMessage(conversationId: String, messageId: String)
}