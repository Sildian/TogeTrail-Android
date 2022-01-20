package com.sildian.apps.togetrail.trail.model.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.dataRepository.TrailRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class TrailSaveDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_saveTrail_then_checkTrailIsNotSaved() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            FirebaseSimulator.requestShouldFail = true
            try {
                val trail = Trail(id = "TA", name = "Trail A")
                val dataRequest = TrailSaveDataRequest(
                    trail,
                    null,
                    null,
                    StorageRepository(),
                    HikerRepository(),
                    TrailRepository()
                )
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: FirebaseException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.trails.isEmpty())
            assertEquals(0, CurrentHikerInfo.currentHiker?.nbTrailsCreated)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_nullHiker_when_saveTrail_then_checkTrailIsNotSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = null
                try {
                    val trail = Trail(id = "TA", name = "Trail A")
                    val dataRequest = TrailSaveDataRequest(
                        trail,
                        null,
                        null,
                        StorageRepository(),
                        HikerRepository(),
                        TrailRepository()
                    )
                    dataRequest.execute()
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            assertTrue(FirebaseSimulator.trails.isEmpty())
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_nullTrail_when_saveTrail_then_checkTrailIsNotSaved() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            try {
                val dataRequest = TrailSaveDataRequest(
                    null,
                    null,
                    null,
                    StorageRepository(),
                    HikerRepository(),
                    TrailRepository()
                )
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.trails.isEmpty())
            assertEquals(0, CurrentHikerInfo.currentHiker?.nbTrailsCreated)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_existingTrail_when_saveTrail_then_checkTrailIsSaved() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", nbTrailsCreated = 0))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            FirebaseSimulator.trails.add(Trail("TA", "Trail A"))
            val trail = Trail(id = "TA", name = "Trail A", distance = 50)
            val dataRequest = TrailSaveDataRequest(
                trail,
                null,
                null,
                StorageRepository(),
                HikerRepository(),
                TrailRepository()
            )
            dataRequest.execute()
            val trailFromDb = FirebaseSimulator.trails.firstOrNull { it.id == "TA" }
            assertNotNull(trailFromDb)
            assertEquals(1, FirebaseSimulator.trails.size)
            assertEquals("Trail A", trailFromDb?.name)
            assertEquals(50, trailFromDb?.distance)
            assertEquals(0, CurrentHikerInfo.currentHiker?.nbTrailsCreated)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_newTrail_when_saveTrail_then_checkTrailIsSaved() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            val trail = Trail(name = "Trail New", distance = 50)
            val dataRequest = TrailSaveDataRequest(
                trail,
                null,
                null,
                StorageRepository(),
                HikerRepository(),
                TrailRepository()
            )
            dataRequest.execute()
            val trailFromDb = FirebaseSimulator.trails.firstOrNull { it.id == "TNEW" }
            assertNotNull(trailFromDb)
            assertEquals(1, FirebaseSimulator.trails.size)
            assertEquals("Trail New", trailFromDb?.name)
            assertEquals(50, trailFromDb?.distance)
            assertEquals(1, CurrentHikerInfo.currentHiker?.nbTrailsCreated)
            val historyItem = FirebaseSimulator.hikerHistoryItems["HA"]?.get(0)
            assertEquals(HikerHistoryType.TRAIL_CREATED, historyItem?.type)
        }
    }

    @Test
    fun given_trailWithImages_when_saveTrail_then_checkTrailIsSavedWithImages() {
        runBlocking {
            FirebaseSimulator.storageUrls.add("Old image")
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            FirebaseSimulator.trails.add(Trail("TA", "Trail A"))
            val trail = Trail(id = "TA", name = "Trail A", mainPhotoUrl = "Old image")
            val dataRequest = TrailSaveDataRequest(
                trail,
                "Old image",
                "New image",
                StorageRepository(),
                HikerRepository(),
                TrailRepository()
            )
            dataRequest.execute()
            val trailFromDb = FirebaseSimulator.trails.firstOrNull { it.id == "TA" }
            assertNotNull(trailFromDb)
            assertEquals(1, FirebaseSimulator.trails.size)
            assertEquals("Trail A", trailFromDb?.name)
            assertEquals("New image", trailFromDb?.mainPhotoUrl)
            assertEquals(0, CurrentHikerInfo.currentHiker?.nbTrailsCreated)
            assertFalse(FirebaseSimulator.storageUrls.contains("Old image"))
            assertTrue(FirebaseSimulator.storageUrls.contains("New image"))
        }
    }
}