package com.sildian.apps.togetrail.hiker.data.viewModels

import com.sildian.apps.togetrail.dataRequestTestSupport.*
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HikerViewModelTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_loadHiker_then_checkError() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            val hikerViewModel = HikerViewModel(
                dispatcher,
                FakeAuthRepository(),
                FakeStorageRepository(),
                FakeHikerRepository()
            )
            hikerViewModel.loadHiker("HA")
            assertNull(hikerViewModel.data.value?.data)
            assertNotNull(hikerViewModel.data.value?.error)
        }
    }

    @Test
    fun given_hikerId_when_loadHiker_then_checkHikerName() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            val hikerViewModel = HikerViewModel(
                dispatcher,
                FakeAuthRepository(),
                FakeStorageRepository(),
                FakeHikerRepository()
            )
            hikerViewModel.loadHiker("HA")
            assertEquals("Hiker A", hikerViewModel.data.value?.data?.name)
            assertNull(hikerViewModel.data.value?.error)
        }
    }
}