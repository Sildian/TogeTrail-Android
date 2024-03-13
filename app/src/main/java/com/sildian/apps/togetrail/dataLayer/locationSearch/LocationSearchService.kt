package com.sildian.apps.togetrail.dataLayer.locationSearch

import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import javax.inject.Inject

class LocationSearchService @Inject constructor(
    private val placesClient: PlacesClient,
    private val placesAutoCompleteSessionToken: AutocompleteSessionToken,
) {

    fun findAutocompleteLocationPredictions(
        query: String,
        typesFilter: List<String>,
    ): Task<FindAutocompletePredictionsResponse> =
        FindAutocompletePredictionsRequest
            .builder()
            .setSessionToken(placesAutoCompleteSessionToken)
            .setTypesFilter(typesFilter)
            .setQuery(query)
            .build()
            .let { request ->
                placesClient.findAutocompletePredictions(request)
            }

    fun findLocationDetails(
        id: String,
        fields: List<Place.Field>,
    ): Task<FetchPlaceResponse> =
        FetchPlaceRequest
            .builder(id, fields)
            .setSessionToken(placesAutoCompleteSessionToken)
            .build()
            .let { request ->
                placesClient.fetchPlace(request)
            }
}