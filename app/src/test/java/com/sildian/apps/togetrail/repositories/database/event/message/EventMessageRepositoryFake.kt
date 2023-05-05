package com.sildian.apps.togetrail.repositories.database.event.message

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Message
import com.sildian.apps.togetrail.repositories.database.entities.conversation.nextMessagesList
import kotlin.random.Random

class EventMessageRepositoryFake(
    private val error: DatabaseException? = null,
    private val messages: List<Message> = Random.nextMessagesList(),
) : EventMessageRepository {

    var addOrUpdateMessageSuccessCount: Int = 0 ; private set
    var deleteMessageSuccessCount: Int = 0 ; private set

    override suspend fun getMessages(eventId: String): List<Message> =
        error?.let { throw it } ?: messages

    override suspend fun addOrUpdateMessage(eventId: String, message: Message) {
        error?.let { throw it } ?: addOrUpdateMessageSuccessCount++
    }

    override suspend fun deleteMessage(eventId: String, messageId: String) {
        error?.let { throw it } ?: deleteMessageSuccessCount++
    }
}