package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.common.utils.toDate
import com.sildian.apps.togetrail.common.utils.toLocalDateTime
import com.sildian.apps.togetrail.features.entities.trail.TrailPointUI
import com.sildian.apps.togetrail.repositories.database.entities.trail.TrailPoint

fun TrailPointUI.toDataModel(): TrailPoint =
    TrailPoint(
        number = number,
        position = position,
        registrationTime = registrationTime?.toDate(),
    )

@Throws(IllegalStateException::class)
fun TrailPoint.toUIModel(): TrailPointUI {
    val number = number ?: throw IllegalStateException("TrailPoint number should be provided")
    val position = position ?: throw IllegalStateException("TrailPoint position should be provided")
    val registrationTime = registrationTime?.toLocalDateTime()
    return TrailPointUI(
        number = number,
        position = position,
        registrationTime = registrationTime,
    )
}