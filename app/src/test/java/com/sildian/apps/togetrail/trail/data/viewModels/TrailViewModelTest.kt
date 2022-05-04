package com.sildian.apps.togetrail.trail.data.viewModels

import com.sildian.apps.togetrail.dataRequestTestSupport.*
import com.sildian.apps.togetrail.trail.data.core.Trail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class TrailViewModelTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_loadTrail_then_checkError() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.trails.add(Trail(id = "TA", name = "Trail A"))
            val trailViewModel = TrailViewModel(
                dispatcher,
                FakeStorageRepository(),
                FakeHikerRepository(),
                FakeTrailRepository()
            )
            trailViewModel.loadTrail("TA")
            assertNull(trailViewModel.data.value?.data)
            assertNotNull(trailViewModel.data.value?.error)
        }
    }

    @Test
    fun given_trailId_when_loadTrailFromDatabase_then_checkTrailName() {
        runBlocking {
            FirebaseSimulator.trails.add(Trail(id = "TA", name = "Trail A"))
            val trailViewModel = TrailViewModel(
                dispatcher,
                FakeStorageRepository(),
                FakeHikerRepository(),
                FakeTrailRepository()
            )
            trailViewModel.loadTrail("TA")
            assertEquals("Trail A", trailViewModel.data.value?.data?.name)
            assertNull(trailViewModel.data.value?.error)
        }
    }
}