package com.sildian.apps.togetrail.features.entities.trail

import com.sildian.apps.togetrail.common.core.geo.Altitude
import com.sildian.apps.togetrail.common.core.geo.Derivation
import com.sildian.apps.togetrail.common.core.geo.Distance
import com.sildian.apps.togetrail.common.core.geo.Position
import com.sildian.apps.togetrail.common.core.geo.nextAltitude
import com.sildian.apps.togetrail.common.core.geo.nextDerivation
import com.sildian.apps.togetrail.common.core.geo.nextDistance
import com.sildian.apps.togetrail.common.core.geo.nextPosition
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.core.nextDuration
import com.sildian.apps.togetrail.common.core.nextLocalDateTime
import com.sildian.apps.togetrail.common.core.nextString
import com.sildian.apps.togetrail.common.core.nextUrlString
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random

fun Random.nextTrailUI(
    id: String = nextString(),
    name: String = nextString(),
    mainPhotoUrl: String? = nextUrlString(),
    source: String = nextString(),
    position: Position = nextPosition(),
    location: Location = nextLocation(),
    description: String = nextString(),
    level: TrailUI.Level = TrailUI.Level.values().random(),
    isLoop: Boolean = nextBoolean(),
    measures: TrailUI.Measures = nextTrailUIMeasures(),
    creationDate: LocalDateTime = nextLocalDateTime(),
    updateDate: LocalDateTime = nextLocalDateTime(),
    authorId: String = nextString(),
    isCurrentUserAuthor: Boolean = nextBoolean(),
    nbLikes: Int = nextInt(from = 0, until = 10),
): TrailUI =
    TrailUI(
        id = id,
        name = name,
        mainPhotoUrl = mainPhotoUrl,
        source = source,
        position = position,
        location = location,
        description = description,
        level = level,
        isLoop = isLoop,
        measures = measures,
        creationDate = creationDate,
        updateDate = updateDate,
        authorId = authorId,
        isCurrentUserAuthor = isCurrentUserAuthor,
        nbLikes = nbLikes,
    )

fun Random.nextTrailUIMeasures(
    duration: Duration? = nextDuration(),
    distance: Distance? = nextDistance(),
    ascent: Derivation? = nextDerivation(),
    descent: Derivation? = nextDerivation(),
    maxElevation: Altitude? = nextAltitude(),
    minElevation: Altitude? = nextAltitude(),
): TrailUI.Measures =
    TrailUI.Measures(
        duration = duration,
        distance = distance,
        ascent = ascent,
        descent = descent,
        maxElevation = maxElevation,
        minElevation = minElevation,
    )