package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class HikerLoadDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_loadHiker_then_checkHikerIsNull() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            val hiker: Hiker? = try {
                val dataRequest = HikerLoadDataRequest("HA", HikerRepository())
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
            val dataRequest = HikerLoadDataRequest("HA", HikerRepository())
            dataRequest.execute()
            val hiker = dataRequest.data
            assertEquals("Hiker A", hiker?.name)
        }
    }
}