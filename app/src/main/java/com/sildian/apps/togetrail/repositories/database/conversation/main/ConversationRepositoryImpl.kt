package com.sildian.apps.togetrail.repositories.database.conversation.main

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Conversation
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val databaseService: ConversationDatabaseService,
) : ConversationRepository {

    override suspend fun getConversation(id: String): Conversation =
        databaseOperation {
            databaseService
                .getConversation(id = id)
                .get()
                .await()
                .toObject(Conversation::class.java)
                ?: throw DatabaseException.UnknownException()
        }

    override suspend fun addConversation(conversation: Conversation) {
        databaseOperation {
            databaseService
                .addConversation(conversation = conversation)
                .await()
        }
    }

    override suspend fun deleteConversation(id: String) {
        databaseOperation {
            databaseService
                .deleteConversation(id = id)
                .await()
        }
    }
}