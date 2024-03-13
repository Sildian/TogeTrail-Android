package com.sildian.apps.togetrail.dataLayer.locationSearch

import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.LocationPrediction
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.core.location.nextLocationPredictionsList
import com.sildian.apps.togetrail.common.network.LocationSearchException
import com.sildian.apps.togetrail.common.search.LocationSearchPrecision
import kotlin.random.Random

class LocationSearchRepositoryFake(
    private val error: LocationSearchException? = null,
    private val locationPredictions: List<LocationPrediction> = Random.nextLocationPredictionsList(),
    private val location: Location = Random.nextLocation(),
) : LocationSearchRepository {

    override suspend fun findAutocompleteLocationPredictions(
        query: String,
        precision: LocationSearchPrecision
    ): List<LocationPrediction> =
        error?.let { throw it } ?: locationPredictions

    override suspend fun findLocationDetails(id: String): Location =
        error?.let { throw it } ?: location
}