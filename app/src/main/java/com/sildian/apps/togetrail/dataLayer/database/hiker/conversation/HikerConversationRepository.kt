package com.sildian.apps.togetrail.dataLayer.database.hiker.conversation

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerConversation

interface HikerConversationRepository {
    @Throws(DatabaseException::class)
    suspend fun getConversations(hikerId: String): List<HikerConversation>
    @Throws(DatabaseException::class)
    suspend fun addOrUpdateConversation(hikerId: String, conversation: HikerConversation)
    @Throws(DatabaseException::class)
    suspend fun deleteConversation(hikerId: String, conversationId: String)
}