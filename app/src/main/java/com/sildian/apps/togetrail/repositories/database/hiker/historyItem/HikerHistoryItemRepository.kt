package com.sildian.apps.togetrail.repositories.database.hiker.historyItem

import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem

interface HikerHistoryItemRepository {

    suspend fun getLastHistoryItems(
        hikerId: String,
    ): List<HikerHistoryItem>

    suspend fun getHistoryItemsForActionAndItemId(
        hikerId: String,
        action: HikerHistoryItem.Action,
        itemId: String,
    ): List<HikerHistoryItem>

    suspend fun addOrUpdateHistoryItem(
        hikerId: String,
        historyItem: HikerHistoryItem,
    )

    suspend fun deleteHistoryItem(
        hikerId: String,
        historyItemId: String,
    )
}