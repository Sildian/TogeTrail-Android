package com.sildian.apps.togetrail.domainLayer.locationSearch

import com.sildian.apps.togetrail.common.core.location.LocationPrediction
import com.sildian.apps.togetrail.common.network.LocationSearchException
import com.sildian.apps.togetrail.common.search.LocationSearchPrecision
import com.sildian.apps.togetrail.dataLayer.locationSearch.LocationSearchRepository
import javax.inject.Inject

interface FindAutocompleteLocationPredictionsUseCase {
    @Throws(LocationSearchException::class)
    suspend operator fun invoke(
        query: String,
        precision: LocationSearchPrecision,
    ): List<LocationPrediction>
}

class FindAutoCompleteLocationPredictionsUseCaseImpl @Inject constructor(
    private val locationSearchRepository: LocationSearchRepository,
) : FindAutocompleteLocationPredictionsUseCase {

    override suspend operator fun invoke(
        query: String,
        precision: LocationSearchPrecision
    ): List<LocationPrediction> =
        locationSearchRepository.findAutocompleteLocationPredictions(
            query = query,
            precision = precision,
        )
}