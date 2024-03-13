package com.sildian.apps.togetrail.dataLayer.locationSearch

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.LocationPrediction
import com.sildian.apps.togetrail.common.core.location.toLocation
import com.sildian.apps.togetrail.common.core.location.toLocationPrediction
import com.sildian.apps.togetrail.common.network.locationSearchOperation
import com.sildian.apps.togetrail.common.search.LocationSearchPrecision
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationSearchRepositoryImpl @Inject constructor(
    private val locationSearchService: LocationSearchService,
) : LocationSearchRepository {

    override suspend fun findAutocompleteLocationPredictions(
        query: String,
        precision: LocationSearchPrecision
    ): List<LocationPrediction> =
        locationSearchOperation {
            locationSearchService.findAutocompleteLocationPredictions(
                query = query,
                typesFilter = listOf(
                    when (precision) {
                        LocationSearchPrecision.HIGH -> PlaceTypes.ADDRESS
                        LocationSearchPrecision.LOW -> PlaceTypes.REGIONS
                    }
                )
            ).await()
                .autocompletePredictions
                .map { it.toLocationPrediction() }
        }

    override suspend fun findLocationDetails(id: String): Location =
        locationSearchOperation {
            locationSearchService.findLocationDetails(
                id = id,
                fields = listOf(
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.ADDRESS_COMPONENTS,
                )
            ).await()
                .place
                .toLocation()
        }
}