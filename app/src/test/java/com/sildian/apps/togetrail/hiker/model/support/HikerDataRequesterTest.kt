package com.sildian.apps.togetrail.hiker.model.support

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.BaseDataRequesterTest
import com.sildian.apps.togetrail.chat.model.core.Message
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class HikerDataRequesterTest: BaseDataRequesterTest() {

    private val hikerDataRequester = HikerDataRequester()

    @Test
    fun given_requestFailure_when_loadHikerFromDatabase_then_checkHikerIsNull() {
        runBlocking {
            requestShouldFail = true
            val hiker = async {
                try {
                    val hiker = hikerDataRequester.loadHikerFromDatabase(USER_ID)
                    assertEquals("TRUE", "FALSE")
                    hiker
                }
                catch (e: FirebaseException) {
                    println(e.message)
                    null
                }
            }.await()
            assertNull(hiker)
        }
    }

    @Test
    fun given_hikerId_when_loadHikerFromDatabase_then_checkHikerName() {
        runBlocking {
            val hiker = async { hikerDataRequester.loadHikerFromDatabase(USER_ID) }.await()
            assertEquals(USER_NAME, hiker?.name)
        }
    }

    @Test
    fun given_requestFailure_when_saveHikerInDatabase_then_checkHikerIsNotSaved() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    hikerDataRequester.saveHikerInDatabase(getHikerSample(), null, null)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerUpdated)
            assertFalse(isImageDeleted)
            assertFalse(isImageUploaded)
            assertFalse(isUserUpdated)
        }
    }

    @Test
    fun given_nullHiker_when_saveHikerInDatabase_then_checkHikerIsNotSaved() {
        runBlocking {
            launch {
                try {
                    hikerDataRequester.saveHikerInDatabase(null, null, null)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerUpdated)
            assertFalse(isImageDeleted)
            assertFalse(isImageUploaded)
            assertFalse(isUserUpdated)
        }
    }

    @Test
    fun given_hiker_when_saveHikerInDatabase_then_checkHikerIsSaved() {
        runBlocking {
            launch { hikerDataRequester.saveHikerInDatabase(getHikerSample(), null, null) }.join()
            assertTrue(isHikerUpdated)
            assertFalse(isImageDeleted)
            assertFalse(isImageUploaded)
            assertTrue(isUserUpdated)
        }
    }

    @Test
    fun given_hikerWithImagesToDeleteAndUpload_when_saveHikerInDatabase_then_checkHikerIsSavedAndImagesAreDeletedAndUploaded() {
        runBlocking {
            val hiker = getHikerSample()
            launch { hikerDataRequester.saveHikerInDatabase(hiker, PHOTO_URL, PHOTO_URI) }.join()
            assertEquals(PHOTO_URL, hiker?.photoUrl)
            assertTrue(isHikerUpdated)
            assertTrue(isImageDeleted)
            assertTrue(isImageUploaded)
            assertTrue(isUserUpdated)
        }
    }

    @Test
    fun given_requestFailure_when_loginUser_then_checkHikerInfoAreNull() {
        runBlocking {
            requestShouldFail = true
            val hiker = async {
                try {
                    val hiker = hikerDataRequester.loginUser()
                    assertEquals("TRUE", "FALSE")
                    hiker
                }
                catch (e: FirebaseException) {
                    println(e.message)
                    null
                }
            }.await()
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
            assertNull(hiker)
            assertNull(CurrentHikerInfo.currentHiker)
        }
    }

    @Test
    fun given_nullUser_when_loginUser_then_checkHikerInfoAreNull() {
        runBlocking {
            returnUserSampleNull = true
            val hiker = async {
                try {
                    val hiker = hikerDataRequester.loginUser()
                    assertEquals("TRUE", "FALSE")
                    hiker
                }
                catch (e: NullPointerException) {
                    println(e.message)
                    null
                }
            }.await()
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
            assertNull(hiker)
            assertNull(CurrentHikerInfo.currentHiker)
        }
    }

    @Test
    fun given_existingUser_when_loginUser_then_checkHikerInfoAreNotUpdated() {
        runBlocking {
            val hiker = async { hikerDataRequester.loginUser() }.await()
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
            assertEquals(USER_ID, hiker?.id)
            assertEquals(USER_NAME, hiker?.name)
            assertEquals(USER_EMAIL, hiker?.email)
            assertEquals(hiker, CurrentHikerInfo.currentHiker)
        }
    }

    @Test
    fun given_newUser_when_loginUser_then_checkHikerInfoAreUpdated() {
        runBlocking {
            returnHikerSampleNull = true
            val hiker = async { hikerDataRequester.loginUser() }.await()
            assertTrue(isHikerUpdated)
            assertTrue(isHikerHistoryItemAdded)
            assertEquals(USER_ID, hiker?.id)
            assertEquals(USER_NAME, hiker?.name)
            assertEquals(USER_EMAIL, hiker?.email)
            assertEquals(hiker, CurrentHikerInfo.currentHiker)
        }
    }

    @Test
    fun given_nothing_when_logoutUser_then_check_userIsSignedOut() {
        CurrentHikerInfo.currentHiker = getHikerSample()
        hikerDataRequester.logoutUser()
        assertNull(CurrentHikerInfo.currentHiker)
        assertTrue(isUserSignedOut)
    }

    @Test
    fun given_requestFailure_when_resetUserPassword_then_checkUserPasswordIsNotReset() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    hikerDataRequester.resetUserPassword()
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isUserPasswordReset)
        }
    }

    @Test
    fun given_nullUser_when_resetUserPassword_then_checkUserPasswordIsNotReset() {
        runBlocking {
            returnUserSampleNull = true
            launch {
                try {
                    hikerDataRequester.resetUserPassword()
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isUserPasswordReset)
        }
    }

    @Test
    fun given_user_when_resetUserPassword_then_checkUserPasswordNotReset() {
        runBlocking {
            launch { hikerDataRequester.resetUserPassword() }.join()
            assertTrue(isUserPasswordReset)
        }
    }

    @Test
    fun given_requestFailure_when_deleteUserAccount_then_checkNothingHappens() {
        runBlocking {
            requestShouldFail = true
            val hiker = getHikerSample()
            CurrentHikerInfo.currentHiker = hiker
            launch {
                try {
                    hikerDataRequester.deleteUserAccount()
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isImageDeleted)
            assertFalse(isHikerDeleted)
            assertFalse(isUserAccountDeleted)
            assertNotNull(CurrentHikerInfo.currentHiker)
        }
    }

    @Test
    fun given_nullUser_when_deleteUserAccount_then_checkNothingHappens() {
        runBlocking {
            returnUserSampleNull = true
            val hiker = getHikerSample()
            CurrentHikerInfo.currentHiker = hiker
            launch {
                try {
                    hikerDataRequester.deleteUserAccount()
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isImageDeleted)
            assertFalse(isHikerDeleted)
            assertFalse(isUserAccountDeleted)
            assertNotNull(CurrentHikerInfo.currentHiker)
        }
    }

    @Test
    fun given_nullHiker_when_deleteUserAccount_then_checkNothingHappens() {
        runBlocking {
            returnUserSampleNull = true
            launch {
                try {
                    hikerDataRequester.deleteUserAccount()
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isImageDeleted)
            assertFalse(isHikerDeleted)
            assertFalse(isUserAccountDeleted)
            assertNull(CurrentHikerInfo.currentHiker)
        }
    }

    @Test
    fun given_hiker_when_deleteUserAccount_then_checkHikerAndUserAccountAreDeleted() {
        runBlocking {
            hikerSampleHasPhoto = true
            val hiker = getHikerSample()
            hiker?.photoUrl = PHOTO_URL
            CurrentHikerInfo.currentHiker = hiker
            launch { hikerDataRequester.deleteUserAccount() }.join()
            assertTrue(isImageDeleted)
            assertTrue(isHikerDeleted)
            assertTrue(isUserAccountDeleted)
            assertNull(CurrentHikerInfo.currentHiker)
        }
    }

    @Test
    fun given_requestFailure_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    hikerDataRequester.sendMessage(getHikerSample(), MESSAGE_TEXT)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatUpdated)
            assertFalse(isHikerMessageSent)
        }
    }

    @Test
    fun given_nullUser_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            returnUserSampleNull = true
            launch {
                try {
                    hikerDataRequester.sendMessage(getHikerSample(), MESSAGE_TEXT)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatUpdated)
            assertFalse(isHikerMessageSent)
        }
    }

    @Test
    fun given_nullInterlocutor_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            returnHikerSampleNull = true
            launch {
                try {
                    hikerDataRequester.sendMessage(getHikerSample(), MESSAGE_TEXT)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatUpdated)
            assertFalse(isHikerMessageSent)
        }
    }

    @Test
    fun given_emptyText_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            launch {
                try {
                    hikerDataRequester.sendMessage(getHikerSample(), "")
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatUpdated)
            assertFalse(isHikerMessageSent)
        }
    }

    @Test
    fun given_validText_when_sendMessage_then_checkMessageIsSent() {
        runBlocking {
            launch { hikerDataRequester.sendMessage(getHikerSample(), MESSAGE_TEXT) }.join()
            assertTrue(isHikerChatUpdated)
            assertTrue(isHikerMessageSent)
        }
    }

    @Test
    fun given_requestFailure_when_deleteMessage_then_checkMessageIsNotDeleted() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    hikerDataRequester.deleteMessage(getHikerSample(), Message(text = MESSAGE_TEXT, authorId = USER_ID))
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatUpdated)
            assertFalse(isHikerChatDeleted)
            assertFalse(isHikerMessageDeleted)
        }
    }

    @Test
    fun given_nullUser_when_deleteMessage_then_checkMessageIsNotDeleted() {
        runBlocking {
            returnUserSampleNull = true
            launch {
                try {
                    hikerDataRequester.deleteMessage(getHikerSample(), Message(text = MESSAGE_TEXT, authorId = USER_ID))
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatUpdated)
            assertFalse(isHikerChatDeleted)
            assertFalse(isHikerMessageDeleted)
        }
    }

    @Test
    fun given_nullInterlocutor_when_deleteMessage_then_checkMessageIsNotDeleted() {
        runBlocking {
            returnHikerSampleNull = true
            launch {
                try {
                    hikerDataRequester.deleteMessage(getHikerSample(), Message(text = MESSAGE_TEXT, authorId = USER_ID))
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatUpdated)
            assertFalse(isHikerChatDeleted)
            assertFalse(isHikerMessageDeleted)
        }
    }

    @Test
    fun given_messageIsLastChatMessage_when_deleteMessage_then_checkMessageIsDeleted() {
        runBlocking {
            launch { hikerDataRequester.deleteMessage(getHikerSample(), Message(text = MESSAGE_TEXT, authorId = USER_ID)) }.join()
            assertTrue(isHikerChatUpdated)
            assertFalse(isHikerChatDeleted)
            assertTrue(isHikerMessageDeleted)
        }
    }

    @Test
    fun given_messageIsNotChatMessage_when_deleteMessage_then_checkMessageAndCharAreDeleted() {
        runBlocking {
            returnMessageSampleNull
            launch { hikerDataRequester.deleteMessage(getHikerSample(), Message(text = MESSAGE_TEXT, authorId = USER_ID)) }.join()
            assertTrue(isHikerChatUpdated)
            assertFalse(isHikerChatDeleted)
            assertTrue(isHikerMessageDeleted)
        }
    }

    @Test
    fun given_requestFailure_when_deleteChat_then_checkChatIsNotDeleted() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    hikerDataRequester.deleteChat(getHikerSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatDeleted)
        }
    }

    @Test
    fun given_nullUser_when_deleteChat_then_checkChatIsNotDeleted() {
        runBlocking {
            returnUserSampleNull = true
            launch {
                try {
                    hikerDataRequester.deleteChat(getHikerSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatDeleted)
        }
    }

    @Test
    fun given_nullHiker_when_deleteChat_then_checkChatIsNotDeleted() {
        runBlocking {
            returnHikerSampleNull = true
            launch {
                try {
                    hikerDataRequester.deleteChat(getHikerSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isHikerChatDeleted)
        }
    }

    @Test
    fun given_user_when_deleteChat_then_checkChatIsDeleted() {
        runBlocking {
            launch { hikerDataRequester.deleteChat(getHikerSample()) }.join()
            assertTrue(isHikerChatDeleted)
        }
    }
}