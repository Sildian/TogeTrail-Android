package com.sildian.apps.togetrail.repositories.database.conversation.main

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Conversation
import com.sildian.apps.togetrail.repositories.database.entities.conversation.nextConversation
import kotlin.random.Random

class ConversationRepositoryFake(
    private val error: DatabaseException? = null,
    private val conversation: Conversation = Random.nextConversation(),
) : ConversationRepository {

    var addConversationSuccessCount: Int = 0 ; private set
    var deleteConversationSuccessCount: Int = 0 ; private set

    override suspend fun getConversation(id: String): Conversation =
        error?.let { throw it } ?: conversation

    override suspend fun addConversation(conversation: Conversation) {
        error?.let { throw it } ?: addConversationSuccessCount++
    }

    override suspend fun deleteConversation(id: String) {
        error?.let { throw it } ?: deleteConversationSuccessCount++
    }
}