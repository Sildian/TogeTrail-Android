package com.sildian.apps.togetrail.dataLayer.database.conversation.message

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.Message
import com.sildian.apps.togetrail.dataLayer.database.entities.conversation.nextMessagesList
import kotlin.random.Random

class ConversationMessageRepositoryFake(
    private val error: DatabaseException? = null,
    private val messages: List<Message> = Random.nextMessagesList(),
) : ConversationMessageRepository {

    var addOrUpdateMessageSuccessCount: Int = 0 ; private set
    var deleteMessageSuccessCount: Int = 0 ; private set

    override suspend fun getMessages(conversationId: String): List<Message> =
        error?.let { throw it } ?: messages

    override suspend fun addOrUpdateMessage(conversationId: String, message: Message) {
        error?.let { throw it } ?: addOrUpdateMessageSuccessCount++
    }

    override suspend fun deleteMessage(conversationId: String, messageId: String) {
        error?.let { throw it } ?: deleteMessageSuccessCount++
    }
}