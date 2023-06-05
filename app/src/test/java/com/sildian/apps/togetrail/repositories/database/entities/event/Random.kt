package com.sildian.apps.togetrail.repositories.database.entities.event

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.geo.nextPosition
import com.sildian.apps.togetrail.common.core.geo.toGeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.utils.nextDate
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.common.utils.nextUrlString
import java.util.*
import kotlin.random.Random

fun Random.nextEvent(
    id: String? = nextString(),
    name: String? = nextString(),
    mainPhotoUrl: String? = nextUrlString(),
    position: GeoLocation? = nextPosition().toGeoLocation(),
    positionHash: String? = nextString(),
    meetingLocation: Location? = nextLocation(),
    startDate: Date? = nextDate(),
    endDate: Date? = nextDate(),
    description: String? = nextString(),
    isCanceled: Boolean? = nextBoolean(),
    creationDate: Date? = nextDate(),
    authorId: String? = nextString(),
    nbHikersRegistered: Int? = nextInt(from = 0, until = 10),
): Event =
    Event(
        id = id,
        name = name,
        mainPhotoUrl = mainPhotoUrl,
        position = position,
        positionHash = positionHash,
        meetingLocation = meetingLocation,
        startDate = startDate,
        endDate = endDate,
        description = description,
        isCanceled = isCanceled,
        creationDate = creationDate,
        authorId = authorId,
        nbHikersRegistered = nbHikersRegistered,
    )

fun Random.nextEventTrail(
    id: String? = null,
): EventTrail =
    EventTrail(id = id)

fun Random.nextEventHiker(
    id: String? = null,
): EventHiker =
    EventHiker(id = id)

fun Random.nextEventsList(itemsCount: Int = nextInt(from = 0, until = 4)): List<Event> =
    List(size = itemsCount) { index ->
        nextEvent(id = index.toString())
    }

fun Random.nextEventTrailsList(itemsCount: Int = nextInt(from = 0, until = 4)): List<EventTrail> =
    List(size = itemsCount) { index ->
        nextEventTrail(id = index.toString())
    }

fun Random.nextEventHikersList(itemsCount: Int = nextInt(from = 0, until = 4)): List<EventHiker> =
    List(size = itemsCount) { index ->
        nextEventHiker(id = index.toString())
    }