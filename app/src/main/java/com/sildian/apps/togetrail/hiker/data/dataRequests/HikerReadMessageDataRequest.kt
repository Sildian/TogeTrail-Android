package com.sildian.apps.togetrail.hiker.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Marks the last message as read
 * within the chat room between the chat holder (current hiker) and the given interlocutor
 ************************************************************************************************/

class HikerReadMessageDataRequest(
    dispatcher: CoroutineDispatcher,
    private val interlocutor: Hiker?,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest(dispatcher) {

    override suspend fun run() {
        CurrentHikerInfo.currentHiker?.let { chatHolder ->
            interlocutor?.let { interlocutor ->
                hikerRepository.getChatBetweenUsers(chatHolder.id, interlocutor.id)?.let { chat ->
                    chat.lastMessageReadId = chat.lastMessage?.id
                    chat.nbUnreadMessages = 0
                    hikerRepository.createOrUpdateHikerChat(chatHolder.id, chat)
                }
            } ?:
            throw NullPointerException("Cannot read any message from a null interlocutor")
        } ?:
        throw NullPointerException("Cannot read any message when the current hiker is null")
    }
}