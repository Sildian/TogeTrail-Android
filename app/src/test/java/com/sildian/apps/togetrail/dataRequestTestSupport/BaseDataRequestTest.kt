package com.sildian.apps.togetrail.dataRequestTestSupport

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/*************************************************************************************************
 * Base for all data request tests
 ************************************************************************************************/

@ExperimentalCoroutinesApi
abstract class BaseDataRequestTest {

    class TestCoroutineRule(val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()):
        TestWatcher(),
        TestCoroutineScope by TestCoroutineScope(dispatcher) {

        override fun starting(description: Description?) {
            super.starting(description)
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description?) {
            super.finished(description)
            cleanupTestCoroutines()
            Dispatchers.resetMain()
        }
    }

    protected lateinit var dispatcher: TestCoroutineDispatcher

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        this.dispatcher = testCoroutineRule.dispatcher
        Dispatchers.setMain(this.testCoroutineRule.dispatcher)
    }

    @After
    fun finish() {
        CurrentHikerInfo.currentHiker = null
        FirebaseSimulator.reset()
        Dispatchers.resetMain()
    }
}