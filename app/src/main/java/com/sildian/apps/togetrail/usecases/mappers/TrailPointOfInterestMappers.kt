package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.common.utils.toDate
import com.sildian.apps.togetrail.common.utils.toLocalDateTime
import com.sildian.apps.togetrail.features.entities.trail.TrailPointOfInterestUI
import com.sildian.apps.togetrail.repositories.database.entities.trail.TrailPointOfInterest

fun TrailPointOfInterestUI.toDataModel(): TrailPointOfInterest =
    TrailPointOfInterest(
        number = number,
        position = position,
        registrationTime = registrationTime?.toDate(),
        name = name,
        description = description,
        photoUrl = photoUrl,
    )

@Throws(IllegalStateException::class)
fun TrailPointOfInterest.toUIModel(): TrailPointOfInterestUI {
    val number = number ?: throw IllegalStateException("TrailPointOfInterest number should be provided")
    val position = position ?: throw IllegalStateException("TrailPointOfInterest position should be provided")
    val registrationTime = registrationTime?.toLocalDateTime()
    val name = name ?: throw IllegalStateException("TrailPointOfInterest name should be provided")
    val description = description.orEmpty()
    val photoUrl = photoUrl
    return TrailPointOfInterestUI(
        number = number,
        position = position,
        registrationTime = registrationTime,
        name = name,
        description = description,
        photoUrl = photoUrl,
    )
}