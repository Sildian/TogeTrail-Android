package com.sildian.apps.togetrail.features.entities.hiker

import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.core.nextEmailAddressString
import com.sildian.apps.togetrail.common.core.nextLocalDate
import com.sildian.apps.togetrail.common.core.nextLocalDateTime
import com.sildian.apps.togetrail.common.core.nextString
import com.sildian.apps.togetrail.common.core.nextUrlString
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

fun Random.nextHikerUI(
    id: String = nextString(),
    email: String = nextEmailAddressString(),
    name: String = nextString(),
    photoUrl: String? = nextUrlString(),
    birthday: LocalDate? = nextLocalDate(),
    home: Location? = nextLocation(),
    description: String? = nextString(),
    profileCreationDate: LocalDateTime = nextLocalDateTime(),
    isCurrentUser: Boolean = nextBoolean(),
    nbTrailsCreated: Int = nextInt(from = 0, until = 10),
    nbEventsCreated: Int = nextInt(from = 0, until = 10),
    nbEventsAttended: Int = nextInt(from = 0, until = 10),
): HikerUI =
    HikerUI(
        id = id,
        email = email,
        name = name,
        photoUrl = photoUrl,
        birthday = birthday,
        home = home,
        description = description,
        profileCreationDate = profileCreationDate,
        isCurrentUser = isCurrentUser,
        nbTrailsCreated = nbTrailsCreated,
        nbEventsCreated = nbEventsCreated,
        nbEventsAttended = nbEventsAttended,
    )