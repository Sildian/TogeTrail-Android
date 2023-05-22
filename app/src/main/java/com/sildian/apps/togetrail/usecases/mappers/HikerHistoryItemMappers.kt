package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.common.utils.toDate
import com.sildian.apps.togetrail.common.utils.toLocalDateTime
import com.sildian.apps.togetrail.features.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.features.entities.hiker.HikerHistoryItemUI.Action as ActionUI
import com.sildian.apps.togetrail.features.entities.hiker.HikerHistoryItemUI.Item as ItemUI
import com.sildian.apps.togetrail.features.entities.hiker.HikerHistoryItemUI.Item.Type as ItemTypeUI
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem.Action as Action
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem.Item as Item
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem.Item.Type as ItemType

fun HikerHistoryItemUI.toDataModel(): HikerHistoryItem =
    HikerHistoryItem(
        date = date.toDate(),
        action = action.toDataModel(),
        item = item.toDataModel(),
    )

private fun ActionUI.toDataModel(): Action =
    when (this) {
        ActionUI.HIKER_REGISTERED -> Action.HIKER_REGISTERED
        ActionUI.TRAIL_CREATED -> Action.TRAIL_CREATED
        ActionUI.EVENT_CREATED -> Action.EVENT_CREATED
        ActionUI.EVENT_ATTENDED -> Action.EVENT_ATTENDED
    }

private fun ItemUI.toDataModel(): Item =
    Item(
        id = id,
        type = type.toDataModel(),
    )

private fun ItemTypeUI.toDataModel(): ItemType =
    when (this) {
        ItemTypeUI.HIKER -> ItemType.HIKER
        ItemTypeUI.TRAIL -> ItemType.TRAIL
        ItemTypeUI.EVENT -> ItemType.EVENT
    }

@Throws(IllegalStateException::class)
fun HikerHistoryItem.toUIModel(): HikerHistoryItemUI {
    val date = date?.toLocalDateTime() ?: throw IllegalStateException("History item date should be provided")
    val action = action?.toUIModel() ?: throw IllegalStateException("History item action should be provided")
    val item = item?.toUIModel() ?: throw IllegalStateException("History item should be provided")
    return HikerHistoryItemUI(
        date = date,
        action = action,
        item = item,
    )
}

private fun Action.toUIModel(): ActionUI =
    when (this) {
        Action.HIKER_REGISTERED -> ActionUI.HIKER_REGISTERED
        Action.TRAIL_CREATED -> ActionUI.TRAIL_CREATED
        Action.EVENT_CREATED -> ActionUI.EVENT_CREATED
        Action.EVENT_ATTENDED -> ActionUI.EVENT_ATTENDED
    }

private fun Item.toUIModel(): ItemUI {
    val id = id ?: throw IllegalStateException("Item id should be provided")
    val type = type?.toUIModel() ?: throw IllegalStateException("Item type should be provided")
    return ItemUI(
        id = id,
        type = type,
    )
}

private fun ItemType.toUIModel(): ItemTypeUI =
    when (this) {
        ItemType.HIKER -> ItemTypeUI.HIKER
        ItemType.TRAIL -> ItemTypeUI.TRAIL
        ItemType.EVENT -> ItemTypeUI.EVENT
    }