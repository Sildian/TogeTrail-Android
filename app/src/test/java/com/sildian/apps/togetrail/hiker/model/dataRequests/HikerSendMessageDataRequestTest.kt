package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeHikerRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HikerSendMessageDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.hikers.add(Hiker(id = "HB", name = "Hiker B"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0]
            try {
                HikerSendMessageDataRequest(
                    dispatcher,
                    FirebaseSimulator.hikers[1],
                    "Hello",
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: FirebaseException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.hikerChats.isEmpty())
            assertTrue(FirebaseSimulator.hikerChatMessages.isEmpty())
        }
    }

    @Test
    fun given_nullSender_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.hikers.add(Hiker(id = "HB", name = "Hiker B"))
            try {
                HikerSendMessageDataRequest(
                    dispatcher,
                    FirebaseSimulator.hikers[1],
                    "Hello",
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.hikerChats.isEmpty())
            assertTrue(FirebaseSimulator.hikerChatMessages.isEmpty())
        }
    }

    @Test
    fun given_nullRecipient_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.hikers.add(Hiker(id = "HB", name = "Hiker B"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0]
            try {
                HikerSendMessageDataRequest(
                    dispatcher,
                    null,
                    "Hello",
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.hikerChats.isEmpty())
            assertTrue(FirebaseSimulator.hikerChatMessages.isEmpty())
        }
    }

    @Test
    fun given_emptyText_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.hikers.add(Hiker(id = "HB", name = "Hiker B"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0]
            try {
                HikerSendMessageDataRequest(
                    dispatcher,
                    FirebaseSimulator.hikers[1],
                    "",
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.hikerChats.isEmpty())
            assertTrue(FirebaseSimulator.hikerChatMessages.isEmpty())
        }
    }

    @Test
    fun given_validTextAndChatRoom_when_sendMessage_then_checkMessageIsSentInExistingChatRoom() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.hikers.add(Hiker(id = "HB", name = "Hiker B"))
            FirebaseSimulator.hikerChats["HA"] = arrayListOf(Duo("HB", "HA", "?"))
            FirebaseSimulator.hikerChats["HB"] = arrayListOf(Duo("HA", "HB", "?"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0]
            HikerSendMessageDataRequest(
                dispatcher,
                FirebaseSimulator.hikers[1],
                "Hello",
                FakeHikerRepository()
            ).execute()
            assertEquals(1, FirebaseSimulator.hikerChats["HA"]?.size)
            assertEquals(1, FirebaseSimulator.hikerChats["HB"]?.size)
            assertEquals(1, FirebaseSimulator.hikerChatMessages["HA"]?.get("HB")?.size)
            assertEquals(1, FirebaseSimulator.hikerChatMessages["HB"]?.get("HA")?.size)
            val chatAToB = FirebaseSimulator.hikerChats["HA"]?.get(0)
            assertEquals("HB", chatAToB?.interlocutorId)
            assertEquals("Hiker B", chatAToB?.interlocutorName)
            assertEquals(0, chatAToB?.nbUnreadMessages)
            assertEquals("Hello", chatAToB?.lastMessage?.text)
            val chatBToA = FirebaseSimulator.hikerChats["HB"]?.get(0)
            assertEquals("HA", chatBToA?.interlocutorId)
            assertEquals("Hiker A", chatBToA?.interlocutorName)
            assertEquals(1, chatBToA?.nbUnreadMessages)
            assertEquals("Hello", chatBToA?.lastMessage?.text)
            val messageAToB = FirebaseSimulator.hikerChatMessages["HA"]?.get("HB")?.get(0)
            assertEquals("Hello", messageAToB?.text)
            val messageBToA = FirebaseSimulator.hikerChatMessages["HB"]?.get("HA")?.get(0)
            assertEquals("Hello", messageBToA?.text)
        }
    }

    @Test
    fun given_validTextAndNoChatRoom_when_sendMessage_then_checkMessageIsSentInNewChatRoom() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.hikers.add(Hiker(id = "HB", name = "Hiker B"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0]
            HikerSendMessageDataRequest(
                dispatcher,
                FirebaseSimulator.hikers[1],
                "Hello",
                FakeHikerRepository()
            ).execute()
            assertEquals(1, FirebaseSimulator.hikerChats["HA"]?.size)
            assertEquals(1, FirebaseSimulator.hikerChats["HB"]?.size)
            assertEquals(1, FirebaseSimulator.hikerChatMessages["HA"]?.get("HB")?.size)
            assertEquals(1, FirebaseSimulator.hikerChatMessages["HB"]?.get("HA")?.size)
            val chatAToB = FirebaseSimulator.hikerChats["HA"]?.get(0)
            assertEquals("HB", chatAToB?.interlocutorId)
            assertEquals("Hiker B", chatAToB?.interlocutorName)
            assertEquals(0, chatAToB?.nbUnreadMessages)
            assertEquals("Hello", chatAToB?.lastMessage?.text)
            val chatBToA = FirebaseSimulator.hikerChats["HB"]?.get(0)
            assertEquals("HA", chatBToA?.interlocutorId)
            assertEquals("Hiker A", chatBToA?.interlocutorName)
            assertEquals(1, chatBToA?.nbUnreadMessages)
            assertEquals("Hello", chatBToA?.lastMessage?.text)
            val messageAToB = FirebaseSimulator.hikerChatMessages["HA"]?.get("HB")?.get(0)
            assertEquals("Hello", messageAToB?.text)
            val messageBToA = FirebaseSimulator.hikerChatMessages["HB"]?.get("HA")?.get(0)
            assertEquals("Hello", messageBToA?.text)
        }
    }
}