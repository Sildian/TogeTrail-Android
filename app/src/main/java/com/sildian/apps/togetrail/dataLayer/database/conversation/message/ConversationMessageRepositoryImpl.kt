package com.sildian.apps.togetrail.dataLayer.database.conversation.message

import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.Message
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConversationMessageRepositoryImpl @Inject constructor(
    private val databaseService: ConversationMessageDatabaseService,
) : ConversationMessageRepository {

    override suspend fun getMessages(conversationId: String): List<Message> =
        databaseOperation {
            databaseService
                .getMessages(conversationId = conversationId)
                .get()
                .await()
                .toObjects(Message::class.java)
        }

    override suspend fun addOrUpdateMessage(conversationId: String, message: Message) {
        databaseOperation {
            databaseService
                .addOrUpdateMessage(conversationId = conversationId, message = message)
                ?.await()
        }
    }

    override suspend fun deleteMessage(conversationId: String, messageId: String) {
        databaseOperation {
            databaseService
                .deleteMessage(conversationId = conversationId, messageId = messageId)
                .await()
        }
    }
}