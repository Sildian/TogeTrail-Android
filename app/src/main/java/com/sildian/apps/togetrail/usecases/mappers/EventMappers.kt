package com.sildian.apps.togetrail.usecases.mappers

import com.sildian.apps.togetrail.common.core.geo.toGeoHash
import com.sildian.apps.togetrail.common.core.geo.toGeoLocation
import com.sildian.apps.togetrail.common.core.geo.toPosition
import com.sildian.apps.togetrail.common.utils.toDate
import com.sildian.apps.togetrail.common.utils.toLocalDate
import com.sildian.apps.togetrail.common.utils.toLocalDateTime
import com.sildian.apps.togetrail.features.entities.event.EventUI
import com.sildian.apps.togetrail.repositories.database.entities.event.Event
import java.time.LocalDateTime
import kotlin.jvm.Throws

fun EventUI.toDataModel(): Event =
    Event(
        id = id,
        name = name,
        mainPhotoUrl = mainPhotoUrl,
        position = position.toGeoLocation(),
        positionHash = position.toGeoHash(),
        meetingLocation = meetingLocation,
        startDate = startDate.toDate(),
        endDate = endDate.toDate(),
        description = description,
        isCanceled = isCanceled,
        creationDate = creationDate.toDate(),
        authorId = authorId,
        nbHikersRegistered = nbHikersRegistered,
    )

@Throws(IllegalStateException::class)
fun Event.toUIModel(currentUserId: String?): EventUI {
    val id = id ?: throw IllegalStateException("Event id should be provided")
    val name = name ?: throw IllegalStateException("Event name should be provided")
    val mainPhotoUrl = mainPhotoUrl
    val position = position?.toPosition() ?: throw IllegalStateException("Event position should be provided")
    val meetingLocation = meetingLocation ?: throw IllegalStateException("Event meeting location should be provided")
    val startDate = startDate?.toLocalDate() ?: throw IllegalStateException("Event start date should be provided")
    val endDate = endDate ?.toLocalDate() ?: throw IllegalStateException("Event end date should be provided")
    val description = description.orEmpty()
    val isCanceled = isCanceled ?: false
    val creationDate = creationDate?.toLocalDateTime() ?: LocalDateTime.now()
    val authorId = authorId.orEmpty()
    val isCurrentUserAuthor = currentUserId.isNullOrBlank().not() && currentUserId == authorId
    val nbHikersRegistered = nbHikersRegistered ?: 0
    return EventUI(
        id = id,
        name = name,
        mainPhotoUrl = mainPhotoUrl,
        position = position,
        meetingLocation = meetingLocation,
        startDate = startDate,
        endDate = endDate,
        description = description,
        isCanceled = isCanceled,
        creationDate = creationDate,
        authorId = authorId,
        isCurrentUserAuthor = isCurrentUserAuthor,
        nbHikersRegistered = nbHikersRegistered,
    )
}
