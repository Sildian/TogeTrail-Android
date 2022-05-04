package com.sildian.apps.togetrail.trail.data.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeTrailRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.trail.data.core.Trail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class TrailLoadDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_loadTrail_then_checkTrailIsNull() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.trails.add(Trail(id = "TA", name = "Trail A"))
            val trail: Trail? = try {
                val dataRequest = TrailLoadDataRequest(dispatcher, "TA", FakeTrailRepository())
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
                dataRequest.data
            } catch (e: FirebaseException) {
                println(e.message)
                null
            }
            assertNull(trail)
        }
    }

    @Test
    fun given_trailId_when_loadTrailFromDatabase_then_checkTrailName() {
        runBlocking {
            FirebaseSimulator.trails.add(Trail(id = "TA", name = "Trail A"))
            val dataRequest = TrailLoadDataRequest(dispatcher, "TA", FakeTrailRepository())
            dataRequest.execute()
            val trail = dataRequest.data
            assertEquals("Trail A", trail?.name)
        }
    }
}