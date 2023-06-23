package com.sildian.apps.togetrail.dataLayer.database.conversation.main

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.Conversation

interface ConversationRepository {
    @Throws(DatabaseException::class)
    suspend fun getConversation(id: String): Conversation
    @Throws(DatabaseException::class)
    suspend fun addConversation(conversation: Conversation)
    @Throws(DatabaseException::class)
    suspend fun deleteConversation(id: String)
}