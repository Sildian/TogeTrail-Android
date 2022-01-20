package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data request tests
 ************************************************************************************************/

@Implements(HikerRepository::class)
class HikerRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE HikerRepository : Request failure"
    }

    @Implementation
    suspend fun getHiker(hikerId: String): Hiker? {
        println("FAKE HikerRepository : Get hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.hikers.firstOrNull { it.id == hikerId }
    }

    @Implementation
    suspend fun updateHiker(hiker: Hiker) {
        println("FAKE HikerRepository : Update hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikers.removeIf { it.id == hiker.id }
        FirebaseSimulator.hikers.add(hiker)
    }

    @Implementation
    suspend fun deleteHiker(hiker: Hiker) {
        println("FAKE HikerRepository : Delete hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikers.removeIf { it.id == hiker.id }
    }

    @Implementation
    suspend fun addHikerHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        println("FAKE HikerRepository : Add history item")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.hikerHistoryItems[hikerId] == null) {
            FirebaseSimulator.hikerHistoryItems[hikerId] = arrayListOf()
        }
        FirebaseSimulator.hikerHistoryItems[hikerId]?.add(historyItem)
    }

    @Implementation
    suspend fun deleteHikerHistoryItems(hikerId: String, type: HikerHistoryType, relatedItemId: String) {
        println("FAKE HikerRepository : Delete history item")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerHistoryItems[hikerId]?.let { historyItems ->
            historyItems.removeIf { it.type == type && it.itemId == relatedItemId }
        }
    }

    @Implementation
    suspend fun updateHikerAttendedEvent(hikerId:String, event: Event) {
        println("FAKE HikerRepository : Update attended event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.hikerAttendedEvents[hikerId] == null) {
            FirebaseSimulator.hikerAttendedEvents[hikerId] = arrayListOf()
        }
        FirebaseSimulator.hikerAttendedEvents[hikerId]?.removeIf { it.id == event.id }
        FirebaseSimulator.hikerAttendedEvents[hikerId]?.add(event)
    }

    @Implementation
    suspend fun deleteHikerAttendedEvent(hikerId: String, eventId: String) {
        println("FAKE HikerRepository : Delete attended event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerAttendedEvents[hikerId]?.removeIf { it.id == eventId }
    }

    @Implementation
    suspend fun getChatBetweenUsers(hikerId: String, interlocutorId: String): Duo? {
        println("FAKE HikerRepository : Get chat between users")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.hikerChats[hikerId]?.let { chats ->
            chats.firstOrNull { it.interlocutorId == interlocutorId }
        }
    }

    @Implementation
    suspend fun createOrUpdateHikerChat(hikerId: String, duo: Duo) {
        println("FAKE HikerRepository : Create or update chat")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.hikerChats[hikerId] == null) {
            FirebaseSimulator.hikerChats[hikerId] = arrayListOf()
        }
        FirebaseSimulator.hikerChats[hikerId]?.removeIf { it.interlocutorId == duo.interlocutorId }
        FirebaseSimulator.hikerChats[hikerId]?.add(duo)
    }

    @Implementation
    suspend fun deleteHikerChat(hikerId: String, interlocutorId: String) {
        println("FAKE HikerRepository : Delete chat")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerChats[hikerId]?.removeIf { it.interlocutorId == interlocutorId }
    }

    @Implementation
    suspend fun getLastHikerMessage(hikerId: String, interlocutorId: String): Message? {
        println("FAKE HikerRepository : Get last hiker message")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.hikerChatMessages[hikerId]?.get(interlocutorId)?.last()
    }

    @Implementation
    suspend fun createOrUpdateHikerMessage(hikerId: String, interlocutorId: String, message: Message) {
        println("FAKE HikerRepository : Create of update message")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.hikerChatMessages[hikerId] == null) {
            FirebaseSimulator.hikerChatMessages[hikerId] = hashMapOf()
        }
        FirebaseSimulator.hikerChatMessages[hikerId]?.let { chats ->
            if (!chats.containsKey(interlocutorId)) {
                chats[interlocutorId] = arrayListOf()
            }
            chats[interlocutorId]?.removeIf { it.id == message.id }
            chats[interlocutorId]?.add(message)
        }
    }

    @Implementation
    suspend fun deleteHikerMessage(hikerId: String, interlocutorId: String, messageId: String) {
        println("FAKE HikerRepository : Delete message")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerChatMessages[hikerId]?.get(interlocutorId)?.let { messages ->
            messages.removeIf { it.id == messageId }
        }
    }
}