package com.sildian.apps.togetrail.repositories.database.hiker.historyItem

import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem
import com.sildian.apps.togetrail.repositories.database.entities.hiker.nextHikerHistoryItemsList
import kotlin.random.Random

class HikerHistoryItemRepositoryFake(
    private val error: DatabaseException? = null,
    private val historyItems: List<HikerHistoryItem> = Random.nextHikerHistoryItemsList(),
) : HikerHistoryItemRepository {

    var addOrUpdateHistoryItemSuccessCount: Int = 0 ; private set
    var deleteHistoryItemSuccessCount: Int = 0 ; private set

    override suspend fun getLastHistoryItems(hikerId: String): List<HikerHistoryItem> =
        error?.let { throw it } ?: historyItems

    override suspend fun getHistoryItemsForActionAndItemId(
        hikerId: String,
        action: HikerHistoryItem.Action,
        itemId: String
    ): List<HikerHistoryItem> =
        error?.let { throw it } ?: historyItems

    override suspend fun addOrUpdateHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        error?.let { throw it } ?: addOrUpdateHistoryItemSuccessCount++
    }

    override suspend fun deleteHistoryItem(hikerId: String, historyItemId: String) {
        error?.let { throw it } ?: deleteHistoryItemSuccessCount++
    }
}