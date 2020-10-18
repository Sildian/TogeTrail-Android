package com.sildian.apps.togetrail.trail.map

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.sildian.apps.togetrail.UserLocationHelperShadow
import com.sildian.apps.togetrail.common.exceptions.UserLocationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLocationManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], shadows = [UserLocationHelperShadow::class])
class TrailRecordExecutorTest {

    private lateinit var context: Context
    private lateinit var applicationShadow: ShadowApplication
    private lateinit var locationManager: LocationManager
    private lateinit var locationManagerShadow: ShadowLocationManager
    private lateinit var trailRecordExecutor: TrailRecordExecutor
    private lateinit var location1: Location
    private lateinit var location2: Location
    private lateinit var location3: Location

    @Before
    @Suppress("DEPRECATION")
    fun init() {
        this.context = RuntimeEnvironment.application.applicationContext
        this.applicationShadow = Shadows.shadowOf(RuntimeEnvironment.application)
        this.locationManager = this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        this.locationManagerShadow = Shadows.shadowOf(this.locationManager)
        this.trailRecordExecutor = TrailRecordExecutor(this.context)
        initLocation1()
        initLocation2()
        initLocation3()
    }

    @After
    fun finish() {
        UserLocationHelperShadow.lastUserLocation = null
    }

    private fun initLocation1() {
        this.location1 = Location("location1")
        this.location1.latitude = 44.713393
        this.location1.longitude = 4.330099
        this.location1.altitude = 642.0
    }

    private fun initLocation2() {
        this.location2 = Location("location2")
        this.location2.latitude = 44.713392
        this.location2.longitude = 4.330098
        this.location2.altitude = 640.0
    }

    private fun initLocation3() {
        this.location3 = Location("location3")
        this.location3.latitude = 44.713718
        this.location3.longitude = 4.330313
        this.location3.altitude = 654.0
    }

    @Test
    fun given_locationAccessNotGranted_when_fetchUserLocation_then_checkFailureIsRaised() {
        runBlocking {
            launch { trailRecordExecutor.fetchUserLocation() }.join()
            assertTrue(trailRecordExecutor.trailPointsRegistered.isEmpty())
            assertEquals(
                UserLocationException.ErrorCode.ACCESS_NOT_GRANTED,
                trailRecordExecutor.userLocationFailure?.errorCode
            )
        }
    }

    @Test
    fun given_locationUnavailable_when_fetchUserLocation_then_checkFailureIsRaised() {
        runBlocking {
            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            locationManagerShadow.setLocationEnabled(false)
            launch { trailRecordExecutor.fetchUserLocation() }.join()
            assertTrue(trailRecordExecutor.trailPointsRegistered.isEmpty())
            assertEquals(
                UserLocationException.ErrorCode.GPS_UNAVAILABLE,
                trailRecordExecutor.userLocationFailure?.errorCode
            )
        }
    }

    @Test
    fun given_nullLocation_when_fetchUserLocation_then_checkFailureIsRaised() {
        runBlocking {
            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            locationManagerShadow.setLocationEnabled(true)
            UserLocationHelperShadow.lastUserLocation = null
            launch { trailRecordExecutor.fetchUserLocation() }.join()
            assertTrue(trailRecordExecutor.trailPointsRegistered.isEmpty())
            assertEquals(
                UserLocationException.ErrorCode.ERROR_UNKNOWN,
                trailRecordExecutor.userLocationFailure?.errorCode
            )
        }
    }

    @Test
    fun given_differentCases_when_fetchUserLocation_then_checkProcess() {
        runBlocking {

            applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

            /*Step 1 : record a first location*/
            locationManagerShadow.setLocationEnabled(true)
            UserLocationHelperShadow.lastUserLocation = location1
            launch { trailRecordExecutor.fetchUserLocation() }.join()
            assertEquals(location1.latitude, trailRecordExecutor.trailPointsRegistered.last().latitude, 0.0)
            assertEquals(location1.longitude, trailRecordExecutor.trailPointsRegistered.last().longitude, 0.0)
            assertEquals(location1.altitude.toInt(), trailRecordExecutor.trailPointsRegistered.last().elevation)
            assertNull(trailRecordExecutor.userLocationFailure)

            /*Step 2 : the location is unavailable*/
            locationManagerShadow.setLocationEnabled(false)
            UserLocationHelperShadow.lastUserLocation = null
            launch { trailRecordExecutor.fetchUserLocation() }.join()
            assertEquals(location1.latitude, trailRecordExecutor.trailPointsRegistered.last().latitude, 0.0)
            assertEquals(location1.longitude, trailRecordExecutor.trailPointsRegistered.last().longitude, 0.0)
            assertEquals(location1.altitude.toInt(), trailRecordExecutor.trailPointsRegistered.last().elevation)
            assertEquals(
                UserLocationException.ErrorCode.GPS_UNAVAILABLE,
                trailRecordExecutor.userLocationFailure?.errorCode
            )

            /*Step 3 : the next location is too closed to be recorded*/
            locationManagerShadow.setLocationEnabled(true)
            UserLocationHelperShadow.lastUserLocation = location2
            launch { trailRecordExecutor.fetchUserLocation() }.join()
            assertEquals(location1.latitude, trailRecordExecutor.trailPointsRegistered.last().latitude, 0.0)
            assertEquals(location1.longitude, trailRecordExecutor.trailPointsRegistered.last().longitude, 0.0)
            assertEquals(location1.altitude.toInt(), trailRecordExecutor.trailPointsRegistered.last().elevation)
            assertNull(trailRecordExecutor.userLocationFailure)

            /*Step 4 : the next location is far enough to be recorded*/
            locationManagerShadow.setLocationEnabled(true)
            UserLocationHelperShadow.lastUserLocation = location3
            launch { trailRecordExecutor.fetchUserLocation() }.join()
            assertEquals(location3.latitude, trailRecordExecutor.trailPointsRegistered.last().latitude, 0.0)
            assertEquals(location3.longitude, trailRecordExecutor.trailPointsRegistered.last().longitude, 0.0)
            assertEquals(location3.altitude.toInt(), trailRecordExecutor.trailPointsRegistered.last().elevation)
            assertNull(trailRecordExecutor.userLocationFailure)
        }
    }
}