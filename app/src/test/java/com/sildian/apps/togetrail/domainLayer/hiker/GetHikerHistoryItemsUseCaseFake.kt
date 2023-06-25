package com.sildian.apps.togetrail.domainLayer.hiker

import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerHistoryItemsUIList
import kotlin.random.Random

class GetHikerHistoryItemsUseCaseFake(
    private val error: Throwable? = null,
    private val historyItems: List<HikerHistoryItemUI> = Random.nextHikerHistoryItemsUIList(),
) : GetHikerHistoryItemsUseCase {

    override suspend fun invoke(hikerId: String): List<HikerHistoryItemUI> =
        error?.let { throw it } ?: historyItems
}