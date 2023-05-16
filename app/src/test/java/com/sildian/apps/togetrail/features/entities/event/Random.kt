package com.sildian.apps.togetrail.features.entities.event

import com.sildian.apps.togetrail.common.core.geo.Position
import com.sildian.apps.togetrail.common.core.geo.nextPosition
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.core.nextLocalDate
import com.sildian.apps.togetrail.common.core.nextLocalDateTime
import com.sildian.apps.togetrail.common.core.nextString
import com.sildian.apps.togetrail.common.core.nextUrlString
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

fun Random.nextEventUI(
    id: String = nextString(),
    name: String = nextString(),
    mainPhotoUrl: String? = nextUrlString(),
    position: Position = nextPosition(),
    meetingLocation: Location = nextLocation(),
    startDate: LocalDate = nextLocalDate(),
    endDate: LocalDate = nextLocalDate(),
    description: String = nextString(),
    isCanceled: Boolean = nextBoolean(),
    creationDate: LocalDateTime = nextLocalDateTime(),
    authorId: String = nextString(),
    isCurrentUserAuthor: Boolean = nextBoolean(),
    nbHikersRegistered: Int = nextInt(from = 0, until = 10),
): EventUI =
    EventUI(
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