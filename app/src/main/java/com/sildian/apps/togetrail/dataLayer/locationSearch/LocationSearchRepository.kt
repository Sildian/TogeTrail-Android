package com.sildian.apps.togetrail.dataLayer.locationSearch

import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.LocationPrediction
import com.sildian.apps.togetrail.common.network.LocationSearchException
import com.sildian.apps.togetrail.common.search.LocationSearchPrecision

interface LocationSearchRepository {
    @Throws(LocationSearchException::class)
    suspend fun findAutocompleteLocationPredictions(
        query: String,
        precision: LocationSearchPrecision,
    ): List<LocationPrediction>
    @Throws(LocationSearchException::class)
    suspend fun findLocationDetails(id: String): Location
}