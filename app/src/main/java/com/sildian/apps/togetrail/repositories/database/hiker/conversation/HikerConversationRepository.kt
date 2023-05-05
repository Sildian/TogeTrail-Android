package com.sildian.apps.togetrail.repositories.database.hiker.conversation

import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerConversation

interface HikerConversationRepository {
    suspend fun getConversations(hikerId: String): List<HikerConversation>
    suspend fun addOrUpdateConversation(hikerId: String, conversation: HikerConversation)
    suspend fun deleteConversation(hikerId: String, conversationId: String)
}