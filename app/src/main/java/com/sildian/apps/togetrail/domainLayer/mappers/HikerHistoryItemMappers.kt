package com.sildian.apps.togetrail.domainLayer.mappers

import com.sildian.apps.togetrail.common.utils.toDate
import com.sildian.apps.togetrail.common.utils.toLocalDateTime
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.HikerInfo
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.ItemInfo
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.HikerRegistered
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.TrailCreated
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.EventCreated
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.EventAttended
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Action
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Item
import kotlin.jvm.Throws
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem.Item.Type as ItemType

fun HikerHistoryItemUI.toDataModel(): HikerHistoryItem {
    val date = date.toDate()
    val action = when (this) {
        is HikerRegistered -> Action.HIKER_REGISTERED
        is TrailCreated -> Action.TRAIL_CREATED
        is EventCreated -> Action.EVENT_CREATED
        is EventAttended -> Action.EVENT_ATTENDED
    }
    val itemType = when (this) {
        is HikerRegistered -> ItemType.HIKER
        is TrailCreated -> ItemType.TRAIL
        is EventCreated -> ItemType.EVENT
        is EventAttended -> ItemType.EVENT
    }
    return HikerHistoryItem(
        date = date,
        action = action,
        item = Item(
            id = itemInfo.id,
            type = itemType,
        )
    )
}

@Throws(IllegalStateException::class)
fun HikerHistoryItem.toUIModel(
    hikerInfo: HikerInfo,
    itemInfo: ItemInfo,
): HikerHistoryItemUI {
    val date = date?.toLocalDateTime() ?: throw IllegalStateException("History item date should be provided")
    if (action == null) {
        throw IllegalStateException("History item action should be provided")
    }
    if (item?.id == null) {
        throw IllegalStateException("History item id should be provided")
    }
    if (item.type == null) {
        throw IllegalStateException("History item type should be provided")
    }
    when (action) {
        Action.HIKER_REGISTERED ->
            check(item.type == ItemType.HIKER) {
                "History item action ($action) and item type (${item.type}) should be consistent"
            }
        Action.TRAIL_CREATED ->
            check(item.type == ItemType.TRAIL) {
                "History item action ($action) and item type (${item.type}) should be consistent"
            }
        Action.EVENT_CREATED ->
            check(item.type == ItemType.EVENT) {
                "History item action ($action) and item type (${item.type}) should be consistent"
            }
        Action.EVENT_ATTENDED ->
            check(item.type == ItemType.EVENT) {
                "History item action ($action) and item type (${item.type}) should be consistent"
            }
    }
    return when (action) {
        Action.HIKER_REGISTERED ->
            HikerRegistered(date = date, hikerInfo = hikerInfo)
        Action.TRAIL_CREATED ->
            TrailCreated(date = date, hikerInfo = hikerInfo, itemInfo = itemInfo)
        Action.EVENT_CREATED ->
            EventCreated(date = date, hikerInfo = hikerInfo, itemInfo = itemInfo)
        Action.EVENT_ATTENDED ->
            EventAttended(date = date, hikerInfo = hikerInfo, itemInfo = itemInfo)
    }
}