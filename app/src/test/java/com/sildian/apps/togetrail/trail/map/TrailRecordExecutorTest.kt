package com.sildian.apps.togetrail.trail.map

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.sildian.apps.togetrail.UserLocationHelperShadow
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
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
        this.trailRecordExecutor.stop()
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
        this.trailRecordExecutor.start()
        assertTrue(this.trailRecordExecutor.trailPointsRegistered.isEmpty())
        assertEquals(
            UserLocationException.ErrorCode.ACCESS_NOT_GRANTED,
            this.trailRecordExecutor.userLocationFailure?.errorCode
        )
    }

    @Test
    fun given_locationUnavailable_when_fetchUserLocation_then_checkFailureIsRaised() {
        this.applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        this.locationManagerShadow.setLocationEnabled(false)
        this.trailRecordExecutor.start()
        assertTrue(this.trailRecordExecutor.trailPointsRegistered.isEmpty())
        assertEquals(
            UserLocationException.ErrorCode.GPS_UNAVAILABLE,
            this.trailRecordExecutor.userLocationFailure?.errorCode
        )
    }

    @Test
    fun given_nullLocation_when_fetchUserLocation_then_checkFailureIsRaised() {
        this.applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        this.locationManagerShadow.setLocationEnabled(true)
        UserLocationHelperShadow.lastUserLocation = null
        this.trailRecordExecutor.start()
        assertTrue(this.trailRecordExecutor.trailPointsRegistered.isEmpty())
        assertEquals(
            UserLocationException.ErrorCode.ERROR_UNKNOWN,
            this.trailRecordExecutor.userLocationFailure?.errorCode
        )
    }

    @Test
    fun given_differentCases_when_fetchUserLocation_then_checkProcess() {

        this.applicationShadow.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        /*Step 1 : record a first location*/
        this.locationManagerShadow.setLocationEnabled(true)
        UserLocationHelperShadow.lastUserLocation = location1
        this.trailRecordExecutor.start()
        assertEquals(this.location1.latitude, this.trailRecordExecutor.trailPointsRegistered.last().latitude, 0.0)
        assertEquals(this.location1.longitude, this.trailRecordExecutor.trailPointsRegistered.last().longitude, 0.0)
        assertEquals(this.location1.altitude.toInt(), this.trailRecordExecutor.trailPointsRegistered.last().elevation)
        assertNull(this.trailRecordExecutor.userLocationFailure)

        /*Step 2 : the location is unavailable*/
        locationManagerShadow.setLocationEnabled(false)
        UserLocationHelperShadow.lastUserLocation = null
        this.trailRecordExecutor.start()
        assertEquals(this.location1.latitude, this.trailRecordExecutor.trailPointsRegistered.last().latitude, 0.0)
        assertEquals(this.location1.longitude, this.trailRecordExecutor.trailPointsRegistered.last().longitude, 0.0)
        assertEquals(this.location1.altitude.toInt(), this.trailRecordExecutor.trailPointsRegistered.last().elevation)
        assertEquals(UserLocationException.ErrorCode.GPS_UNAVAILABLE, this.trailRecordExecutor.userLocationFailure?.errorCode)

        /*Step 3 : the next location is too closed to be recorded*/
        locationManagerShadow.setLocationEnabled(true)
        UserLocationHelperShadow.lastUserLocation = location2
        this.trailRecordExecutor.start()
        assertEquals(this.location1.latitude, this.trailRecordExecutor.trailPointsRegistered.last().latitude, 0.0)
        assertEquals(this.location1.longitude, this.trailRecordExecutor.trailPointsRegistered.last().longitude, 0.0)
        assertEquals(this.location1.altitude.toInt(), this.trailRecordExecutor.trailPointsRegistered.last().elevation)
        assertNull(this.trailRecordExecutor.userLocationFailure)

        /*Step 4 : the next location is far enough to be recorded*/
        locationManagerShadow.setLocationEnabled(true)
        UserLocationHelperShadow.lastUserLocation = location3
        this.trailRecordExecutor.start()
        assertEquals(this.location3.latitude, this.trailRecordExecutor.trailPointsRegistered.last().latitude, 0.0)
        assertEquals(this.location3.longitude, this.trailRecordExecutor.trailPointsRegistered.last().longitude, 0.0)
        assertEquals(this.location3.altitude.toInt(), this.trailRecordExecutor.trailPointsRegistered.last().elevation)
        assertNull(this.trailRecordExecutor.userLocationFailure)
    }
}