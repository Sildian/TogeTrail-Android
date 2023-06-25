package com.sildian.apps.togetrail.domainLayer.hiker

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem.HikerHistoryItemRepository
import com.sildian.apps.togetrail.domainLayer.mappers.toUIModel
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import javax.inject.Inject
import kotlin.jvm.Throws

interface GetHikerHistoryItemsUseCase {
    @Throws(DatabaseException::class)
    suspend operator fun invoke(hikerId: String): List<HikerHistoryItemUI>
}

class GetHikerHistoryItemsUseCaseImpl @Inject constructor(
    private val hikerHistoryItemRepository: HikerHistoryItemRepository,
) : GetHikerHistoryItemsUseCase {

    override suspend operator fun invoke(hikerId: String): List<HikerHistoryItemUI> =
        hikerHistoryItemRepository
            .getLastHistoryItems(hikerId = hikerId)
            .map { it.toUIModel() }
}