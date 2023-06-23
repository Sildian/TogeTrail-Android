package com.sildian.apps.togetrail.dataLayer.database.entities.hiker

import java.util.*

data class HikerHistoryItem(
    val date: Date? = null,
    val action: Action? = null,
    val item: Item? = null,
) {

    val id: String?
        get() {
            val itemTypeDescription = item?.type?.description ?: return null
            val itemId = item.id ?: return null
            val timeStamp = date?.time ?: return null
            return itemTypeDescription + "_" + itemId + "_" + timeStamp
        }

    enum class Action {
        HIKER_REGISTERED,
        TRAIL_CREATED,
        EVENT_CREATED,
        EVENT_ATTENDED,
    }

    data class Item(
        val id: String? = null,
        val type: Type? = null,
    ) {

        enum class Type(val description: String) {
            HIKER("hiker"),
            TRAIL("trail"),
            EVENT("event"),
        }
    }
}
