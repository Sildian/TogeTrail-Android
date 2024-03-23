package com.sildian.apps.togetrail.domainLayer.locationSearch

import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.network.LocationSearchException
import kotlin.random.Random

class FindLocationDetailsUseCaseFake(
    private val error: LocationSearchException? = null,
    private val location: Location = Random.nextLocation(),
) : FindLocationDetailsUseCase {

    override suspend operator fun invoke(locationId: String): Location =
        error?.let { throw it } ?: location
}