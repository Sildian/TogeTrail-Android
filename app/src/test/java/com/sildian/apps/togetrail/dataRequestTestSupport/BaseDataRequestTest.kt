package com.sildian.apps.togetrail.dataRequestTestSupport

import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import org.junit.After
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/*************************************************************************************************
 * Base for all data request tests
 * Use Shadows in order to avoid requests to the server
 * The aim of these tests is to check the business logic, not the queries
 ************************************************************************************************/

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [28],
    shadows = [
        FakeAuthRepository::class,
        FakeStorageRepository::class,
        FakeHikerRepository::class,
        FakeTrailRepository::class,
        FakeEventRepository::class
    ]
)
abstract class BaseDataRequestTest {
    
    @After
    fun finish() {
        CurrentHikerInfo.currentHiker = null
        FirebaseSimulator.reset()
    }
}