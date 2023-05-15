package com.sildian.apps.togetrail.repositories.database.hiker.historyItem

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem

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