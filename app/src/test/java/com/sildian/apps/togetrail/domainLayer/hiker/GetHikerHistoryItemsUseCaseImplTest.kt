package com.sildian.apps.togetrail.domainLayer.hiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.nextHikerHistoryItemsList
import com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem.HikerHistoryItemRepository
import com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem.HikerHistoryItemRepositoryFake
import com.sildian.apps.togetrail.domainLayer.mappers.toUIModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GetHikerHistoryItemsUseCaseImplTest {

    private fun initUseCase(
        hikerHistoryItemRepository: HikerHistoryItemRepository = HikerHistoryItemRepositoryFake(),
    ): GetHikerHistoryItemsUseCase =
        GetHikerHistoryItemsUseCaseImpl(
            hikerHistoryItemRepository = hikerHistoryItemRepository,
        )

    @Test(expected = DatabaseException::class)
    fun `GIVEN DatabaseException error WHEN invoke THEN throws DatabaseException`() = runTest {
        // Given
        val useCase = initUseCase(
            hikerHistoryItemRepository = HikerHistoryItemRepositoryFake(error = DatabaseException.UnknownException()),
        )

        // When
        useCase(hikerId = Random.nextString())
    }

    @Test
    fun `GIVEN historyItems WHEN invoke THEN result is correct historyItemsUI`() = runTest {
        // Given
        val historyItems = Random.nextHikerHistoryItemsList()
        val useCase = initUseCase(
            hikerHistoryItemRepository = HikerHistoryItemRepositoryFake(historyItems = historyItems),
        )

        // When
        val historyItemsUI = useCase(hikerId = Random.nextString())

        // Then
        val expectedResult = historyItems.map { it.toUIModel() }
        assertEquals(expectedResult, historyItemsUI)
    }
}