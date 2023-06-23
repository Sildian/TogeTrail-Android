package com.sildian.apps.togetrail.domainLayer.mappers

import com.sildian.apps.togetrail.common.utils.toDate
import com.sildian.apps.togetrail.common.utils.toLocalDate
import com.sildian.apps.togetrail.common.utils.toLocalDateTime
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerUI
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.Hiker
import java.time.LocalDateTime

fun HikerUI.toDataModel(): Hiker =
    Hiker(
        id = id,
        email = email,
        name = name,
        photoUrl = photoUrl,
        birthday = birthday?.toDate(),
        home = home,
        description = description,
        profileCreationDate = profileCreationDate.toDate(),
        nbTrailsCreated = nbTrailsCreated,
        nbEventsCreated = nbEventsCreated,
        nbEventsAttended = nbEventsAttended,
    )

@Throws(IllegalStateException::class)
fun Hiker.toUIModel(currentUserId: String?): HikerUI {
    val id = id ?: throw IllegalStateException("Hiker id should be provided")
    val email = email ?: throw IllegalStateException("Hiker email should be provided")
    val name = name ?: throw IllegalStateException("Hiker name should be provided")
    val photoUrl = photoUrl
    val birthday = birthday?.toLocalDate()
    val home = home
    val description = description
    val profileCreationDate = profileCreationDate?.toLocalDateTime() ?: LocalDateTime.now()
    val isCurrentUser = currentUserId.isNullOrBlank().not() && currentUserId == id
    val nbTrailsCreated = nbTrailsCreated ?: 0
    val nbEventsCreated = nbEventsCreated ?: 0
    val nbEventsAttended = nbEventsAttended ?: 0
    return HikerUI(
        id = id,
        email = email,
        name = name,
        photoUrl = photoUrl,
        birthday = birthday,
        home = home,
        description = description.orEmpty(),
        profileCreationDate = profileCreationDate,
        isCurrentUser = isCurrentUser,
        nbTrailsCreated = nbTrailsCreated,
        nbEventsCreated = nbEventsCreated,
        nbEventsAttended = nbEventsAttended,
    )
}