package com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem

interface HikerHistoryItemRepository {

    @Throws(DatabaseException::class)
    suspend fun getLastHistoryItems(
        hikerId: String,
    ): List<HikerHistoryItem>

    @Throws(DatabaseException::class)
    suspend fun getHistoryItemsForActionAndItemId(
        hikerId: String,
        action: HikerHistoryItem.Action,
        itemId: String,
    ): List<HikerHistoryItem>

    @Throws(DatabaseException::class)
    suspend fun addOrUpdateHistoryItem(
        hikerId: String,
        historyItem: HikerHistoryItem,
    )

    @Throws(DatabaseException::class)
    suspend fun deleteHistoryItem(
        hikerId: String,
        historyItemId: String,
    )
}