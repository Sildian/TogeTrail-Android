package com.sildian.apps.togetrail.trail.ui.map

import android.Manifest
import android.location.Location
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.userLocationTestSupport.BaseUserLocationDataRequestTest
import com.sildian.apps.togetrail.userLocationTestSupport.FakeUserLocationContinuousFinder
import com.sildian.apps.togetrail.userLocationTestSupport.UserLocationSimulator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class TrailRecordExecutorTest: BaseUserLocationDataRequestTest() {

    private fun generateLocation1(): Location =
        Location("location1").apply {
            latitude = 44.713393
            longitude = 4.330099
            altitude = 642.0
        }

    private fun generateLocation2(): Location =
        Location("location2").apply {
            latitude = 44.713392
            longitude = 4.330098
            altitude = 640.0
        }

    private fun generateLocation3(): Location =
        Location("location3").apply {
            latitude = 44.713718
            longitude = 4.330313
            altitude = 654.0
        }

    @Test
    fun given_locationAccessNotGranted_when_fetchUserLocation_then_checkError() {
        runBlocking {
            val trailRecordExecutor = TrailRecordExecutor(
                dispatcher,
                FakeUserLocationContinuousFinder(locationProviderClient)
            )
            trailRecordExecutor.start(1)
            trailRecordExecutor.stop()
            assertTrue(trailRecordExecutor.trailPointsRegistered.value!!.data.isEmpty())
            assertEquals(
                UserLocationException.ErrorCode.ACCESS_NOT_GRANTED,
                (trailRecordExecutor.trailPointsRegistered.value?.error as UserLocationException).errorCode
            )
        }
    }

    @Test
    fun given_locationUnavailable_when_fetchUserLocation_then_checkError() {
        runBlocking {
            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            locationManagerShadow.setLocationEnabled(false)
            val trailRecordExecutor = TrailRecordExecutor(
                dispatcher,
                FakeUserLocationContinuousFinder(locationProviderClient)
            )
            trailRecordExecutor.start(1)
            trailRecordExecutor.stop()
            assertTrue(trailRecordExecutor.trailPointsRegistered.value!!.data.isEmpty())
            assertEquals(
                UserLocationException.ErrorCode.GPS_UNAVAILABLE,
                (trailRecordExecutor.trailPointsRegistered.value?.error as UserLocationException).errorCode
            )
        }
    }

    @Test
    fun given_nullLocation_when_fetchUserLocation_then_checkError() {
        runBlocking {
            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            locationManagerShadow.setLocationEnabled(true)
            UserLocationSimulator.lastLocation = null
            val trailRecordExecutor = TrailRecordExecutor(
                dispatcher,
                FakeUserLocationContinuousFinder(locationProviderClient)
            )
            trailRecordExecutor.start(1)
            trailRecordExecutor.stop()
            assertTrue(trailRecordExecutor.trailPointsRegistered.value!!.data.isEmpty())
            assertEquals(
                UserLocationException.ErrorCode.ERROR_UNKNOWN,
                (trailRecordExecutor.trailPointsRegistered.value?.error as UserLocationException).errorCode
            )
        }
    }

    @Test
    fun given_differentCases_when_fetchUserLocation_then_checkResults() {
        runBlocking {

            val location1 = generateLocation1()
            val location2 = generateLocation2()
            val location3 = generateLocation3()

            val trailRecordExecutor = TrailRecordExecutor(
                dispatcher,
                FakeUserLocationContinuousFinder(locationProviderClient)
            )

            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

            /*Step 1 : record a first location*/
            locationManagerShadow.setLocationEnabled(true)
            UserLocationSimulator.lastLocation = location1
            trailRecordExecutor.start(1)
            trailRecordExecutor.stop()
            assertEquals(location1.latitude, trailRecordExecutor.trailPointsRegistered.value!!.data.last().latitude, 0.0)
            assertEquals(location1.longitude, trailRecordExecutor.trailPointsRegistered.value!!.data.last().longitude, 0.0)
            assertEquals(location1.altitude.toInt(), trailRecordExecutor.trailPointsRegistered.value!!.data.last().elevation)
            assertNull(trailRecordExecutor.trailPointsRegistered.value?.error)

            /*Step 2 : the location is unavailable*/
            locationManagerShadow.setLocationEnabled(false)
            UserLocationSimulator.lastLocation = null
            trailRecordExecutor.start(1)
            trailRecordExecutor.stop()
            assertEquals(location1.latitude, trailRecordExecutor.trailPointsRegistered.value!!.data.last().latitude, 0.0)
            assertEquals(location1.longitude, trailRecordExecutor.trailPointsRegistered.value!!.data.last().longitude, 0.0)
            assertEquals(location1.altitude.toInt(), trailRecordExecutor.trailPointsRegistered.value!!.data.last().elevation)
            assertEquals(
                UserLocationException.ErrorCode.GPS_UNAVAILABLE,
                (trailRecordExecutor.trailPointsRegistered.value?.error as UserLocationException).errorCode
            )

            /*Step 3 : the next location is too closed to be recorded*/
            locationManagerShadow.setLocationEnabled(true)
            UserLocationSimulator.lastLocation = location2
            trailRecordExecutor.start(1)
            trailRecordExecutor.stop()
            assertEquals(location1.latitude, trailRecordExecutor.trailPointsRegistered.value!!.data.last().latitude, 0.0)
            assertEquals(location1.longitude, trailRecordExecutor.trailPointsRegistered.value!!.data.last().longitude, 0.0)
            assertEquals(location1.altitude.toInt(), trailRecordExecutor.trailPointsRegistered.value!!.data.last().elevation)
            assertEquals(
                UserLocationException.ErrorCode.GPS_UNAVAILABLE,
                (trailRecordExecutor.trailPointsRegistered.value?.error as UserLocationException).errorCode
            )

            /*Step 4 : the next location is far enough to be recorded*/
            locationManagerShadow.setLocationEnabled(true)
            UserLocationSimulator.lastLocation = location3
            trailRecordExecutor.start(1)
            trailRecordExecutor.stop()
            assertEquals(location3.latitude, trailRecordExecutor.trailPointsRegistered.value!!.data.last().latitude, 0.0)
            assertEquals(location3.longitude, trailRecordExecutor.trailPointsRegistered.value!!.data.last().longitude, 0.0)
            assertEquals(location3.altitude.toInt(), trailRecordExecutor.trailPointsRegistered.value!!.data.last().elevation)
            assertNull(trailRecordExecutor.trailPointsRegistered.value?.error)
        }
    }
}