package com.sildian.apps.togetrail.dataRequestTestSupport

import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

/*************************************************************************************************
 * Base for all data request tests
 ************************************************************************************************/

@ExperimentalCoroutinesApi
abstract class BaseDataRequestTest {

    protected val dispatcher = TestCoroutineDispatcher()

    @Before
    fun init() {
        Dispatchers.setMain(this.dispatcher)
    }

    @After
    fun finish() {
        CurrentHikerInfo.currentHiker = null
        FirebaseSimulator.reset()
        Dispatchers.resetMain()
    }
}