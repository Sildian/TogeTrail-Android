package com.sildian.apps.togetrail.common.core.geo

import kotlin.random.Random

fun Random.nextPosition(
    latitude: Double = nextLatitude(),
    longitude: Double = nextLongitude(),
    altitude: Altitude = nextAltitude(),
): Position =
    Position(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
    )

fun Random.nextDistance(
    meters: Int = nextInt(),
): Distance =
    Distance(meters = meters)

fun Random.nextAltitude(
    meters: Int = nextInt(),
): Altitude =
    Altitude(meters = meters)

fun Random.nextDerivation(
    meters: Int = nextInt(),
): Derivation =
    Derivation(meters = meters)

fun Random.nextLatitude(): Double =
    nextDouble(from = -90.0, until = 91.0)

fun Random.nextLongitude(): Double =
    nextDouble(from = -180.0, until = 181.0)