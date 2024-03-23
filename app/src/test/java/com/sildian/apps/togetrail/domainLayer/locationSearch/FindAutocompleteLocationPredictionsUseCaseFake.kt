package com.sildian.apps.togetrail.domainLayer.locationSearch

import com.sildian.apps.togetrail.common.core.location.LocationPrediction
import com.sildian.apps.togetrail.common.core.location.nextLocationPredictionsList
import com.sildian.apps.togetrail.common.network.LocationSearchException
import com.sildian.apps.togetrail.common.search.LocationSearchPrecision
import kotlin.random.Random

class FindAutocompleteLocationPredictionsUseCaseFake(
    private val error: LocationSearchException? = null,
    private val locationPredictions: List<LocationPrediction> = Random.nextLocationPredictionsList(),
) : FindAutocompleteLocationPredictionsUseCase {

    override suspend operator fun invoke(
        query: String,
        precision: LocationSearchPrecision
    ): List<LocationPrediction> =
        error?.let { throw it } ?: locationPredictions
}