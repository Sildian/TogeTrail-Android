package com.sildian.apps.togetrail.dataLayer.database.entities.hiker

import java.util.*

data class HikerHistoryItem(
    val date: Date? = null,
    val action: Action? = null,
    val item: Item? = null,
) {

    val id: String?
        get() {
            val actionDescription = action?.description ?: return null
            val itemId = item?.id ?: return null
            val timeStamp = date?.time ?: return null
            return actionDescription + "_" + itemId + "_" + timeStamp
        }

    enum class Action(val description: String) {
        HIKER_REGISTERED("hiker_registered"),
        TRAIL_CREATED("trail_created"),
        EVENT_CREATED("event_created"),
        EVENT_ATTENDED("event_attended"),
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
