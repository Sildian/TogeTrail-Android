package com.sildian.apps.togetrail.hiker.data.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.firebaseTestSupport.FakeAuthRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HikerChangeEmailAddressDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_changeEmailAddress_then_checkNothingHappens() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", "hikerAPhoto")
            try {
                HikerChangeEmailAddressDataRequest(
                    dispatcher,
                    FakeAuthRepository(),
                    "toto@togetrail.com"
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: FirebaseException) {
                println(e.message)
            }
        }
    }

    @Test
    fun given_nullUser_when_changeEmailAddress_then_checkNothingHappens() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            try {
                HikerChangeEmailAddressDataRequest(
                    dispatcher,
                    FakeAuthRepository(),
                    "toto@togetrail.com"
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
        }
    }

    @Test
    fun given_nullEmail_when_changeEmailAddress_then_checkNothingHappens() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", "hikerAPhoto")
            try {
                HikerChangeEmailAddressDataRequest(
                    dispatcher,
                    FakeAuthRepository(),
                    null
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
        }
    }

    @Test
    fun given_invalidEmail_when_changeEmailAddress_then_checkNothingHappens() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", "hikerAPhoto")
            try {
                HikerChangeEmailAddressDataRequest(
                    dispatcher,
                    FakeAuthRepository(),
                    "toto"
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    @Test
    fun given_validEmail_when_changeEmailAddress_then_checkOperationIsSuccessful() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", "hikerAPhoto")
            HikerChangeEmailAddressDataRequest(
                dispatcher,
                FakeAuthRepository(),
                "toto@togetrail.com"
            ).execute()
        }
    }
}