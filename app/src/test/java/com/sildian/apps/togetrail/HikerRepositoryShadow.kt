package com.sildian.apps.togetrail

import com.google.firebase.FirebaseException
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
}