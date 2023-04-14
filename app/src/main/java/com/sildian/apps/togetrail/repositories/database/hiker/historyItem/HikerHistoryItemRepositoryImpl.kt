package com.sildian.apps.togetrail.repositories.database.hiker.historyItem

import com.sildian.apps.togetrail.common.network.databaseOperation
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem
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

    override suspend fun getHistoryItemsForTypeAndDetailsId(
        hikerId: String,
        type: HikerHistoryItem.Type,
        detailsId: String
    ): List<HikerHistoryItem> =
        databaseOperation {
            databaseService
                .getHistoryItemsForTypeAndDetailsId(
                    hikerId = hikerId,
                    type = type,
                    detailsId = detailsId,
                ).get()
                .await()
                .toObjects(HikerHistoryItem::class.java)
        }

    override suspend fun addHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        databaseOperation {
            databaseService
                .addHistoryItem(hikerId = hikerId, historyItem = historyItem)
                .await()
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