package com.sildian.apps.togetrail.repositories.database.event.message

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Message
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventMessageRepositoryImpl @Inject constructor(
    private val databaseService: EventMessageDatabaseService,
) : EventMessageRepository {

    override suspend fun getMessages(eventId: String): List<Message> =
        databaseOperation {
            databaseService
                .getMessages(eventId = eventId)
                .get()
                .await()
                .toObjects(Message::class.java)
        }

    override suspend fun addOrUpdateMessage(eventId: String, message: Message) {
        databaseOperation {
            databaseService
                .addOrUpdateMessage(eventId = eventId, message = message)
                ?.await()
                ?: throw DatabaseException.UnknownException()
        }
    }

    override suspend fun deleteMessage(eventId: String, messageId: String) {
        databaseOperation {
            databaseService
                .deleteMessage(eventId = eventId, messageId = messageId)
                .await()
        }
    }
}