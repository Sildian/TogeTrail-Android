package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.*
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class HikerSaveDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_saveHiker_then_checkHikerIsNotSaved() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            try {
                val hiker = Hiker(id = "HA", name = "Hiker A")
                val dataRequest = HikerSaveDataRequest(
                    dispatcher,
                    hiker,
                    null,
                    null,
                    FakeAuthRepository(),
                    FakeStorageRepository(),
                    FakeHikerRepository()
                )
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: FirebaseException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.hikers.isEmpty())
        }
    }

    @Test
    fun given_nullHiker_when_saveHiker_then_checkHikerIsNotSaved() {
        runBlocking {
            try {
                val dataRequest = HikerSaveDataRequest(
                    dispatcher,
                    null,
                    null,
                    null,
                    FakeAuthRepository(),
                    FakeStorageRepository(),
                    FakeHikerRepository()
                )
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.hikers.isEmpty())
        }
    }

    @Test
    fun given_hiker_when_saveHiker_then_checkHikerIsSaved() {
        runBlocking {
            val hiker = Hiker(id = "HA", name = "Hiker A")
            val dataRequest = HikerSaveDataRequest(
                dispatcher,
                hiker,
                null,
                null,
                FakeAuthRepository(),
                FakeStorageRepository(),
                FakeHikerRepository()
            )
            dataRequest.execute()
            assertNotNull(FirebaseSimulator.hikers.firstOrNull { it.id == "HA" })
        }
    }

    @Test
    fun given_hikerWithImages_when_saveHiker_then_checkHikerIsSavedWithImages() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("Old image")
            val hiker = Hiker(id = "HA", name = "Hiker A")
            val dataRequest = HikerSaveDataRequest(
                dispatcher,
                hiker,
                "Old image",
                "New image",
                FakeAuthRepository(),
                FakeStorageRepository(),
                FakeHikerRepository()
            )
            dataRequest.execute()
            val hikerFromDb = FirebaseSimulator.hikers.firstOrNull { it.id == "HA" }
            assertNotNull(hikerFromDb)
            assertEquals("New image", hikerFromDb?.photoUrl)
            assertFalse(FirebaseSimulator.storageUrls.contains("Old image"))
            assertTrue(FirebaseSimulator.storageUrls.contains("New image"))
        }
    }
}