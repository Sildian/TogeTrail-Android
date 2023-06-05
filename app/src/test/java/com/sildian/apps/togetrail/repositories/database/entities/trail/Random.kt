package com.sildian.apps.togetrail.repositories.database.entities.trail

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.*
import com.sildian.apps.togetrail.common.core.geo.*
import com.sildian.apps.togetrail.common.core.location.*
import java.time.Duration
import java.util.Date
import kotlin.random.Random

fun Random.nextTrail(
    id: String? = nextString(),
    name: String? = nextString(),
    mainPhotoUrl: String? = nextUrlString(),
    source: String? = nextString(),
    position: GeoLocation? = nextPosition().toGeoLocation(),
    positionHash: String? = nextString(),
    location: Location? = nextLocation(),
    description: String? = nextString(),
    level: Trail.Level? = Trail.Level.values().random(),
    isLoop: Boolean? = nextBoolean(),
    measures: Trail.Measures? = nextTrailMeasures(),
    creationDate: Date? = nextDate(),
    authorId: String? = nextString(),
    nbLikes: Int? = nextInt(from = 0, until = 10),
): Trail =
    Trail(
        id = id,
        name = name,
        mainPhotoUrl = mainPhotoUrl,
        source = source,
        position = position,
        positionHash = positionHash,
        location = location,
        description = description,
        level = level,
        isLoop = isLoop,
        measures = measures,
        creationDate = creationDate,
        authorId = authorId,
        nbLikes = nbLikes,
    )

fun Random.nextTrailMeasures(
    duration: Duration? = nextDuration(),
    distance: Distance? = nextDistance(),
    ascent: Derivation? = nextDerivation(),
    descent: Derivation? = nextDerivation(),
    maxElevation: Altitude? = nextAltitude(),
    minElevation: Altitude? = nextAltitude(),
): Trail.Measures =
    Trail.Measures(
        duration = duration,
        distance = distance,
        ascent = ascent,
        descent = descent,
        maxElevation = maxElevation,
        minElevation = minElevation,
    )

fun Random.nextTrailPoint(
    number: Int? = nextInt(from = 0, until = 100),
    position: Position? = nextPosition(),
    registrationTime: Date? = nextDate(),
): TrailPoint =
    TrailPoint(
        number = number,
        position = position,
        registrationTime = registrationTime,
    )

fun Random.nextTrailPointOfInterest(
    number: Int? = nextInt(from = 0, until = 100),
    position: Position? = nextPosition(),
    registrationTime: Date? = nextDate(),
    name: String? = nextString(),
    description: String? = nextString(),
    photoUrl: String? = nextUrlString(),
): TrailPointOfInterest =
    TrailPointOfInterest(
        number = number,
        position = position,
        registrationTime = registrationTime,
        name = name,
        description = description,
        photoUrl = photoUrl,
    )

fun Random.nextTrailsList(itemsCount: Int = nextInt(from = 0, until = 4)): List<Trail> =
    List(size = itemsCount) { index ->
        Trail(id = index.toString())
    }

fun Random.nextTrailPointsList(itemsCount: Int = nextInt(from = 0, until = 4)): List<TrailPoint> =
    List(size = itemsCount) { index ->
        TrailPoint(number = index)
    }

fun Random.nextTrailPointsOfInterestList(itemsCount: Int = nextInt(from = 0, until = 4)): List<TrailPointOfInterest> =
    List(size = itemsCount) { index ->
        TrailPointOfInterest(number = index)
    }