package com.sildian.apps.togetrail.repositories.database.entities.trail

import com.sildian.apps.togetrail.common.core.geo.Position
import java.util.*

data class TrailPointOfInterest(
    val number: Int? = null,
    val position: Position? = null,
    val registrationTime: Date? = null,
    val name: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
)
