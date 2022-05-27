package com.sildian.apps.togetrail.hiker.data.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.firebaseTestSupport.FakeHikerRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HikerLoadDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_loadHiker_then_checkHikerIsNull() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            val hiker: Hiker? = try {
                val dataRequest = HikerLoadDataRequest(dispatcher, "HA", FakeHikerRepository())
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
                dataRequest.data
            } catch (e: FirebaseException) {
                println(e.message)
                null
            }
            assertNull(hiker)
        }
    }

    @Test
    fun given_hikerId_when_loadHiker_then_checkHikerName() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            val dataRequest = HikerLoadDataRequest(dispatcher, "HA", FakeHikerRepository())
            dataRequest.execute()
            val hiker = dataRequest.data
            assertEquals("Hiker A", hiker?.name)
        }
    }
}