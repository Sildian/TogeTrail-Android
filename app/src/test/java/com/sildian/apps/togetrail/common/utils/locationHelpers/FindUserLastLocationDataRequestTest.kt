package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.Manifest
import android.location.Location
import com.sildian.apps.togetrail.userLocationTestSupport.BaseUserLocationDataRequestTest
import com.sildian.apps.togetrail.userLocationTestSupport.FakeUserLocationFinder
import com.sildian.apps.togetrail.userLocationTestSupport.UserLocationSimulator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class FindUserLastLocationDataRequestTest: BaseUserLocationDataRequestTest() {

    @Test
    fun given_locationAccessNotGranted_when_findUserLastLocation_then_checkError() {
        runBlocking {
            val dataRequest = FindUserLastLocationDataRequest(
                dispatcher,
                FakeUserLocationFinder(
                    context,
                    locationProviderClient
                )
            )
            val location = try {
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
                dataRequest.data
            } catch (e: UserLocationException) {
                println(e.message)
                null
            }
            assertNull(location)
        }
    }

    @Test
    fun given_locationUnavailable_when_findUserLastLocation_then_checkError() {
        runBlocking {
            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            locationManagerShadow.setLocationEnabled(false)
            val dataRequest = FindUserLastLocationDataRequest(
                dispatcher,
                FakeUserLocationFinder(
                    context,
                    locationProviderClient
                )
            )
            val location = try {
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
                dataRequest.data
            } catch (e: UserLocationException) {
                println(e.message)
                null
            }
            assertNull(location)
        }
    }

    @Test
    fun given_nullLocation_when_findUserLastLocation_then_checkError() {
        runBlocking {
            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            locationManagerShadow.setLocationEnabled(true)
            UserLocationSimulator.lastLocation = null
            val dataRequest = FindUserLastLocationDataRequest(
                dispatcher,
                FakeUserLocationFinder(
                    context,
                    locationProviderClient
                )
            )
            val location = try {
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
                dataRequest.data
            } catch (e: UserLocationException) {
                println(e.message)
                null
            }
            assertNull(location)
        }
    }

    @Test
    fun given_location_when_findUserLastLocation_then_checkLocation() {
        runBlocking {
            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            locationManagerShadow.setLocationEnabled(true)
            UserLocationSimulator.lastLocation = Location("Location").apply {
                latitude = 44.713393
                longitude = 4.330099
                altitude = 642.0
            }
            val dataRequest = FindUserLastLocationDataRequest(
                dispatcher,
                FakeUserLocationFinder(
                    context,
                    locationProviderClient
                )
            )
            dataRequest.execute()
            assertEquals(44.713393, dataRequest.data?.latitude!!, 0.0)
            assertEquals(4.330099, dataRequest.data?.longitude!!, 0.0)
            assertEquals(642.0, dataRequest.data?.altitude!!, 0.0)
        }
    }
}