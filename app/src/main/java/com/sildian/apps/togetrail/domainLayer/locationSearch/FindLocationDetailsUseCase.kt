package com.sildian.apps.togetrail.domainLayer.locationSearch

import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.network.LocationSearchException
import com.sildian.apps.togetrail.dataLayer.locationSearch.LocationSearchRepository
import javax.inject.Inject

interface FindLocationDetailsUseCase {
    @Throws(LocationSearchException::class)
    suspend operator fun invoke(locationId: String): Location
}

class FindLocationDetailsUseCaseImpl @Inject constructor(
    private val locationSearchRepository: LocationSearchRepository,
): FindLocationDetailsUseCase {

    override suspend operator fun invoke(locationId: String): Location =
        locationSearchRepository.findLocationDetails(id = locationId)
}