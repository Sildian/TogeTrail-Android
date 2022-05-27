package com.sildian.apps.togetrail.trail.data.viewModels

import com.sildian.apps.togetrail.dataRequestTestSupport.*
import com.sildian.apps.togetrail.firebaseTestSupport.FakeHikerRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FakeStorageRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FakeTrailRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.trail.data.models.Trail
import io.ticofab.androidgpxparser.parser.GPXParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
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

    @Test
    @Suppress("DEPRECATION")
    fun given_gpxSampleTest1_when_loadTrailFromGpx_then_checkTrailName() {
        runBlocking {
            val context = RuntimeEnvironment.application.applicationContext
            val inputStream = context.assets.open("gpx_sample_test_1")
            val trailViewModel = TrailViewModel(
                dispatcher,
                FakeStorageRepository(),
                FakeHikerRepository(),
                FakeTrailRepository()
            )
            trailViewModel.loadTrailFromGpx(inputStream, GPXParser())
            assertEquals("Test", trailViewModel.data.value?.data?.name)
            assertNull(trailViewModel.data.value?.error)
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun given_closedGpxSampleTest1_when_loadTrailFromGpx_then_checkError() {
        runBlocking {
            val context = RuntimeEnvironment.application.applicationContext
            val inputStream = context.assets.open("gpx_sample_test_1")
            inputStream.close()
            val trailViewModel = TrailViewModel(
                dispatcher,
                FakeStorageRepository(),
                FakeHikerRepository(),
                FakeTrailRepository()
            )
            trailViewModel.loadTrailFromGpx(inputStream, GPXParser())
            assertNull(trailViewModel.data.value?.data)
            assertNotNull(trailViewModel.data.value?.error)
        }
    }
}