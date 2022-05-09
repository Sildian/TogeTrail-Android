package com.sildian.apps.togetrail.hiker.data.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.*
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HikerDeleteAccountDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_deleteHikerAccount_then_checkNothingHappens() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", "hikerAPhoto")
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0]
            try {
                HikerDeleteAccountDataRequest(
                    dispatcher,
                    FakeAuthRepository(),
                    FakeStorageRepository(),
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: FirebaseException) {
                println(e.message)
            }
            assertNotNull(FirebaseSimulator.currentUser)
            assertNotNull(CurrentHikerInfo.currentHiker)
            assertFalse(FirebaseSimulator.storageUrls.isEmpty())
            assertFalse(FirebaseSimulator.hikers.isEmpty())
        }
    }

    @Test
    fun given_nullUser_when_deleteHikerAccount_then_checkNothingHappens() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0]
            try {
                HikerDeleteAccountDataRequest(
                    dispatcher,
                    FakeAuthRepository(),
                    FakeStorageRepository(),
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            assertNull(FirebaseSimulator.currentUser)
            assertNotNull(CurrentHikerInfo.currentHiker)
            assertFalse(FirebaseSimulator.storageUrls.isEmpty())
            assertFalse(FirebaseSimulator.hikers.isEmpty())
        }
    }

    @Test
    fun given_nullHiker_when_deleteHikerAccount_then_checkNothingHappens() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", "hikerAPhoto")
            try {
                HikerDeleteAccountDataRequest(
                    dispatcher,
                    FakeAuthRepository(),
                    FakeStorageRepository(),
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            assertNotNull(FirebaseSimulator.currentUser)
            assertNull(CurrentHikerInfo.currentHiker)
            assertFalse(FirebaseSimulator.storageUrls.isEmpty())
            assertFalse(FirebaseSimulator.hikers.isEmpty())
        }
    }

    @Test
    fun given_hiker_when_deleteHikerAccount_then_checkHikerAndUserAccountAreDeleted() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("hikerAPhoto")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", photoUrl = "hikerAPhoto"))
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", "hikerAPhoto")
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0]
            HikerDeleteAccountDataRequest(
                dispatcher,
                FakeAuthRepository(),
                FakeStorageRepository(),
                FakeHikerRepository()
            ).execute()
            assertNull(FirebaseSimulator.currentUser)
            assertNull(CurrentHikerInfo.currentHiker)
            assertTrue(FirebaseSimulator.storageUrls.isEmpty())
            assertTrue(FirebaseSimulator.hikers.isEmpty())
        }
    }
}