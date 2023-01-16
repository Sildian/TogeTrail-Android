package com.sildian.apps.togetrail.userLocationTestSupport

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLocationManager

/*************************************************************************************************
 * Base for all data request tests using user location
 ************************************************************************************************/

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(maxSdk = 33)
abstract class BaseUserLocationDataRequestTest: BaseDataRequestTest() {

    protected lateinit var context: Context
    protected lateinit var applicationShadow: ShadowApplication
    protected lateinit var locationManager: LocationManager
    protected lateinit var locationManagerShadow: ShadowLocationManager
    protected lateinit var locationProviderClient: FusedLocationProviderClient

    @Before
    override fun init() {
        super.init()
        this.context = RuntimeEnvironment.application.applicationContext
        this.applicationShadow = Shadows.shadowOf(RuntimeEnvironment.application)
        this.locationManager = this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        this.locationManagerShadow = Shadows.shadowOf(this.locationManager)
        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(this.context)
    }

    @After
    override fun finish() {
        super.finish()
        UserLocationSimulator.lastLocation = null
    }
}