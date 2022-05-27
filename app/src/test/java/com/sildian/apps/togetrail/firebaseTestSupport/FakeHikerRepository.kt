package com.sildian.apps.togetrail.firebaseTestSupport

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.chat.data.models.Duo
import com.sildian.apps.togetrail.chat.data.models.Message
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryType
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import com.sildian.apps.togetrail.trail.data.models.Trail
import org.mockito.Mockito

/*************************************************************************************************
 * Fake repository for Hiker
 ************************************************************************************************/

class FakeHikerRepository: HikerRepository {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE HikerRepository : Request failure"
    }

    override fun getHikerReference(hikerId: String): DocumentReference =
        Mockito.mock(DocumentReference::class.java)

    override suspend fun getHiker(hikerId: String): Hiker? {
        println("FAKE HikerRepository : Get hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.hikers.firstOrNull { it.id == hikerId }
    }

    override suspend fun updateHiker(hiker: Hiker) {
        println("FAKE HikerRepository : Update hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikers.removeIf { it.id == hiker.id }
        FirebaseSimulator.hikers.add(hiker)
    }

    override suspend fun deleteHiker(hiker: Hiker) {
        println("FAKE HikerRepository : Delete hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikers.removeIf { it.id == hiker.id }
    }

    override suspend fun addHikerHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        println("FAKE HikerRepository : Add history item")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.hikerHistoryItems[hikerId] == null) {
            FirebaseSimulator.hikerHistoryItems[hikerId] = arrayListOf()
        }
        FirebaseSimulator.hikerHistoryItems[hikerId]?.add(historyItem)
    }

    override suspend fun deleteHikerHistoryItems(hikerId: String, type: HikerHistoryType, relatedItemId: String) {
        println("FAKE HikerRepository : Delete history item")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerHistoryItems[hikerId]?.let { historyItems ->
            historyItems.removeIf { it.type == type && it.itemId == relatedItemId }
        }
    }

    override suspend fun updateHikerAttendedEvent(hikerId:String, event: Event) {
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

    override suspend fun deleteHikerAttendedEvent(hikerId: String, eventId: String) {
        println("FAKE HikerRepository : Delete attended event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerAttendedEvents[hikerId]?.removeIf { it.id == eventId }
    }

    override suspend fun updateHikerLikedTrail(hikerId: String, trail: Trail) {
        println("FAKE HikerRepository : Update liked trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.hikerLikedTrails[hikerId] == null) {
            FirebaseSimulator.hikerLikedTrails[hikerId] = arrayListOf()
        }
        FirebaseSimulator.hikerLikedTrails[hikerId]?.removeIf { it.id == trail.id }
        FirebaseSimulator.hikerLikedTrails[hikerId]?.add(trail)
    }

    override suspend fun deleteHikerLikedTrail(hikerId: String, trailId: String) {
        println("FAKE HikerRepository : Delete liked trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerLikedTrails[hikerId]?.removeIf { it.id == trailId }
    }

    override suspend fun updateHikerMarkedTrail(hikerId: String, trail: Trail) {
        println("FAKE HikerRepository : Update marked trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.hikerMarkedTrails[hikerId] == null) {
            FirebaseSimulator.hikerMarkedTrails[hikerId] = arrayListOf()
        }
        FirebaseSimulator.hikerMarkedTrails[hikerId]?.removeIf { it.id == trail.id }
        FirebaseSimulator.hikerMarkedTrails[hikerId]?.add(trail)
    }

    override suspend fun deleteHikerMarkedTrail(hikerId: String, trailId: String) {
        println("FAKE HikerRepository : Delete marked trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerMarkedTrails[hikerId]?.removeIf { it.id == trailId }
    }

    override suspend fun getChatBetweenUsers(hikerId: String, interlocutorId: String): Duo? {
        println("FAKE HikerRepository : Get chat between users")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.hikerChats[hikerId]?.let { chats ->
            chats.firstOrNull { it.interlocutorId == interlocutorId }
        }
    }

    override suspend fun createOrUpdateHikerChat(hikerId: String, duo: Duo) {
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

    override suspend fun deleteHikerChat(hikerId: String, interlocutorId: String) {
        println("FAKE HikerRepository : Delete chat")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerChats[hikerId]?.removeIf { it.interlocutorId == interlocutorId }
    }

    override suspend fun getLastHikerMessage(hikerId: String, interlocutorId: String): Message? {
        println("FAKE HikerRepository : Get last hiker message")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.hikerChatMessages[hikerId]?.get(interlocutorId)?.last()
    }

    override suspend fun createOrUpdateHikerMessage(hikerId: String, interlocutorId: String, message: Message) {
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

    override suspend fun deleteHikerMessage(hikerId: String, interlocutorId: String, messageId: String) {
        println("FAKE HikerRepository : Delete message")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.hikerChatMessages[hikerId]?.get(interlocutorId)?.let { messages ->
            messages.removeIf { it.id == messageId }
        }
    }
}