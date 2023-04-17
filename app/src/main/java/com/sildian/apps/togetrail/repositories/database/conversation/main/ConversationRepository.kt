package com.sildian.apps.togetrail.repositories.database.conversation.main

import com.sildian.apps.togetrail.repositories.database.entities.conversation.Conversation

interface ConversationRepository {
    suspend fun getConversation(id: String): Conversation
    suspend fun addConversation(conversation: Conversation)
    suspend fun deleteConversation(id: String)
}