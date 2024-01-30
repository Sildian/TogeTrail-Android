package com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem

import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HikerHistoryItemRepositoryImpl @Inject constructor(
    private val databaseService: HikerHistoryItemDatabaseService,
) : HikerHistoryItemRepository {

    override suspend fun getLastHistoryItems(hikerId: String): List<HikerHistoryItem> =
        databaseOperation {
            databaseService
                .getLastHistoryItems(hikerId = hikerId)
                .get()
                .await()
                .toObjects(HikerHistoryItem::class.java)
        }

    override suspend fun getHistoryItemsForActionAndItemId(
        hikerId: String,
        action: HikerHistoryItem.Action,
        itemId: String
    ): List<HikerHistoryItem> =
        databaseOperation {
            databaseService
                .getHistoryItemsForActionAndItemId(
                    hikerId = hikerId,
                    action = action,
                    itemId = itemId,
                ).get()
                .await()
                .toObjects(HikerHistoryItem::class.java)
        }

    override suspend fun addOrUpdateHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        databaseOperation {
            databaseService
                .addOrUpdateHistoryItem(hikerId = hikerId, historyItem = historyItem)
                ?.await()
        }
    }

    override suspend fun deleteHistoryItem(hikerId: String, historyItemId: String) {
        databaseOperation {
            databaseService
                .deleteHistoryItem(hikerId = hikerId, historyItemId = historyItemId)
                .await()
        }
    }
}