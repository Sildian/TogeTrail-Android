package com.sildian.apps.togetrail.repositories.database.hiker.conversation

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerConversation
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HikerConversationRepositoryImpl @Inject constructor(
    private val databaseService: HikerConversationDatabaseService,
) : HikerConversationRepository {

    override suspend fun getConversations(hikerId: String): List<HikerConversation> =
        databaseOperation {
            databaseService
                .getConversations(hikerId = hikerId)
                .get()
                .await()
                .toObjects(HikerConversation::class.java)
        }

    override suspend fun updateConversation(hikerId: String, conversation: HikerConversation) {
        databaseOperation {
            databaseService
                .updateConversation(hikerId = hikerId, conversation = conversation)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }

    override suspend fun deleteConversation(hikerId: String, conversationId: String) {
        databaseOperation {
            databaseService
                .deleteConversation(hikerId = hikerId, conversationId = conversationId)
                .await()
        }
    }
}