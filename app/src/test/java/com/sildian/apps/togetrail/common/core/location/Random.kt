package com.sildian.apps.togetrail.common.core.location

import com.sildian.apps.togetrail.common.utils.nextAlphaString
import com.sildian.apps.togetrail.common.utils.nextString
import kotlin.random.Random

fun Random.nextLocationPrediction(
    id: String = nextString(),
    name: String = nextString(),
): LocationPrediction =
    LocationPrediction(
        id = id,
        name = name,
    )

fun Random.nextLocation(
    id: String = nextString(),
    country: Location.Country? = nextCountry(),
    region: Location.Region? = nextRegion(),
    fullAddress: String? = nextString(),
): Location =
    Location(
        id = id,
        country = country,
        region = region,
        fullAddress = fullAddress,
    )

fun Random.nextCountry(
    code: String = nextAlphaString(size = 2),
    name: String = nextAlphaString(),
): Location.Country =
    Location.Country(code = code, name = name)

fun Random.nextRegion(
    code: String = nextAlphaString(size = 2),
    name: String = nextAlphaString(),
): Location.Region =
    Location.Region(code = code, name = name)

fun Random.nextLocationPredictionsList(itemsCount: Int = nextInt(from = 1, until = 4)): List<LocationPrediction> =
    List(size = itemsCount) { index ->
        nextLocationPrediction(id = index.toString())
    }