package com.sildian.apps.togetrail.trail.model.support

import com.sildian.apps.togetrail.BaseDataRequesterTest
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TrailDataRequesterTest: BaseDataRequesterTest() {

    private lateinit var trailDataRequester: TrailDataRequester

    @Before
    fun init() {
        trailDataRequester = TrailDataRequester()
    }

    @Test
    fun given_trailId_when_loadTrailFromDatabase_then_checkTrailName() {
        runBlocking {
            val trail = async { trailDataRequester.loadTrailFromDatabase(TRAIL_ID) }.await()
            assertEquals(TRAIL_NAME, trail?.name)
        }
    }

    @Test
    fun given_nullHiker_when_saveTrailInDatabase_then_checkTrailIsNotSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = null
            launch {
                try {
                    trailDataRequester.saveTrailInDatabase(
                        getTrailSample(), null, null
                    )
                    assertEquals("TRUE", "FALSE")
                } catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isImageDeleted)
            assertFalse(isImageUploaded)
            assertFalse(isTrailAdded)
            assertFalse(isTrailUpdated)
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_nullTrail_when_saveTrailInDatabase_then_checkTrailIsNotSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch {
                try {
                    trailDataRequester.saveTrailInDatabase(
                        null, null, null
                    )
                    assertEquals("TRUE", "FALSE")
                } catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isImageDeleted)
            assertFalse(isImageUploaded)
            assertFalse(isTrailAdded)
            assertFalse(isTrailUpdated)
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_existingTrail_when_saveTrailInDatabase_then_checkTrailIsSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            val trail = getTrailSample()
            launch {
                trailDataRequester.saveTrailInDatabase(
                    trail, null, null
                )
            }.join()
            assertFalse(isImageDeleted)
            assertFalse(isImageUploaded)
            assertFalse(isTrailAdded)
            assertTrue(isTrailUpdated)
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_newTrail_when_saveTrailInDatabase_then_checkTrailIsSavedAndHikerIsUpdated() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            val trail = getTrailSample()
            trail?.id = null
            launch {
                trailDataRequester.saveTrailInDatabase(
                    trail,  null, null
                )
            }.join()
            assertFalse(isImageDeleted)
            assertFalse(isImageUploaded)
            assertTrue(isTrailAdded)
            assertTrue(isTrailUpdated)
            assertTrue(isHikerUpdated)
            assertTrue(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_trailWithPhoto_when_saveTrailInDatabase_then_checkTrailIsSavedAndPhotosAreDeletedAndUploaded() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            val trail = getTrailSample()
            launch {
                trailDataRequester.saveTrailInDatabase(
                    trail,  PHOTO_URL, PHOTO_URI
                )
            }.join()
            assertTrue(isImageDeleted)
            assertTrue(isImageUploaded)
            assertFalse(isTrailAdded)
            assertTrue(isTrailUpdated)
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
        }
    }
}