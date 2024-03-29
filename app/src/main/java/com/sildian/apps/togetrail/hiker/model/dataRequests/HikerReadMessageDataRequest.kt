package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository

/*************************************************************************************************
 * Marks the last message as read
 * within the chat room between the chat holder (current hiker) and the given interlocutor
 ************************************************************************************************/

class HikerReadMessageDataRequest(
    private val interlocutor: Hiker?,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest() {

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