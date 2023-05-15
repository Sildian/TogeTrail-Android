package com.sildian.apps.togetrail.repositories.database.conversation.main

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Conversation

interface ConversationRepository {
    @Throws(DatabaseException::class)
    suspend fun getConversation(id: String): Conversation
    @Throws(DatabaseException::class)
    suspend fun addConversation(conversation: Conversation)
    @Throws(DatabaseException::class)
    suspend fun deleteConversation(id: String)
}