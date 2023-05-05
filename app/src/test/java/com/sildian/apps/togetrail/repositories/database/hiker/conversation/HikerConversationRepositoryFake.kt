package com.sildian.apps.togetrail.repositories.database.hiker.conversation

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerConversation
import com.sildian.apps.togetrail.repositories.database.entities.hiker.nextHikerConversationsList
import kotlin.random.Random

class HikerConversationRepositoryFake(
    private val error: DatabaseException? = null,
    private val conversations: List<HikerConversation> = Random.nextHikerConversationsList(),
) : HikerConversationRepository {

    var updateConversationSuccessCount: Int = 0 ; private set
    var deleteConversationSuccessCount: Int = 0 ; private set

    override suspend fun getConversations(hikerId: String): List<HikerConversation> =
        error?.let { throw it } ?: conversations

    override suspend fun updateConversation(hikerId: String, conversation: HikerConversation) {
        error?.let { throw it } ?: updateConversationSuccessCount++
    }

    override suspend fun deleteConversation(hikerId: String, conversationId: String) {
        error?.let { throw it } ?: deleteConversationSuccessCount++
    }
}