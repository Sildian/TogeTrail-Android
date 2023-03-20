package com.sildian.apps.togetrail.repositories.database.entities.trail

import com.sildian.apps.togetrail.common.core.geo.Position
import java.util.*

data class TrailPoint(
    val number: Int = 0,
    val position: Position? = null,
    val registrationTime: Date? = null,
)
