package com.sildian.apps.togetrail.dataLayer.database.entities.trail

import com.sildian.apps.togetrail.common.core.geo.Position
import java.util.*

data class TrailPoint(
    val number: Int? = null,
    val position: Position? = null,
    val registrationTime: Date? = null,
)
