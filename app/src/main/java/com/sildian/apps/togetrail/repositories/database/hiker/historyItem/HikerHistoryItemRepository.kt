package com.sildian.apps.togetrail.repositories.database.hiker.historyItem

import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem

interface HikerHistoryItemRepository {

    suspend fun getLastHistoryItems(
        hikerId: String,
    ): List<HikerHistoryItem>

    suspend fun getHistoryItemsForTypeAndDetailsId(
        hikerId: String,
        type: HikerHistoryItem.Type,
        detailsId: String,
    ): List<HikerHistoryItem>

    suspend fun addHistoryItem(
        hikerId: String,
        historyItem: HikerHistoryItem,
    )

    suspend fun deleteHistoryItem(
        hikerId: String,
        historyItemId: String,
    )
}