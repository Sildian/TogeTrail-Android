package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

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
                    AuthRepository(),
                    StorageRepository(),
                    HikerRepository()
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
                    AuthRepository(),
                    StorageRepository(),
                    HikerRepository()
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
                    AuthRepository(),
                    StorageRepository(),
                    HikerRepository()
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
                AuthRepository(),
                StorageRepository(),
                HikerRepository()
            ).execute()
            assertNull(FirebaseSimulator.currentUser)
            assertNull(CurrentHikerInfo.currentHiker)
            assertTrue(FirebaseSimulator.storageUrls.isEmpty())
            assertTrue(FirebaseSimulator.hikers.isEmpty())
        }
    }
}