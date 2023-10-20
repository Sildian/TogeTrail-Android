package com.sildian.apps.togetrail.uiLayer.hikerProfile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sildian.apps.togetrail.common.coroutines.CoroutineTestRule
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.domainLayer.hiker.GetHikerHistoryItemsUseCase
import com.sildian.apps.togetrail.domainLayer.hiker.GetHikerHistoryItemsUseCaseFake
import com.sildian.apps.togetrail.domainLayer.hiker.GetSingleHikerUseCase
import com.sildian.apps.togetrail.domainLayer.hiker.GetSingleHikerUseCaseFake
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextEventAttendedHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextEventCreatedHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerHistoryItemsUIList
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerRegisteredHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextTrailCreatedHistoryItemUI
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class HikerProfileViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private fun initViewModel(
        hikerId: String = Random.nextString(),
        getSingleHikerUseCase: GetSingleHikerUseCase = GetSingleHikerUseCaseFake(),
        getHikerHistoryItemsUseCase: GetHikerHistoryItemsUseCase = GetHikerHistoryItemsUseCaseFake(),
    ): HikerProfileViewModel =
        HikerProfileViewModel(
            savedStateHandle = SavedStateHandle().apply {
                set(HikerProfileActivity.KEY_BUNDLE_HIKER_ID, hikerId)
            },
            coroutineDispatcher = coroutineTestRule.dispatcher,
            getSingleHikerUseCase = getSingleHikerUseCase,
            getHikerHistoryItemsUseCase = getHikerHistoryItemsUseCase,
        )

    @Test
    fun `GIVEN error WHEN loadHiker THEN HikerState is Error`() = runTest {
        // Given
        val error = IllegalStateException()
        val viewModel = initViewModel(
            getSingleHikerUseCase = GetSingleHikerUseCaseFake(error = error)
        )

        // When
        viewModel.loadHiker()

        // Then
        val expectedState = HikerState.Error(e = error)
        viewModel.hikerState.test {
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN hiker WHEN loadHiker THEN HikerState is Result`() = runTest {
        // Given
        val hiker = Random.nextHikerUI()
        val viewModel = initViewModel(
            getSingleHikerUseCase = GetSingleHikerUseCaseFake(hiker = hiker)
        )

        // When
        viewModel.loadHiker()

        // Then
        val expectedState = HikerState.Result(hiker = hiker)
        viewModel.hikerState.test {
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN error WHEN loadHikerHistoryItems THEN HistoryItemsState is Error`() = runTest {
        // Given
        val error = IllegalStateException()
        val viewModel = initViewModel(
            getHikerHistoryItemsUseCase = GetHikerHistoryItemsUseCaseFake(error = error)
        )

        // When
        viewModel.loadHikerHistoryItems()

        // Then
        val expectedState = HikerHistoryItemsState.Error(e = error)
        viewModel.historyItemsState.test {
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN historyItems WHEN loadHikerHistoryItems THEN HistoryItemsState is Result`() = runTest {
        // Given
        val historyItems = Random.nextHikerHistoryItemsUIList()
        val viewModel = initViewModel(
            getHikerHistoryItemsUseCase = GetHikerHistoryItemsUseCaseFake(historyItems = historyItems)
        )

        // When
        viewModel.loadHikerHistoryItems()

        // Then
        val expectedState = HikerHistoryItemsState.Result(historyItems = historyItems)
        viewModel.historyItemsState.test {
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN nothing WHEN onEditMenuButtonClick THEN trigger NavigateToHikerProfileEdit event`() = runTest {
        val viewModel = initViewModel()
        viewModel.navigationEvent.test {
            viewModel.onEditMenuButtonClick()
            assertEquals(HikerProfileNavigationEvent.NavigateToHikerProfileEdit, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN nothing WHEN onConversationMenuButtonClick THEN trigger NavigateToConversation event`() = runTest {
        val hikerId = Random.nextString()
        val viewModel = initViewModel(hikerId = hikerId)
        viewModel.navigationEvent.test {
            viewModel.onConversationMenuButtonClick()
            assertEquals(HikerProfileNavigationEvent.NavigateToConversation(interlocutorId = hikerId), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN HikerRegistered item WHEN onHikerHistoryItemClick THEN trigger no navigation event`() = runTest {
        val viewModel = initViewModel()
        viewModel.navigationEvent.test {
            // Given
            val historyItem = Random.nextHikerRegisteredHistoryItemUI()

            // When
            viewModel.onHikerHistoryItemClick(historyItem = historyItem)

            // Then
            expectNoEvents()
        }
    }

    @Test
    fun `GIVEN TrailCreated item WHEN onHikerHistoryItemClick THEN trigger NavigateToTrail event`() = runTest {
        val viewModel = initViewModel()
        viewModel.navigationEvent.test {
            // Given
            val historyItem = Random.nextTrailCreatedHistoryItemUI()

            // When
            viewModel.onHikerHistoryItemClick(historyItem = historyItem)

            // Then
            val expectedEvent = HikerProfileNavigationEvent.NavigateToTrail(trailId = historyItem.itemInfo.id)
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN EventCreated item WHEN onHikerHistoryItemClick THEN trigger NavigateToEvent event`() = runTest {
        val viewModel = initViewModel()
        viewModel.navigationEvent.test {
            // Given
            val historyItem = Random.nextEventCreatedHistoryItemUI()

            // When
            viewModel.onHikerHistoryItemClick(historyItem = historyItem)

            // Then
            val expectedEvent = HikerProfileNavigationEvent.NavigateToEvent(eventId = historyItem.itemInfo.id)
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN EventAttended item WHEN onHikerHistoryItemClick THEN trigger NavigateToEvent event`() = runTest {
        val viewModel = initViewModel()
        viewModel.navigationEvent.test {
            // Given
            val historyItem = Random.nextEventAttendedHistoryItemUI()

            // When
            viewModel.onHikerHistoryItemClick(historyItem = historyItem)

            // Then
            val expectedEvent = HikerProfileNavigationEvent.NavigateToEvent(eventId = historyItem.itemInfo.id)
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}