package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeAuthRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeHikerRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HikerLoginDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_loginHiker_then_checkHikerInfoAreNull() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", null)
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            val hiker = try {
                val dataRequest = HikerLoginDataRequest(dispatcher, FakeAuthRepository(), FakeHikerRepository())
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
                dataRequest.data
            } catch (e: FirebaseException) {
                println(e.message)
                null
            }
            assertNull(hiker)
            assertNull(CurrentHikerInfo.currentHiker)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_nullUser_when_loginHiker_then_checkHikerInfoAreNull() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            val hiker = try {
                val dataRequest = HikerLoginDataRequest(dispatcher, FakeAuthRepository(), FakeHikerRepository())
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
                dataRequest.data
            } catch (e: NullPointerException) {
                println(e.message)
                null
            }
            assertNull(hiker)
            assertNull(CurrentHikerInfo.currentHiker)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_existingUser_when_loginHiker_then_checkHikerInfo() {
        runBlocking {
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", null)
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            val dataRequest = HikerLoginDataRequest(dispatcher, FakeAuthRepository(), FakeHikerRepository())
            dataRequest.execute()
            val hiker = dataRequest.data
            assertEquals("HA", CurrentHikerInfo.currentHiker?.id)
            assertEquals("Hiker A", CurrentHikerInfo.currentHiker?.name)
            assertEquals("HA", hiker?.id)
            assertEquals("Hiker A", hiker?.name)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_newUser_when_loginHiker_then_checkHikerProfileIsCreated() {
        runBlocking {
            FirebaseSimulator.setCurrentUser("HA", "ha@togetrail.com", "Hiker A", null)
            val dataRequest = HikerLoginDataRequest(dispatcher, FakeAuthRepository(), FakeHikerRepository())
            dataRequest.execute()
            val hiker = dataRequest.data
            val hikerFromDB = FirebaseSimulator.hikers[0]
            assertEquals("HA", CurrentHikerInfo.currentHiker?.id)
            assertEquals("Hiker A", CurrentHikerInfo.currentHiker?.name)
            assertEquals("HA", hiker?.id)
            assertEquals("Hiker A", hiker?.name)
            assertEquals("HA", hikerFromDB.id)
            assertEquals("Hiker A", hikerFromDB.name)
            val historyItem = FirebaseSimulator.hikerHistoryItems["HA"]?.get(0)
            assertEquals(HikerHistoryType.HIKER_REGISTERED, historyItem?.type)
        }
    }
}