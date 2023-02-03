package com.sildian.apps.togetrail.common.core.location

import com.sildian.apps.togetrail.common.core.nextAlphaString
import com.sildian.apps.togetrail.common.core.nextString
import kotlin.random.Random

fun Random.nextLocation(
    country: Location.Country? = nextCountry(),
    region: Location.Region? = nextRegion(),
    fullAddress: String? = nextString(),
): Location =
    Location(
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