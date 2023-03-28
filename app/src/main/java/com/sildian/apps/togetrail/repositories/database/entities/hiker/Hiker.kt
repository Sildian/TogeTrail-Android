package com.sildian.apps.togetrail.repositories.database.entities.hiker

import com.sildian.apps.togetrail.common.core.location.Location
import java.util.*

data class Hiker(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val birthday: Date? = null,
    val home: Location? = null,
    val description: String? = null,
    val profileCreationDate: Date? = null,
    val nbTrailsCreated: Int = 0,
    val nbEventsCreated: Int = 0,
    val nbEventsAttended: Int = 0,
)
