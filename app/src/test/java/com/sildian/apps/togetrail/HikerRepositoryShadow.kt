package com.sildian.apps.togetrail

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(HikerRepository::class)
class HikerRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE HikerRepository : Request failure"
    }

    @Implementation
    suspend fun getHiker(hikerId: String): Hiker? {
        println("FAKE HikerRepository : Get hiker")
        if (!BaseDataRequesterTest.requestShouldFail) {
            return BaseDataRequesterTest.getHikerSample()
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun updateHiker(hiker: Hiker) {
        println("FAKE HikerRepository : Update hiker")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerUpdated = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteHiker(hiker: Hiker) {
        println("FAKE HikerRepository : Delete hiker")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerDeleted = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun addHikerHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        println("FAKE HikerRepository : Add history item")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerHistoryItemAdded = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteHikerHistoryItems(hikerId: String, type: HikerHistoryType, relatedItemId: String) {
        println("FAKE HikerRepository : Delete history item")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerHistoryItemDeleted = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun updateHikerAttendedEvent(hikerId:String, event: Event) {
        println("FAKE HikerRepository : Update attended event")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerRegisteredToEvent = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteHikerAttendedEvent(hikerId: String, eventId: String) {
        println("FAKE HikerRepository : Delete attended event")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerUnregisteredFromEvent = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun getChatBetweenUsers(hikerId: String, interlocutorId: String): Duo? {
        println("FAKE HikerRepository : Get chat between users")
        if (!BaseDataRequesterTest.requestShouldFail) {
            return BaseDataRequesterTest.getDuoSample()
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun createOrUpdateHikerChat(hikerId: String, duo: Duo) {
        println("FAKE HikerRepository : Create or update chat")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerChatUpdated = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteHikerChat(hikerId: String, interlocutorId: String) {
        println("FAKE HikerRepository : Delete chat")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerChatDeleted = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun getLastHikerMessage(hikerId: String, interlocutorId: String): Message? {
        println("FAKE HikerRepository : Get last hiker message")
        if (!BaseDataRequesterTest.requestShouldFail) {
            return BaseDataRequesterTest.getMessageSample()
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun createOrUpdateHikerMessage(hikerId: String, interlocutorId: String, message: Message) {
        println("FAKE HikerRepository : Create of update message")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerMessageSent = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteHikerMessage(hikerId: String, interlocutorId: String, messageId: String) {
        println("FAKE HikerRepository : Delete message")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isHikerMessageDeleted = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }
}