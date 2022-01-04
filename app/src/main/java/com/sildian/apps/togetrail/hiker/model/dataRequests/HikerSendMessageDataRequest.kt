package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository

/*************************************************************************************************
 * Sends a message from the sender (current hiker) to the given recipient
 * The message is saved within both sender and recipient's private chat rooms
 ************************************************************************************************/

class HikerSendMessageDataRequest(
    private val recipient: Hiker?,
    private val text: String,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest() {

    override suspend fun run() {
        CurrentHikerInfo.currentHiker?.let { sender ->
            recipient?.let { recipient ->
                text.takeIf { it.isNotEmpty() }?.let {
                    val chatSenderToRecipient = getOrCreateChat(sender, recipient)
                    val chatRecipientToSender = getOrCreateChat(recipient, sender)
                    buildMessage()?.let { message ->
                        postMessage(sender, recipient, message)
                        postMessage(recipient, sender, message)
                        notifyChat(chatSenderToRecipient, message)
                        notifyChat(chatRecipientToSender, message)
                    }
                } ?:
                throw IllegalArgumentException("Cannot send a message with an empty text")
            } ?:
            throw NullPointerException("Cannot send a message to a null recipient")
        } ?:
        throw NullPointerException("Cannot send a message when the current hiker is null")
    }

    private suspend fun getOrCreateChat(chatHolder: Hiker, interlocutor: Hiker): Duo =
        hikerRepository.getChatBetweenUsers(chatHolder.id, interlocutor.id)?.apply {
            interlocutorName = interlocutor.name
            interlocutorPhotoUrl = interlocutor.photoUrl
        } ?:
        Duo(interlocutor.id, chatHolder.id, interlocutor.name, interlocutor.photoUrl).also { chat ->
            hikerRepository.createOrUpdateHikerChat(chatHolder.id, chat)
        }

    private fun buildMessage(): Message? =
        CurrentHikerInfo.currentHiker?.let { sender ->
            Message(
                text = text,
                authorId = sender.id,
                authorName = sender.name,
                authorPhotoUrl = sender.photoUrl
            )
        }

    private suspend fun postMessage(chatHolder: Hiker, interlocutor: Hiker, message: Message) {
        hikerRepository.createOrUpdateHikerMessage(chatHolder.id, interlocutor.id, message)
    }

    private suspend fun notifyChat(chat: Duo, message: Message) {
        chat.lastMessage = message
        if (chat.userId == CurrentHikerInfo.currentHiker?.id) {
            chat.lastMessageReadId = message.id
        } else {
            chat.nbUnreadMessages++
        }
        hikerRepository.createOrUpdateHikerChat(chat.userId, chat)
    }
}