package com.sildian.apps.togetrail.repositories.database.entities.hiker

import com.sildian.apps.togetrail.common.core.location.Location
import java.util.*

data class HikerHistoryItem(
    val date: Date? = null,
    val type: Type? = null,
    val details: Details? = null,
) {
    
    enum class Type {
        HIKER_REGISTERED,
        TRAIL_CREATED,
        EVENT_CREATED,
        EVENT_ATTENDED,
    }

    data class Details(
        val id: String? = null,
        val name: String? = null,
        val photoUrl: String? = null,
        val location: Location? = null,
    )
}
